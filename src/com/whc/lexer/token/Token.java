package com.whc.lexer.token;

public class Token {
    protected String text;
    protected int sortCode;
//    protected String property;
    protected int lineIndex;

    public Token() {
    }

    public Token(int lineIndex) {
        this.lineIndex = lineIndex;
    }

    public int getSortCode() {
        return -1;
    }

    public String getText() {
        return null;
    }

//    public String getProperty() {
//        return null;
//    }

    public int getLineIndex() {
        return lineIndex;
    }
}
