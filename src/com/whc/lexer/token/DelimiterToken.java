package com.whc.lexer.token;

import java.util.HashMap;
import java.util.Map;

public class DelimiterToken extends Token{
    private String sign;

    static Map map = new HashMap(){{
       put("(",81);
       put(")",82);
       put(";",84);
       put("{",86);
       put("}",87);
       put("[",88);
       put("]",89);
    }};
    public DelimiterToken(String sign,int lineIndex){
        super(lineIndex);
        this.sign = sign;
        sortCode = (int) map.get(sign);
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
