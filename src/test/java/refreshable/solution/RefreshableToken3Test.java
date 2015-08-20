package refreshable.solution;

import refreshable.RefreshableToken;

public class RefreshableToken3Test extends RefreshableTokenTest {

    @Override
    public RefreshableToken<String> refreshableToken() {
        return new RefreshableToken3();
    }
}
