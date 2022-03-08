package it.unibo.sd.beccacino.model;

public enum Value {
    QUATTRO(1),
    CINQUE(2),
    SEI(3),
    SETTE(4),
    FANTE(5),
    CAVALLO(6),
    RE(7),
    ASSO(8),
    DUE(9),
    TRE(10),;

    private final int valueNumber;

    Value(int valueNumber) {
        this.valueNumber = valueNumber;
    }

    public int toInteger() {
        return this.valueNumber;
    }
}
