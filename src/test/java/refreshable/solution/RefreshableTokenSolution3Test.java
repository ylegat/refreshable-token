package refreshable.solution;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.StrictAssertions.assertThat;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import org.assertj.core.api.StrictAssertions;
import org.junit.Test;

public class RefreshableTokenSolution3Test extends AbstractRefreshableTokenBreakingAPITest<String> {

    public RefreshableTokenBreakingAPI<String> refreshableToken() {
        LinkedList<String> tokens = new LinkedList<>();
        tokens.add("token1");
        tokens.add("token2");
        return new RefreshableTokenSolution3(tokens::poll);
    }

    public RefreshableTokenBreakingAPI<String> refreshableToken(int millis) {
        LinkedList<String> tokens = new LinkedList<>();
        tokens.add("token1");
        tokens.add("token2");
        return new RefreshableTokenSolution3(() -> {
            quietSleep(millis);
            return tokens.poll();
        });
    }

    @Override
    public boolean isTokenSameAs(String token, String expectation) {
        return Objects.equals(token, expectation);
    }
}