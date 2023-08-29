package org.example.services;

import org.example.db.DBConnector;
import org.example.pojo.Account;

import java.math.BigDecimal;
import java.util.Timer;
import java.util.TimerTask;

import static org.example.config.ConfigHandler.getInstanceConfig;
import static org.example.db.CRUDAccounts.*;

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
