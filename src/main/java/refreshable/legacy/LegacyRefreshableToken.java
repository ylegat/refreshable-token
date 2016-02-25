package refreshable.legacy;

import java.util.function.Supplier;

public interface LegacyRefreshableToken<T> {

    T token();

    T refresh(Supplier<T> tokenSupplier);
}
