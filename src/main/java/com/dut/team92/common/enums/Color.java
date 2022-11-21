package com.dut.team92.common.enums;

public enum Color {
    YELLOW("#FFFF00"), RED("#FF0000"), GREEN("#008000"), BLUE("#0000FF"),
    GRAY("#808080"), CYAN("#00FFFF"), BLACK("#000000"), MAGENTA("#FF00FF"),
    LIME("#00FF00");

    private final String code;

    Color(String colorCode) {
        this.code = colorCode;
    }

    public String getCode() {
        return this.code;
    }
}
