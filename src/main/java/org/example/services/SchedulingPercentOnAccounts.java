package org.example.services;

import org.example.db.DBConnector;
import org.example.pojo.Account;

import java.math.BigDecimal;
import java.util.Timer;
import java.util.TimerTask;

import static org.example.config.ConfigHandler.getInstanceConfig;
import static org.example.db.CRUDAccounts.*;

/**
 * Класс для регулярной проверки начисления процентов по остатку на счёте
 * Проверяет что разница между текущим месяцем и месяцем последнего зачисления больше 1 и начисляет проценты за 1 мясяц
 * Если счёт открыт менее месяца назад проценты так же начислены не будут
 */
public class SchedulingPercentOnAccounts {

    public SchedulingPercentOnAccounts() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                scheduling();
            }
        }).start();
    }

    public void scheduling() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                    updateAllBalanceByPercent(BigDecimal.valueOf(getInstanceConfig().getConfig().getPercent()));
            }
        }, 0, getInstanceConfig().getConfig().getPeriodVerifyPercentMls());
    }


}
