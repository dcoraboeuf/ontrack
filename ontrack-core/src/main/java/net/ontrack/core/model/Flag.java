package net.ontrack.core.model;

public class Flag {

    public static final Flag SET = new Flag(true);
    public static final Flag UNSET = new Flag(false);

    private final boolean set;

    private Flag(boolean set) {
        this.set = set;
    }

    public boolean isSet() {
        return set;
    }
}
