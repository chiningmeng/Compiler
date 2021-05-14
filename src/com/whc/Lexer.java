package com.whc;


import com.whc.Token.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public class Lexer {

    private InputStream reader;
    private static final int EMPTY = -1;
    private int lastChar = EMPTY;

    public Lexer(InputStream r){
        reader = r;
    }
    private static boolean isLetter(int c){
        return 'A' <= c && c <= 'Z'||'a' <= c && c <= 'z';
    }
    private static boolean isDigit(int c){
        return '0' <= c && c <= '9';
    }

    private static boolean isSpace(int c){
        return 0<=c && c <= ' ';
    }

    private int getChar() throws IOException {
        if(lastChar == EMPTY){
            return reader.read();
        } else {
            int c = lastChar;
            lastChar = EMPTY;
            return c;
        }
    }

    private void ungetChar(int c){
        lastChar = c;
    }

    public Token read() throws IOException{
        StringBuilder word = new StringBuilder();
        int c;
        //跳过空格
        do{
            c= getChar();
        }while (isSpace(c));

        if(c<0){
            //代码结尾
            return null;
        }else if(isDigit(c)){
            do{ //识别数字
                //[0-9][0-9]*
                word.append((char)c);
                c = getChar();
                if(!isDigit(c)){
                    ungetChar(c);
                    return new NumToken(Integer.valueOf(word.toString()));
                }
            }while (isDigit(c));
        }else if(isLetter(c)){
            do{ //识别标识符
                //  [A-Za-z][A-Za-z0-9]*
                word.append((char)c);
                c = getChar();
                if(!(isLetter(c)||isDigit(c))){
                    ungetChar(c);
                    return new IdToken(word.toString());
                }
            }while (isLetter(c)||isDigit(c));
        }else if(OperatorToken.isExist(String.valueOf((char)c))>=0){
            //识别运算符
            word.append((char)c);
            c = getChar();
            if(OperatorToken.isExist(String.valueOf((char)c))>=0){
                //下一个字符是运算符
                word.append((char)c);
                if(OperatorToken.isExist(word.toString())>=0){
                    return new OperatorToken(word.toString());
                }else {
                    //组合符号不存在，回退
                    ungetChar(c);
                    word.deleteCharAt(word.length()-1);
                }
            }else {
                //下一个字符不是运算符
                ungetChar(c);
                return new OperatorToken(word.toString());
            }
        }else if(DelimiterToken.isExist(String.valueOf((char)c))>=0){
            return new DelimiterToken(String.valueOf((char)c));
        } else {
            throw new IOException();
        }


        return new Token() {
            @Override
            public int getSortCode() {
                return -1;
            }

            @Override
            public String getText() {
                return null;
            }

            @Override
            public String getProperty() {
                return null;
            }
        };
    }
}
