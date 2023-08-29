package org.example.db;

import lombok.SneakyThrows;
import org.example.pojo.Account;
import org.example.pojo.Bank;

import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Класс предназначен для работы с сущностью {@link Bank}
 * Не обязательные поля устанавливаются только при получении его из базы
 */
public class CRUDBanks extends DBConnector{

    protected static String formatQueryForGetBankById = "SELECT * FROM test.banks where id = %s";

    @SneakyThrows
    public static Bank getBankById(Integer id) {
        synchronized (DBConnector.class) {
            Statement statement = getStatementPostgres();
            ResultSet resultSet = getResultSetBySQLQuery(statement, String.format(formatQueryForGetBankById, id));
            Bank bankForRet = null;
            if (resultSet.next()) {
                bankForRet = new Bank(resultSet.getInt(1), resultSet.getString(2));
            }
            closeDbConnection();
            return bankForRet;
        }
    }
}
