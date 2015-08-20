package refreshable.solution;

import java.util.Objects;
import java.util.function.Supplier;

public class RefreshableToken4 implements RefreshableTokenBreakingAPI<String> {

    private final Object monitor = new Object();

    private final Supplier<String> tokenSupplier;

    private volatile String value;

    public RefreshableToken4(Supplier<String> tokenSupplier) {
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
