package it.unibo.sd.beccacino;

public enum ResponseCode {
    OK(200, "Ok"),
    CREATE(400, "Cannot create lobby."),
    LEAVE(401, "Cannot leave lobby."),
    JOIN(402, "Cannot join lobby."),
    START(403, "Cannot start the match."),
    PERMISSION_DENIED(405, "Permission denied.");

    private final int code;
    private final String message;
    
    public int toInteger(ResponseCode responseCode) {
        return this.code;
    }

    @Override
    public String toString() {
        return this.message;
    }

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
