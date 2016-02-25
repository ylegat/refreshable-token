package refreshable.solution;

import refreshable.legacy.LegacyRefreshableToken;

public class RefreshableTokenSolution2Test extends AbstractLegacyRefreshableTokenTest {

    @Override
    public LegacyRefreshableToken<String> refreshableToken() {
        return new RefreshableTokenSolution2();
    }
}
