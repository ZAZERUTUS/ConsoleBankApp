package org.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
public class Bank {
    final int id;
    String name;
}
