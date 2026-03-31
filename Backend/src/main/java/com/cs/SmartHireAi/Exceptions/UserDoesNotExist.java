package com.cs.SmartHireAi.Exceptions;

public class UserDoesNotExist extends RuntimeException {
    public UserDoesNotExist() {
        super("User nhi hai ");
    }
}
