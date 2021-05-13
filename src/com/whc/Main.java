package com.whc;

import com.whc.Token.Token;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class Main {

    public static void main(String[] args) throws IOException {
        Reader br = new BufferedReader(new InputStreamReader(System.in));
        Lexer lexer = new Lexer(br);
        for(int i=0;i<100;i++) {
            Token token = lexer.read();
            System.out.println("<"+token.getText() + "," + token.getProperty()+">");
        }
    }
}
