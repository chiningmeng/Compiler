package com.whc.lexer.token;

public class NumToken extends Token {

    private double property;

    public NumToken(Double value,int lineINdex) {
        super(lineINdex);
        this.text = Double.toString(value);
        this.sortCode = 100;
        this.property = value;
    }


    @Override
    public int getSortCode() {
        return sortCode;
    }

    @Override
    public String getText() {
        return text;
    }

    public double getProperty() {
        return property;
    }

}



