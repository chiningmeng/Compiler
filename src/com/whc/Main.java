package com.whc;

import com.whc.Token.Token;

import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException {
        //Reader br = new BufferedReader(new InputStreamReader(System.in));
        File f = new File("C:\\Users\\whc\\Desktop\\编译原理\\lexer\\test.txt");
        InputStream in = new FileInputStream(f);
        Lexer lexer = new Lexer(in);
       while(true) {
            Token token = lexer.read();
            try{
                System.out.println("< "+token.getText() + " , " + token.getProperty()+" >");
            } catch (Exception e) {
                break;
            }
        }
    }
}
