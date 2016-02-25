package refreshable.solution;

import refreshable.legacy.LegacyRefreshableToken;

public class RefreshableTokenSolution1Test extends AbstractLegacyRefreshableTokenTest {

    @Override
    public LegacyRefreshableToken<String> refreshableToken() {
        return new RefreshableTokenSolution1();
    }
}
