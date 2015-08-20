package refreshable;

import java.util.function.Supplier;

public interface RefreshableToken<T> {

    T token();

    T refresh(Supplier<T> tokenSupplier);
}
