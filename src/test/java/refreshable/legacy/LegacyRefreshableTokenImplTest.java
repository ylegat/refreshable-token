package refreshable.legacy;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.StrictAssertions.assertThat;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import org.junit.Test;

public class LegacyRefreshableTokenImplTest {

    @Test
    public void should_be_empty_by_default() throws Exception {
        // GIVEN
        LegacyRefreshableToken<String> reference = refreshableToken();

        // WHEN
        String token = reference.token();

        // THEN
        assertThat(token).isNull();
    }

    @Test
    public void should_set_the_value_on_refresh() throws Exception {
        // GIVEN
        LegacyRefreshableToken<String> reference = refreshableToken();
        String nextToken = "nextToken";

        // WHEN
        reference.refresh(() -> nextToken);

        // THEN
        assertThat(reference.token()).isSameAs(nextToken);
    }

    @Test
    public void should_skip_refresh_when_epoch_changes() throws Exception {
        // GIVEN
        LegacyRefreshableToken<String> reference = refreshableToken();
        ExecutorService executor = newFixedThreadPool(2);
        CyclicBarrier barrier = new CyclicBarrier(2);
        String token = "token";

        // WHEN
        executor.submit(() -> {
            reference.token();
            quietAwait(barrier);
            reference.refresh(() -> {
                quietSleep(100);
                return token;
            });
        });

        executor.submit(() -> {
            reference.token();
            quietAwait(barrier);
            quietSleep(50);
            reference.refresh(() -> "should_not_be_fetched");
        });

        executor.shutdown();
        executor.awaitTermination(1, SECONDS);

        // THEN
        assertThat(reference.token()).isSameAs(token);
    }

    private LegacyRefreshableToken<String> refreshableToken() {
        return new LegacyRefreshableTokenImpl();
    }

    private void quietAwait(CyclicBarrier barrier) {
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
