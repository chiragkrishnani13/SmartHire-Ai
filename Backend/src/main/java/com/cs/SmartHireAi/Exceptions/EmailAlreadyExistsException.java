package com.cs.SmartHireAi.Exceptions;

public class EmailAlreadyExistsException extends Exception{
    public EmailAlreadyExistsException(){
        super("Email Already Exists");
    }
}
