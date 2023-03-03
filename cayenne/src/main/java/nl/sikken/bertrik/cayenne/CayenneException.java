package nl.sikken.bertrik.cayenne;

/**
 * Cayenne parsing exception.
 */
public class CayenneException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     *
     * @param message the exception message
     */
    public CayenneException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param e the throwable
     */
    public CayenneException(Throwable e) {
        super(e);
    }

}
