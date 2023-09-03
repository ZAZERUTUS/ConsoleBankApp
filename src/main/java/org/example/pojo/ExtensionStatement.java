package org.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public enum ExtensionStatement {
    PDF, TXT, ALL_FORMAT;


    public static ExtensionStatement getTypeByValue(String value) throws IllegalArgumentException{
        for (ExtensionStatement type: ExtensionStatement.values()) {
            if (value.equalsIgnoreCase(type.name())) {
                return type;
            }
        }
        return ALL_FORMAT;
    }
}
