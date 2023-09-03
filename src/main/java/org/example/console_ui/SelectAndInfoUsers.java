package org.example.console_ui;

import static org.example.db.CRUDAccounts.getAccountsByUserId;
import static org.example.db.CRUDCustomers.getAllCustomers;
import static org.example.helpers.customConsole.CustomPrint.customPrintYellow;

import org.example.pojo.Account;
import org.example.pojo.Customer;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import lombok.SneakyThrows;

public class SelectAndInfoUsers {

    @SneakyThrows
    public void getInfo() {
        tag:
        while (true) {
            int selectPoint = 999;
            Scanner in = new Scanner(System.in);
            System.out.print("Какую информацию вы хотите получить?\n" +
                    "Доступны:\n" +
                    "1 - Список пользователей\n" +
                    "2 - Список счетов по id пользователя\n" +
                    "0 - выход\n" +
                    "Введите номер действия:");
            try {
                selectPoint = in.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Пожалуйста, введите корректное число");
            }

            switch (selectPoint) {
                case 1:
                    printAllUsers();
                    break;
                case 2:
                    printAllAccountUserWorker();
                    break;
                case 0:
                    break tag;
            }
        }
    }

    protected void printAllAccountUserWorker() {
        Integer id = null;
        Scanner in = new Scanner(System.in);
        while (id == null) {
            try {
                System.out.println("Введите id пользователя для получения информации о его счетах:");
                id = in.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Пожалуйста, введите корректное число");
            }
        }
        printAllAccountUser(id);
    }

    protected void printAllUsers() {
        List<Customer> customers = getAllCustomers();
        for (Customer customer: customers) {
            customPrintYellow(customer.toString());
        }
    }

    protected void printAllAccountUser(Integer idCustomer) {
        List<Account> accounts = getAccountsByUserId(idCustomer);
        if (accounts.size() == 0) {
            customPrintYellow("У пользователя отсутствуют счета");
        }
        for (Account account: accounts) {
            customPrintYellow(account.toString());
        }
    }
}
