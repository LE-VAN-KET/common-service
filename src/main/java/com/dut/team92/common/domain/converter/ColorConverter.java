package com.dut.team92.common.domain.converter;

import com.dut.team92.common.enums.Color;

import javax.persistence.AttributeConverter;

public class ColorConverter implements AttributeConverter<Color, String> {
    @Override
    public String convertToDatabaseColumn(Color color) {
        return color.getCode();
    }

    @Override
    public Color convertToEntityAttribute(String dbData) {
        for (Color color : Color.values()) {
            if (color.getCode().equals(dbData)) {
                return color;
            }
        }

        throw new IllegalArgumentException("Unknown database value:" + dbData);
    }
}
