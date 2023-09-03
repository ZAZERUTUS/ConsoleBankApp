package org.example;


import lombok.extern.log4j.Log4j2;
import org.example.console_ui.CashInOutOperationsWorker;
import org.example.console_ui.SelectAndInfoUsers;
import org.example.console_ui.TransferOperationWorker;
import org.example.pojo.*;
import org.example.services.SchedulingPercentOnAccounts;
import org.example.statement_files.StatementByAccount;

import java.util.InputMismatchException;
import java.util.Scanner;

@Log4j2
public class Main {

    public static void main(String[] args) {
        new SchedulingPercentOnAccounts();

        while (true) {
            int intent = selectIntent();
            switch (intent) {
                case 1 -> new SelectAndInfoUsers().getInfo();
                case 2 -> selectorOperation();
                case 3 -> {
                    Scanner in = new Scanner(System.in);
                    System.out.print("Введите желаемое расширение выписки\n" +
                            "доступны PDF и TXT при вводе любого другого значения будут сформированы обе выписки: ");
                    new StatementByAccount(ExtensionStatement.getTypeByValue(in.next())).saveStatement();
                }
                case 4 -> new StatementByAccount(ExtensionStatement.PDF).saveMoneyStatement();
                default -> verifyExitFromApp();
            }

        }
    }

    protected static int selectIntent() {
        while (true) {
            int selectPoint = 999;
            Scanner in = new Scanner(System.in);
            System.out.print("Какое действие желаете совершить?\n" +
                    "Доступны:\n" +
                    "1 - Получение информации о пользователях и счетах\n" +
                    "2 - Совершение операций со счетами\n" +
                    "3 - Получение выписки по счёту\n" +
                    "4 - Получение money statement по счёту\n" +
                    "0 - выход\n" +
                    "Введите номер действия:");
            try {
                selectPoint = in.nextInt();
                return selectPoint;
            } catch (InputMismatchException e) {
                System.out.println("Пожалуйста, введите корректное число");
            }
        }
    }

    protected static void selectorOperation() {
        TypeTransaction selectedType;
        try {
            selectedType = getTypeOperation();
            if (selectedType != TypeTransaction.TRANSFER) {
                new CashInOutOperationsWorker(selectedType).executeOperation();
            } else {
                new TransferOperationWorker(selectedType).executeOperation();
            }
        } catch (CustomBreakException e) {
            log.error(e);
            verifyExitFromApp();
        }
    }

    protected static void verifyExitFromApp() {
        Scanner in = new Scanner(System.in);
        System.out.print("Если вы хотите остановить приложение введите 0 или любые символы что бы вернутся к приожению: ");
        try {
            int isExit = in.nextInt();
            if (isExit == 0) {
                System.exit(0);
            }
        } catch (InputMismatchException ignored) {}
    }

    private static TypeTransaction getTypeOperation() throws CustomBreakException{
        System.out.println("Пожалуйста введите тип операции CASH_IN, CASH_OUT, TRANSFER\n" +
                "так же доступно - Пополнение, Списание, Перевод: ");

        Scanner in = new Scanner(System.in);
        try {
            return TypeTransaction.getTypeByValue(in.next());
        } catch (Exception ignored) {}
        throw new CustomBreakException("Not correct type operation");
    }
}