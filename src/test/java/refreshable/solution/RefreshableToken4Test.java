package refreshable.solution;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.StrictAssertions.assertThat;
import java.util.LinkedList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import org.assertj.core.api.StrictAssertions;
import org.junit.Test;

public class RefreshableToken4Test {

    public RefreshableTokenBreakingAPI<String> refreshableToken() {
        LinkedList<String> tokens = new LinkedList<>();
        tokens.add("token1");
        tokens.add("token2");
        return new RefreshableToken4(tokens::poll);
    }

    public RefreshableTokenBreakingAPI<String> refreshableToken(int millis) {
        LinkedList<String> tokens = new LinkedList<>();
        tokens.add("token1");
        tokens.add("token2");
        return new RefreshableToken4(() -> {
            quietSleep(millis);
            return tokens.poll();
        });
    }

    @Test
    public void should_be_empty_by_default() throws Exception {
        // GIVEN
        RefreshableTokenBreakingAPI<String> reference = refreshableToken();

        // WHEN
        String token = reference.token();

        // THEN
        assertThat(token).isNull();
    }

    @Test
    public void should_set_the_value_on_refresh() throws Exception {
        // GIVEN
        RefreshableTokenBreakingAPI<String> reference = refreshableToken();

        // WHEN
        reference.refresh();

        // THEN
        assertThat(reference.token()).isSameAs("token1");
    }

    @Test
    public void should_skip_refresh_when_epoch_changes() throws Exception {
        // GIVEN
        RefreshableTokenBreakingAPI<String> reference = refreshableToken(100);
        ExecutorService executor = newFixedThreadPool(2);
        CyclicBarrier barrier = new CyclicBarrier(2);

        // WHEN
        executor.submit(() -> {
            String receivedToken = reference.token();
            await(barrier);
            reference.refresh(receivedToken);
        });

        executor.submit(() -> {
            String receivedToken = reference.token();
            await(barrier);
            quietSleep(50);
            reference.refresh(receivedToken);
        });

        executor.shutdown();
        executor.awaitTermination(1, SECONDS);

        // THEN
        StrictAssertions.assertThat(reference.token()).isSameAs("token1");
    }

    @Test
    public void should_not_refresh_when_token_has_changed() throws InterruptedException {
        // GIVEN
        RefreshableTokenBreakingAPI<String> reference = refreshableToken();
        ExecutorService executor = newFixedThreadPool(2);
        CyclicBarrier barrier = new CyclicBarrier(2);

        // WHEN
        executor.submit(() -> {
            String receivedToken = reference.token();
            await(barrier);

            reference.refresh(receivedToken);
            await(barrier);
        });

        executor.submit(() -> {
            String receivedToken = reference.token();
            await(barrier);

            await(barrier);
            reference.refresh(receivedToken);
        });

        executor.shutdown();
        executor.awaitTermination(1, SECONDS);

        // THEN
        assertThat(reference.token()).isSameAs("token1");
    }

    private void await(CyclicBarrier barrier) {
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
    }

    private void quietSleep(int millis) {
        try {
            MILLISECONDS.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}