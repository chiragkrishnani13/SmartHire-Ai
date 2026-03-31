package com.cs.SmartHireAi.Exceptions;

public class DateOfBirthDoesNotExist extends RuntimeException {
    public DateOfBirthDoesNotExist() {
        super("Date of birth is null");
    }
}
