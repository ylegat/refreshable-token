package refreshable.solution;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * This solution break the legacy API by asking for the previous token to be communicating when asking for a refresh.
 * It has a flaw though : if the new generated token has the same value than the previous one, a refresh will still be triggered.
 */
public class RefreshableTokenSolution3 implements RefreshableTokenBreakingAPI<String> {

    private final Object monitor = new Object();

    private final Supplier<String> tokenSupplier;

    private volatile String value;

    public RefreshableTokenSolution3(Supplier<String> tokenSupplier) {
        this.tokenSupplier = tokenSupplier;
    }

    @Override
    public String token() {
        synchronized (monitor) {
            return value;
        }
    }

    @Override
    public String refresh() {
        return refresh(null);
    }

    @Override
    public String refresh(String previousToken) {
        synchronized (monitor) {
            if (isSameTokenThenPrevious(previousToken)) {
                value = tokenSupplier.get();
            }

            return value;
        }
    }

    private boolean isSameTokenThenPrevious(String previousToken) {
        return Objects.equals(previousToken, value);
    }
}
