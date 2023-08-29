package org.example.pojo;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * Класс для получения информации о счёте
 * Не обязательные поля устанавливаются только при получении его из базы
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Account {
    public int id;
    @NonNull
    public String number;
    @NonNull
    public BigDecimal amount;
    @NonNull
    public Integer bankId;
    @NonNull
    public Integer customerId;
    public Timestamp timeCreateAccount;
    public Timestamp timeInterestPercent;
}
