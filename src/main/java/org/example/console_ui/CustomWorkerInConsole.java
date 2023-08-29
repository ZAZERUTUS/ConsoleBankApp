package org.example.console_ui;

import org.example.pojo.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Scanner;

public interface CustomWorkerInConsole {

//    public default void isVerifyArgumentForBreak()

    default BigDecimal getRoundedBigDecimal(Scanner scanner) {
        return scanner.nextBigDecimal().setScale(2, RoundingMode.HALF_UP);
    }

    Transaction getCurrentTransaction();
}
