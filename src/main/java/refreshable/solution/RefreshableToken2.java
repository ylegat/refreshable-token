package refreshable.solution;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import refreshable.RefreshableToken;

public class RefreshableToken2 implements RefreshableToken<String> {

    private final AtomicLong epoch = new AtomicLong();

    private final ThreadLocal<Long> readEpoch = ThreadLocal.withInitial(epoch::incrementAndGet);

    private long writeEpoch;

    private volatile String value;

    @Override
    public String token() {
        synchronized (epoch) {
            readEpoch.set(epoch.incrementAndGet());
            return value;
        }
    }

    @Override
    public String refresh(Supplier<String> tokenSupplier) {
        synchronized (epoch) {
            if (isSameTokenSinceLastRead()) {
                value = tokenSupplier.get();
                writeEpoch = epoch.incrementAndGet();
            }
        }

        return token();
    }

    private boolean isSameTokenSinceLastRead() {
        return this.readEpoch.get() > writeEpoch;
    }
}
