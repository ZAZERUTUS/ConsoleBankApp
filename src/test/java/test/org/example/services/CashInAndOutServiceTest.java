package test.org.example.services;

import lombok.SneakyThrows;
import org.example.services.CashInAndOutService;
import org.example.pojo.Account;
import org.example.pojo.Transaction;
import org.example.pojo.TypeTransaction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.Objects;

import static org.example.db.CRUDAccounts.*;
import static org.example.db.CRUDTransactions.getTransactionById;


public class CashInAndOutServiceTest {

    private static Account account1;
    private static Account account2;

    @BeforeAll
    @SneakyThrows
    public static void addDataForTest() {
        Account acc1 = new Account("testNumAdded", new BigDecimal(200), 1, 1);
        account1 = getAccountById(addAccount(acc1));

        Account acc2 = new Account("testNumAdded2", new BigDecimal(100), 1, 1);
        account2 = getAccountById(addAccount(acc2));
    }

    @AfterAll
    public static void deleteDataForTest() {
        deleteAccount(account1);
        deleteAccount(account2);
    }

    @ParameterizedTest(name = "{index} -  From {0} operation {1} success operation is {2}")
//    @ValueSource(ints = {32, 33, 222, 1})
    @CsvSource(value = {
            "25, Cash_out, true",
            "200, Cash_out, true",
            "201, cash_out, false",
            "22, cash_in, true",
            "2244, cash_in, true"

    })
    public void testAssertionCash(int operationSum, String type,  boolean isSuccess) {
        TypeTransaction typeTransaction = TypeTransaction.getTypeByValue(type);
        Transaction transaction = Transaction.builder()
                .account1Id(account1.id)
                .type(typeTransaction)
                .amountOperation(new BigDecimal(operationSum))
                .build();
        CashInAndOutService cashInAndOutService = new CashInAndOutService(transaction);
        Assertions.assertEquals(cashInAndOutService.transfer(), isSuccess);
    }

    @Test
    public void testTransactionIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> new CashInAndOutService(null));
    }

    @Test
    public void testTransferAndSaveStatementInDBIncorrectTransaction() {
        TypeTransaction typeTransaction = TypeTransaction.TRANSFER;
        Transaction transaction = Transaction.builder()
                .account1Id(account1.id)
                .account2Id(account2.id)
                .type(typeTransaction)
                .amountOperation(new BigDecimal("23.44"))
                .build();
        Assertions.assertEquals(null,
                new CashInAndOutService(transaction).transferAndSaveStatementInDB());
    }

    @Test
    public void testTransferAndSaveStatementInDBCorrectTransaction() {
        BigDecimal sumTransfer = new BigDecimal("23.44");
        TypeTransaction typeTransaction = TypeTransaction.CASH_IN;
        Transaction transaction = Transaction.builder()
                .account1Id(account2.id)
                .type(typeTransaction)
                .amountOperation(new BigDecimal("23.44"))
                .build();
        Integer idTransaction = new CashInAndOutService(transaction).transferAndSaveStatementInDB();
        Assertions.assertNotNull(idTransaction);

        Transaction fullTransaction = getTransactionById(idTransaction);
        Assertions.assertEquals(sumTransfer, fullTransaction.getAmountOperation());
        Assertions.assertEquals(account2.id, fullTransaction.getAccount1Id());
    }
}
