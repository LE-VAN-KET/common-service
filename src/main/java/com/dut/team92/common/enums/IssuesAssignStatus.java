package com.dut.team92.common.enums;

public enum IssuesAssignStatus {
    ACTIVE(0), BLOCKED(1);

    private final int value;

    IssuesAssignStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
