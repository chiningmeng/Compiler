package com.whc.lexer;


import com.whc.exception.UnknownSymbolException;
import com.whc.lexer.token.*;

import java.io.IOException;
import java.io.InputStream;

public class Lexer {

    private InputStream reader;

    //回退字符所用
    private static final int EMPTY = -1;
    private int lastChar = EMPTY;

    //记录行号
    private int lineIndex = 1;

    public Lexer(InputStream r){
        reader = r;
    }
    private static boolean isLetter(int c){
        return 'A' <= c && c <= 'Z'||'a' <= c && c <= 'z';
    }
    private static boolean isDigit(int c){
        return '0' <= c && c <= '9';
    }

    private  boolean isSpace(int c){
        if(c=='\n'){
            //当前行号加一
            this.lineIndex++;
        }
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

    public Token read() throws IOException, UnknownSymbolException {
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
                    return new NumToken(Integer.valueOf(word.toString()),lineIndex);
                }
            }while (isDigit(c));
        }else if(isLetter(c)){
            do{ //识别标识符
                //  [A-Za-z][A-Za-z0-9]*
                word.append((char)c);
                c = getChar();
                if(!(isLetter(c)||isDigit(c))){
                    ungetChar(c);
                    return new IdToken(word.toString(),lineIndex);
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
                    return new OperatorToken(word.toString(),lineIndex);
                }else {
                    //组合符号不存在，回退
                    ungetChar(c);
                    word.deleteCharAt(word.length()-1);
                }
            }else {
                //下一个字符不是运算符
                ungetChar(c);
                return new OperatorToken(word.toString(),lineIndex);
            }
        }else if(DelimiterToken.isExist(String.valueOf((char)c))>=0){
            return new DelimiterToken(String.valueOf((char)c),lineIndex);
        } else {
            //以上单词均不能匹配，抛出异常
            throw new UnknownSymbolException(lineIndex,(char)c);
        }
        return new Token(lineIndex);
    }
}
