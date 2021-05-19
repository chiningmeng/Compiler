package com.whc.parser;

import com.whc.exception.UnknownSymbolException;
import com.whc.lexer.Lexer;
import com.whc.lexer.token.Token;

import java.io.IOException;
import java.util.*;

public class Parser {
    private Lexer lexer;
    private Token token;
    //产生式步骤
    private List<String> ProductionSteps = new ArrayList<>();
    //推导过程步骤，把assistance+analyticStep拼接后装入
    private List<String> analyticSteps = new ArrayList<>();
    //非终结符
    private Deque<String> analyticStep = new LinkedList<>();
    //终结符
    private List<String> assistance = new LinkedList<>();

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }
    /*
    * expr -> term rest
    * term -> unary rest
    * rest -> + term rest | - term rest | ε
    * rest -> * unary rest | / unary rest |ε
    * unary -> factor
    * factor -> num
    * */

    private void read() {
        try {
            token = lexer.read();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnknownSymbolException e) {
            e.printStackTrace();
        }
    }

    /*
    * expr -> term rest
    * */
    public void expr(){
        this.read();
        ProductionSteps.add("expr -> term rest");

        analyticStep.pollFirst();
        analyticStep.addFirst("rest");
        analyticStep.addFirst("term");
        analyticSteps.add(assistance.toString()+analyticStep.toString());

        term();
        rest();
    }

    /*
    * term -> unary rest
    * */
    public void term(){
        ProductionSteps.add("term -> unary rest");

        analyticStep.pollFirst();
        analyticStep.addFirst("rest");
        analyticStep.addFirst("unary");
        analyticSteps.add(assistance.toString()+analyticStep.toString());

        unary();
        rest();
    }

    /*
    * rest -> + term rest | - term rest | ε
    * */
    public void rest(){
        if(token!=null && "+".equals(token.getText())){
            ProductionSteps.add("rest -> + term rest");

            analyticStep.pollFirst();
            analyticStep.addFirst("rest");
            analyticStep.addFirst("term");
            assistance.add("+");
            analyticSteps.add(assistance.toString()+analyticStep.toString());

            this.read();
            term();
            rest();
        }else if(token!=null && "-".equals(token.getText())){
            ProductionSteps.add("rest -> - term rest");

            analyticStep.pollFirst();
            analyticStep.addFirst("rest");
            analyticStep.addFirst("term");
            assistance.add("-");
            analyticSteps.add(assistance.toString()+analyticStep.toString());

            this.read();
            term();
            rest();
        }else if(token!=null && "*".equals(token.getText())){
            ProductionSteps.add("rest -> * term rest");

            analyticStep.pollFirst();
            analyticStep.addFirst("rest");
            analyticStep.addFirst("term");
            assistance.add("*");
            analyticSteps.add(assistance.toString()+analyticStep.toString());

            this.read();
            term();
            rest();
        }else if(token!=null && "/".equals(token.getText())){
            ProductionSteps.add("rest -> / term rest");

            analyticStep.pollFirst();
            analyticStep.addFirst("rest");
            analyticStep.addFirst("term");
            assistance.add("/");
            analyticSteps.add(assistance.toString()+analyticStep.toString());

            this.read();
            term();
            rest();
        }else {
            ProductionSteps.add("rest -> ε");
            analyticStep.pollFirst();
            analyticSteps.add(assistance.toString()+analyticStep.toString());
            return;
        }
    }

    /*
    * unary -> factor
    * */
    public void unary(){
        ProductionSteps.add("unary -> factor");
        analyticStep.pollFirst();
        analyticStep.addFirst("factor");
        analyticSteps.add(assistance.toString()+analyticStep.toString());
        factor();
    }

    /*
    * factor -> num
    * */
    public void factor(){
        int sort = this.token.getSortCode();
        if(sort==111||sort==100){
            ProductionSteps.add("factor -> num");
            assistance.add(token.getText());
            analyticStep.pollFirst();
            analyticSteps.add(assistance.toString()+analyticStep.toString());
            //已匹配，更新单词
            this.read();
        }
    }

    public void displayProductionSteps(){
        for(String str : ProductionSteps){
            System.out.println(str);
        }
    }

    public void displayAnalyticSteps(){
        for(String str : analyticSteps){
            System.out.println(str);
        }
    }
}
