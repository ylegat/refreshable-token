package refreshable.solution;

import java.util.LinkedList;
import java.util.Objects;
import refreshable.solution.RefreshableTokenSolution4.Reference;

public class RefreshableTokenSolution4Test extends AbstractRefreshableTokenBreakingAPITest<Reference<String>> {

    @Override
    public RefreshableTokenBreakingAPI<Reference<String>> refreshableToken() {
        LinkedList<String> tokens = new LinkedList<>();
        tokens.add("token1");
        tokens.add("token2");
        return new RefreshableTokenSolution4(tokens::poll);
    }

    @Override
    public RefreshableTokenBreakingAPI<Reference<String>> refreshableToken(int millis) {
        LinkedList<String> tokens = new LinkedList<>();
        tokens.add("token1");
        tokens.add("token2");
        return new RefreshableTokenSolution4(() -> {
            quietSleep(millis);
            return tokens.poll();
        });
    }

    @Override
    public boolean isTokenSameAs(Reference<String> token, String expectation) {
        return Objects.equals(token.value, expectation);
    }
}