package refreshable.solution;

import refreshable.RefreshableToken;

public class RefreshableToken2Test extends RefreshableTokenTest {

    @Override
    public RefreshableToken<String> refreshableToken() {
        return new RefreshableToken2();
    }
}
