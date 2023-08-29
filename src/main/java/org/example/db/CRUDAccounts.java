package org.example.db;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.example.pojo.Account;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.logging.Logger;

/**
 * Класс предназначен для работы с сущностью {@link Account}
 */
@Log4j2
public class CRUDAccounts extends DBConnector{

    protected static String queryNotUpdatedPercent = "SELECT * FROM test.accounts WHERE time_created < (CURRENT_DATE) - interval '1' month AND date_part('month', time_last_interest_percent) < date_part('month', CURRENT_DATE)";
    protected static String formatQueryForGetAccountById = "SELECT * FROM test.accounts WHERE id = %s;";
    protected static String formatQueryForGetDeleteById = "DELETE FROM test.accounts WHERE id = %s RETURNING id;";
    protected static String formatQueryForUpdateAccount = "UPDATE test.accounts SET balance = %s WHERE id = %s RETURNING id;";
    protected static String formatQueryForAddAccount = "INSERT INTO test.accounts (num, balance, bank_id, customer_id) VALUES ('%s', '%s', %s, %s) RETURNING id;";

    protected static boolean isNull(Object o)  {
        return o == null;
    }

    @SneakyThrows
    public static synchronized Account getAccountById(Integer id) {
        synchronized (DBConnector.class) {
            new DBConnector();
            Statement statement = getStatementPostgres();
            ResultSet resultSet = getResultSetBySQLQuery(statement, String.format(formatQueryForGetAccountById, id));
            Account accountForRet = null;
            try {
                if (resultSet.next()) {
                    accountForRet = new Account(resultSet.getInt(1),
                            resultSet.getString(2),
                            resultSet.getBigDecimal(3),
                            resultSet.getInt(4),
                            resultSet.getInt(5),
                            resultSet.getTimestamp(6),
                            resultSet.getTimestamp(7));
                    return accountForRet;
                }
            } catch (NullPointerException e) {
                System.out.println("Not fount account in DB with id - " + id);
            }
            closeDbConnection();
            log.info("Account for return - " + accountForRet);
            return accountForRet;
        }
    }

    /**
     *
     * @param account - экземпляр класса полученный из бд!
     * @return
     */
    @SneakyThrows
    public static synchronized boolean updateAccount(Account account) {
        if (account.timeCreateAccount == null) {
            throw new ClassNotFoundException("Incorrect class! Get function class from db");
        }
        synchronized (DBConnector.class) {
            Statement statement = getStatementPostgres();
            ResultSet resultSet = getResultSetBySQLQuery(statement,
                    String.format(formatQueryForUpdateAccount, account.amount, account.id));
            try {
                if (resultSet.next()) {
                    return resultSet.getInt(1) == account.id;
                }
            } catch (NullPointerException e) {
                log.error("Request for update account not return result");
            }
            log.error("Incorrect behavior when update account - " + account);
            return false;
        }
    }

    @SneakyThrows
    public static synchronized Integer addAccount(Account account) {
        synchronized (DBConnector.class) {
            Statement statement = getStatementPostgres();
            ResultSet resultSet = getResultSetBySQLQuery(statement,
                    String.format(formatQueryForAddAccount, account.number, account.amount, account.bankId, account.customerId));
            try {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            } catch (NullPointerException e) {
                log.error("Request for update account not return result");
            }
            log.error("Incorrect behavior when update account - " + account);
            return null;
        }
    }
    @SneakyThrows
    public static synchronized boolean deleteAccount(Account account) {
        synchronized (DBConnector.class) {
            Statement statement = getStatementPostgres();
            ResultSet resultSet = getResultSetBySQLQuery(statement,
                    String.format(formatQueryForGetDeleteById, account.id));
            try {
                if (resultSet.next()) {
                    return resultSet.getInt(1) == account.id;
                }
            } catch (NullPointerException e) {
                log.error("Request for update account not return result");
            }
            log.error("Incorrect behavior when update account - " + account);
            return false;
        }
    }

    @SneakyThrows
    public static void updateAllBalanceByPercent(BigDecimal percent) {
        synchronized (DBConnector.class) {
            Statement statement = getStatementPostgres();
            ResultSet resultSet = getResultSetBySQLQuery(statement, queryNotUpdatedPercent);
            while (resultSet.next()) {
                System.out.println("update account with id - " + resultSet.getString(1));
                BigDecimal current = resultSet.getBigDecimal(3);
                resultSet.updateFloat(3, current.multiply(

                        percent.setScale(2, RoundingMode.HALF_UP).divide(
                                new BigDecimal(100),
                                RoundingMode.HALF_UP)
                                .add(new BigDecimal(1))
                ).floatValue());
                resultSet.updateRow();
                resultSet.updateTimestamp(7 , new Timestamp(System.currentTimeMillis()));
                resultSet.updateRow();
            }
        }
    }

}
