package com.dut.team92.common.enums;

public enum ProjectStatus {
    ACTIVE(0), BLOCKED(1), DELETE(2), HIDDEN(3), SHOW(4);

    private final int value;

    ProjectStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
