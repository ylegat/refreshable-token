package refreshable.solution;

import java.util.function.Supplier;

/**
 * This solution break the legacy API by asking for the previous token to be communicating when asking for a refresh.
 * The token is now wrapped inside a value class called Reference.
 */
public class RefreshableTokenSolution4 implements RefreshableTokenBreakingAPI<RefreshableTokenSolution4.Reference<String>> {

    private Reference<String> reference;

    private final Supplier<String> tokenSupplier;

    private final Object monitor = new Object();

    public RefreshableTokenSolution4(Supplier<String> tokenSupplier) {
        this.tokenSupplier = tokenSupplier;
    }

    @Override
    public Reference<String> token() {
        synchronized (monitor) {
            return reference;
        }
    }

    @Override
    public Reference<String> refresh() {
        return refresh(null);
    }

    @Override
    public Reference<String> refresh(Reference<String> previousReference) {
        synchronized (monitor) {
            if (reference == previousReference) {
                reference = new Reference<>(tokenSupplier.get());
            }

            return reference;
        }
    }

    public static class Reference<T> {

        public final T value;

        private Reference(T value) {
            this.value = value;
        }
    }

}
