package com.whc.Token;

public abstract class Token {

    protected int sortCode;
    protected String property;

    public abstract int getSortCode() ;

    public abstract String getText();

    public abstract String getProperty();
}
