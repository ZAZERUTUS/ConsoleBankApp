package org.example;

public class CustomBreakException extends NoSuchMethodException{
    public CustomBreakException(String mess) {
        super(mess);
    }
}
