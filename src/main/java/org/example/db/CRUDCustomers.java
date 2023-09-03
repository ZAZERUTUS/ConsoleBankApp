package org.example.db;

import static org.example.db.CRUDAccounts.getAccountsIdByUserId;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import org.example.pojo.Customer;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class CRUDCustomers extends DBConnector{

//    protected String formatCustomerById = "SELECT test.customers.id,test.customers.name,test.customers.last_name,test.accounts.id FROM test.customers ,test.accounts WHERE  test.accounts.customer_id = test.customers.id AND test.accounts.customer_id = %s";

    protected static String formatCustomerById = "SELECT * FROM test.customers where id = %s";
    protected static String queryGetAllCustomers = "SELECT * FROM test.customers";

    @SneakyThrows
    public static synchronized List<Customer> getAllCustomers() {
        Statement statement = new DBConnector().getStatementPostgres();
        ResultSet resultSet = getResultSetBySQLQuery(statement, queryGetAllCustomers);
        List<Customer> customers = new ArrayList<>();
        if (resultSet != null) {
            while (resultSet.next()) {
                customers.add(getCustomerById(resultSet.getInt(1)));
            }
        }
        return customers;
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
            customer.setAccounts(getAccountsIdByUserId(customerId));
        }
        closeDbConnection();
        return customer;
    }
}
