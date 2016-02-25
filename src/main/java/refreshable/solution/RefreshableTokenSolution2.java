package refreshable.solution;

import java.util.Objects;
import java.util.function.Supplier;
import refreshable.legacy.LegacyRefreshableToken;

/**
 * This solution store inside a thread local the value of the last token read for every thread.
 * It has a flaw though : if the new generated token has the same value than the previous one, a refresh will still be triggered.
 */
public class RefreshableTokenSolution2 implements LegacyRefreshableToken<String> {

    private final Object monitor = new Object();

    private final ThreadLocal<String> lastValue = new ThreadLocal<>();

    private volatile String value;

    @Override
    public String token() {
        synchronized (monitor) {
            lastValue.set(value);
            return value;
        }
    }

    @Override
    public String refresh(Supplier<String> tokenSupplier) {
        synchronized (monitor) {
            if (isSameTokenSinceLastRead()) {
                value = tokenSupplier.get();
            }

            return token();
        }
    }

    private boolean isSameTokenSinceLastRead() {
        return Objects.equals(this.lastValue.get(), value);
    }
}
