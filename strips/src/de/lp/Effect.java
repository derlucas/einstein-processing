package de.lp;

public enum Effect {
    NONE(-1),
    TRIAL1(1),
    TRIAL2(2),
    TRIAL3(3),
    KNEE3(5),
    TRIALPRI(6),
    TESTMODE(7),
    KNEE3ON(8),
    DANCE2(9),
    KNEE4(10),
    BUILDING(11),
    RGB(12);

    private int value;

    Effect(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Effect from(float value) {
        int intVal = (int)value;
        for(Effect v: Effect.values()) {
            if (v.getValue() == intVal) {
                return v;
            }
        }
        return null;
    }
}
