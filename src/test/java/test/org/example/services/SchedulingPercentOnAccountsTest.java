package test.org.example.services;

import lombok.SneakyThrows;
import org.example.config.Config;
import org.example.pojo.Account;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.math.BigDecimal;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.ZoneOffset;

import static org.example.config.ConfigHandler.getInstanceConfig;
import static org.example.db.CRUDAccounts.*;
import static org.example.db.CRUDAccounts.deleteAccount;

public class SchedulingPercentOnAccountsTest {

    private static Account account1;
    private static Account account2;
    private static final Config config = getInstanceConfig(Paths.get("configRorTest")).getConfig();

    @BeforeAll
    @SneakyThrows
    public static void addDataForTest() {
        Account acc1 = new Account("testNumAdded", new BigDecimal(200), 1, 1);
        account1 = getAccountById(addAccount(acc1));
        account1.timeInterestPercent = Timestamp.from(account1.timeInterestPercent.toLocalDateTime().minusMonths(1).toInstant(ZoneOffset.UTC));

        //todo переделать обновления класса account что бы обновляло все данные и обновить метод для работы с сетами которые будут обновляться по балансам
        Account acc2 = new Account("testNumAdded2", new BigDecimal(100), 1, 1);
        account2 = getAccountById(addAccount(acc2));
        account2.timeInterestPercent = Timestamp.from(account2.timeInterestPercent.toLocalDateTime().minusDays(25).toInstant(ZoneOffset.UTC));

    }

    @AfterAll
    public static void deleteDataForTest() {
        deleteAccount(account1);
        deleteAccount(account2);
    }

    public void startScheduledUpdate() {
        SchedulingPercentOnAccountsTest schedulingPercentOnAccountsTest =
                new SchedulingPercentOnAccountsTest();
        schedulingPercentOnAccountsTest.startScheduledUpdate();

    }
}
