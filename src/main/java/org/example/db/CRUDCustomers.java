package org.example.db;

import lombok.SneakyThrows;
import org.example.pojo.Customer;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CRUDCustomers extends DBConnector{

//    protected String formatCustomerById = "SELECT test.customers.id,test.customers.name,test.customers.last_name,test.accounts.id FROM test.customers ,test.accounts WHERE  test.accounts.customer_id = test.customers.id AND test.accounts.customer_id = %s";

    protected static String formatCustomerById = "SELECT * FROM test.customers where id = %s";
    protected static String formatCustomerAccountsById = "SELECT id FROM test.accounts where customer_id = %s";


    @SneakyThrows
    protected static List<Integer> getAccountIds(Statement statement, int customerId) {
        ResultSet resultSet = getResultSetBySQLQuery(statement,
                String.format(formatCustomerAccountsById, customerId));
        List<Integer> idsAccount = new ArrayList<>();
        while (resultSet.next()) {
            idsAccount.add(resultSet.getInt(1));
        }
        return idsAccount;
    }
    @SneakyThrows
    public static synchronized Customer getCustomerById(int customerId) {
        Statement statement = new DBConnector().getStatementPostgres();
        String query = String.format(formatCustomerById, customerId);
        ResultSet resultSet = getResultSetBySQLQuery(statement, query);
        Customer customer = null;
        if (resultSet != null) {
            resultSet.next();
            customer = new Customer(resultSet.getInt(1),
                    resultSet.getString(2),
                    resultSet.getString(3));
            customer.setAccounts(getAccountIds(statement, customerId));
        }
        closeDbConnection();
        return customer;
    }
}
