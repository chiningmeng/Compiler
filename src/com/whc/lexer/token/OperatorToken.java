package com.whc.lexer.token;

import java.util.HashMap;
import java.util.Map;

public class OperatorToken extends Token{

    private String property;

    static Map map = new HashMap(){{
        put("+",41);
        put("-",42);
        put("*",43);
        put("/",44);
        put("%",45);
        put("=",46);
        put(">",47);
        put(">=",48);
        put("<",49);
        put("<=",50);
        put("==",51);
        put("!=",52);
        put("&&",53);
        put("||",54);
        put("!",55);
        put("++",46);
        put("--",57);
        put("|",58);
        put("&",59);
    }};

    public OperatorToken(String text, int lineIndex) {
        super(lineIndex);
        this.text = text;
        sortCode = (int) map.get(text);
        property = "-";
    }

    @Override
    public int getSortCode() {
        return sortCode;
    }

    @Override
    public String getText() {
        return text;
    }

    public String getProperty() {
        return property;
    }

    public static int isExist(String text){
        if(map.get(text)!=null){
            return (int) map.get(text);
        }else{
            return -1;
        }
    }
}
