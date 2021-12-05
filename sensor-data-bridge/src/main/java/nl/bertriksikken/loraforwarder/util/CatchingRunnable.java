package nl.bertriksikken.loraforwarder.util;

import org.slf4j.Logger;

/**
 * A wrapper around Runnable that catches exceptions and just logs them.
 */
public final class CatchingRunnable implements Runnable {

    private final Logger logger;
    private final Runnable delegate;

    public CatchingRunnable(Logger logger, Runnable runnable) {
        this.logger = logger;
        this.delegate = runnable;
    }
    
    @Override
    public void run() {
        try {
            delegate.run();
        } catch (Throwable e) {
            logger.error("Caught unhandled exception", e);
        }
    }
    
    @Override
    public String toString() {
        return delegate.toString();
    }
    
}
