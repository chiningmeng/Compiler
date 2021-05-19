package com.whc.lexer.token;

import java.util.HashMap;
import java.util.Map;

public class IdToken extends Token{
    private String text;

    private Map map = new HashMap(){{
       put("int",5);
       put("else",15);
       put("if",17);
       put("while",20);
    }};
    public IdToken(String text,int lineIndex) {
        super(lineIndex);
        this.text = text;
        setSortCode(text);

    }

    @Override
    public int getSortCode() {
        return sortCode;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public String getProperty() {
        return property;
    }

    public void setSortCode(String text){
        //查询是否为关键字
        if(map.get(text)!=null){
            //是，返回对应种别码,并设置默认属性值“-”
            sortCode = (int) map.get(text);
            property = "-";
        }else {
            //不是，作为标识符，返回对应种别码111，设置属性值
            sortCode = 111;
            property = text;

        }
    }
}
