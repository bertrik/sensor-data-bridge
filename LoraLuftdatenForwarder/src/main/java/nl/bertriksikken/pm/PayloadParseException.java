package nl.bertriksikken.pm;

public final class PayloadParseException extends Exception {

    private static final long serialVersionUID = 700134681969926572L;

    public PayloadParseException(String message) {
        super(message);
    }
    
    public PayloadParseException(Throwable cause) {
        super(cause);
    }
    
}
