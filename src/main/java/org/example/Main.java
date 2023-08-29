package org.example;


import lombok.extern.log4j.Log4j2;
import org.example.console_ui.CashInOutOperationsWorker;
import org.example.console_ui.TransferOperationWorker;
import org.example.pojo.*;
import org.example.services.SchedulingPercentOnAccounts;

import java.util.InputMismatchException;
import java.util.Scanner;

@Log4j2
public class Main {

    public static void main(String[] args) {


        TypeTransaction selectedType;
        new SchedulingPercentOnAccounts();

        while (true) {

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
    }

    private static void verifyExitFromApp() {
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