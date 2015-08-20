package refreshable.solution;

import java.util.Objects;
import java.util.function.Supplier;
import refreshable.RefreshableToken;

public class RefreshableToken3 implements RefreshableToken<String> {

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
