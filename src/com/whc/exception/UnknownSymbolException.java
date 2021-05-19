package com.whc.exception;


import com.whc.lexer.token.Token;

public class UnknownSymbolException extends Exception {

    public UnknownSymbolException(int lineIndex, char c){
        super("Cannot resolve symbol: " +"\"" + c + "\"" + " at " + lineIndex + ".");
    }
}
