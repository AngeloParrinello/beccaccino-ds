package it.unibo.sd.beccacino;

public enum ResponseCode {
    OK(200, "Ok"),
    CREATE(400, "Cannot create lobby"),
    LEAVE(401, "Cannot leave lobby"),
    JOIN(402, "Cannot join lobby");

    ResponseCode(int code, String message) {}
}
