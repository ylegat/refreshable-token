package refreshable.legacy;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import refreshable.RefreshableToken;

/**
 * A holder to a reference that can be thread safely refreshed (reloaded).
 * The reference is guarded y a lock.
 * <p>
 * The reference also has an epoch counter which is incremented every time it is refreshed.
 * Before taking the lock to refresh, the thread will read the current epoch counter.
 * <p>
 * When successfully locking the lock, the thread will compare its reading of the epoch counter to the current value.
 * <ul>
 * <li>if they are the same, no other thread refreshed in between, so it can proceed</li>
 * <li>if they are different, another thread refreshed in between, so skip this refresh</li>
 * </ul>
 */
public class RefreshableToken1 implements RefreshableToken<String> {

    private String value;

    private final AtomicLong epoch = new AtomicLong();
    private final Lock lock = new ReentrantLock();

    @Override
    public String token() {
        lock.lock();
        try {
            return value;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String refresh(Supplier<String> tokenSupplier) {
        long startEpoch = epoch.get();
        lock.lock();
        try {
            if(epoch.get() == startEpoch) {
                // we got the lock when expected, so proceed to refresh
                value = tokenSupplier.get();
                epoch.incrementAndGet();
            }
            // else another thread executed a refresh, just return, the value has already been changed
            return value;
        } finally {
            lock.unlock();
        }
    }
}
