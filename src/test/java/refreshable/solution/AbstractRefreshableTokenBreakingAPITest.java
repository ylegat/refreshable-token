package refreshable.solution;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.StrictAssertions.assertThat;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import org.junit.Test;

public abstract class AbstractRefreshableTokenBreakingAPITest<T> {

    public abstract RefreshableTokenBreakingAPI<T> refreshableToken();

    public abstract RefreshableTokenBreakingAPI<T> refreshableToken(int millis);

    public abstract boolean isTokenSameAs(T token, String expectation);

    @Test
    public void should_be_empty_by_default() throws Exception {
        // GIVEN
        RefreshableTokenBreakingAPI<T> reference = refreshableToken();

        // WHEN
        T token = reference.token();

        // THEN
        assertThat(token).isNull();
    }

    @Test
    public void should_set_the_value_on_refresh() throws Exception {
        // GIVEN
        RefreshableTokenBreakingAPI<T> reference = refreshableToken();

        // WHEN
        reference.refresh();

        // THEN
        assertThat(isTokenSameAs(reference.token(), "token1")).isTrue();
    }

    @Test
    public void should_skip_refresh_when_epoch_changes() throws Exception {
        // GIVEN
        RefreshableTokenBreakingAPI<T> reference = refreshableToken(100);
        ExecutorService executor = newFixedThreadPool(2);
        CyclicBarrier barrier = new CyclicBarrier(2);

        // WHEN
        executor.submit(() -> {
            T receivedToken = reference.token();
            await(barrier);
            reference.refresh(receivedToken);
        });

        executor.submit(() -> {
            T receivedToken = reference.token();
            await(barrier);
            quietSleep(50);
            reference.refresh(receivedToken);
        });

        executor.shutdown();
        executor.awaitTermination(1, SECONDS);

        // THEN
        assertThat(isTokenSameAs(reference.token(), "token1")).isTrue();
    }

    @Test
    public void should_not_refresh_when_token_has_changed() throws InterruptedException {
        // GIVEN
        RefreshableTokenBreakingAPI<T> reference = refreshableToken();
        ExecutorService executor = newFixedThreadPool(2);
        CyclicBarrier barrier = new CyclicBarrier(2);

        // WHEN
        executor.submit(() -> {
            T receivedToken = reference.token();
            await(barrier);

            reference.refresh(receivedToken);
            await(barrier);
        });

        executor.submit(() -> {
            T receivedToken = reference.token();
            await(barrier);

            await(barrier);
            reference.refresh(receivedToken);
        });

        executor.shutdown();
        executor.awaitTermination(1, SECONDS);

        // THEN
        assertThat(isTokenSameAs(reference.token(), "token1")).isTrue();
    }

    protected void await(CyclicBarrier barrier) {
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
    }

    protected void quietSleep(int millis) {
        try {
            MILLISECONDS.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
