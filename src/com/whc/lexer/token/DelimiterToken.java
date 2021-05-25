package com.whc.lexer.token;

import java.util.HashMap;
import java.util.Map;

public class DelimiterToken extends Token{

    private String property;
    static Map map = new HashMap(){{
       put("(",81);
       put(")",82);
       put(";",84);
       put("{",86);
       put("}",87);
       put("[",88);
       put("]",89);
       put(",",90);
    }};
    public DelimiterToken(String text, int lineIndex){
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

    public static int isExist(String sign){
        if(map.get(sign)!=null){
            return (int) map.get(sign);
        }else{
            return -1;
        }
    }
}
