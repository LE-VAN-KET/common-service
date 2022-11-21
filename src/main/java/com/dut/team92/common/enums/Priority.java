package com.dut.team92.common.enums;

public enum Priority {
    LOWEST(0), LOW(1), MEDIUM(2), HIGH(3), HIGHEST(4);

    private final int value;

    Priority(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
