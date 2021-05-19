package com.whc.lexer.token;

public class NumToken extends Token {
    private int value;

    public NumToken(int value,int lineINdex) {
        super(lineINdex);
        this.value = value;
        this.sortCode = 100;
        this.property = String.valueOf(value);
    }


    @Override
    public int getSortCode() {
        return sortCode;
    }

    @Override
    public String getText() {
        return Integer.toString(value);
    }

    @Override
    public String getProperty() {
        return property;
    }


    public int getNumber() {
        return value;
    }
}



