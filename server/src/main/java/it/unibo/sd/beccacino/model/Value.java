package it.unibo.sd.beccacino.model;

public enum Value {
    ASSO(1),
    DUE(2),
    TRE(3),
    QUATTRO(4),
    CINQUE(5),
    SEI(6),
    SETTE(7),
    FANTE(8),
    CAVALLO(9),
    RE(10);

    private final int valueNumber;

    Value(int valueNumber) {
        this.valueNumber = valueNumber;
    }

    public int toInteger() {
        return this.valueNumber;
    }
}
