package org.example.pojo;

public enum CurrencyType {
    BYN, RUB, EUR;

    public static CurrencyType getTypeByValue(String value) throws IllegalArgumentException{
        for (CurrencyType type: CurrencyType.values()) {
            if (value.equalsIgnoreCase(type.name())) {
                return type;
            }
        }
        throw new IllegalArgumentException("Not valid extension file");
    }
}
