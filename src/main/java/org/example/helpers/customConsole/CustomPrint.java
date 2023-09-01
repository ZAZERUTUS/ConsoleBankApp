package org.example.helpers.customConsole;

import static org.example.helpers.customConsole.ConsoleColors.ANSI_PURPLE;
import static org.example.helpers.customConsole.ConsoleColors.ANSI_RED;
import static org.example.helpers.customConsole.ConsoleColors.ANSI_RESET;
import static org.example.helpers.customConsole.ConsoleColors.ANSI_YELLOW;

public class CustomPrint {
    public static void customPrint(String color, String text) {
        System.out.println(color + text + ANSI_RESET);
    }

    public static void customPrintYellow(String text) {
        customPrint(ANSI_YELLOW, text);
    }

    public static void customPrintRed(String text) {
        customPrint(ANSI_RED, text);
    }

    public static void customPrintPurple(String text) {
        customPrint(ANSI_PURPLE, text);
    }
}
