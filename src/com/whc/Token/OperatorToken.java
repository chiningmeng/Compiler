package com.whc.Token;

import java.util.HashMap;
import java.util.Map;

public class OperatorToken extends Token{

    private String sign;

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
    }};

    public OperatorToken(String sign) {
        this.sign = sign;
        sortCode = (int) map.get(sign);
        property = "-";
    }

    public OperatorToken(int sort, String sign) {
        this.sortCode = sort;
        this.sign = sign;
        property = "-";
    }

    @Override
    public int getSortCode() {
        return sortCode;
    }

    @Override
    public String getText() {
        return sign;
    }

    @Override
    public String getProperty() {
        return property;
    }

    public static int isExist(String sign){
        if(map.get(sign)!=null){
            return (int) map.get(sign);
        }else{
            return -1;
        }
    }
}
