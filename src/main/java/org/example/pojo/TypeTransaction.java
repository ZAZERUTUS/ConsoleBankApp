package org.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TypeTransaction {
    CASH_IN("Пополнение"), CASH_OUT("Списание"), TRANSFER("Перевод");

    String type;

    public static TypeTransaction getTypeByValue(String value) throws IllegalArgumentException{
        for (TypeTransaction type: TypeTransaction.values()) {
            if (value.toLowerCase().equals(type.getType().toLowerCase()) || type.toString().equals(value.toUpperCase())) {
                return type;
            }
        }
        throw new IllegalArgumentException("Not valid type operation");
    }
}
