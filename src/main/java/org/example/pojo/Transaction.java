package org.example.pojo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Builder
public class Transaction {
    final Integer id;
    TypeTransaction type;
    BigDecimal amountOperation;
    Integer account1Id;
    Integer account2Id;
    Timestamp timeTransaction;
}
