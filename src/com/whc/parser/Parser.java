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
    * expr -> term rest5
    * term -> unary rest6
    * rest5 -> + term rest5 | - term rest5 | ε
    * rest6 -> * unary rest6 | / unary rest6 |ε
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
    * expr -> term rest5
    * */
    public void expr(){
        this.read();
        ProductionSteps.add("expr -> term rest5");

        analyticStep.pollFirst();
        analyticStep.addFirst("rest5");
        analyticStep.addFirst("term");
        analyticSteps.add(assistance.toString()+analyticStep.toString());

        term();
        rest5();
    }

    /*
    * term -> unary rest6
    * */
    public void term(){
        ProductionSteps.add("term -> unary rest6");

        analyticStep.pollFirst();
        analyticStep.addFirst("rest6");
        analyticStep.addFirst("unary");
        analyticSteps.add(assistance.toString()+analyticStep.toString());

        unary();
        rest6();
    }

    /*
    * rest5 -> + term rest5 | - term rest5 | ε
    * */
    public void rest5(){
        if(token!=null && 41==token.getSortCode()){//"+".equals(token.getText())
            ProductionSteps.add("rest5 -> + term rest5");

            analyticStep.pollFirst();
            analyticStep.addFirst("rest5");
            analyticStep.addFirst("term");
            assistance.add("+");
            analyticSteps.add(assistance.toString()+analyticStep.toString());

            this.read();
            term();
            rest5();
        }else if(token!=null && 42==token.getSortCode()){//"-".equals(token.getText())
            ProductionSteps.add("rest5 -> - term rest5");

            analyticStep.pollFirst();
            analyticStep.addFirst("rest5");
            analyticStep.addFirst("term");
            assistance.add("-");
            analyticSteps.add(assistance.toString()+analyticStep.toString());

            this.read();
            term();
            rest5();
        }else {
            ProductionSteps.add("rest5 -> ε");
            analyticStep.pollFirst();
            analyticSteps.add(assistance.toString()+analyticStep.toString());
            return;
        }
    }

    public void rest6(){
        if(token!=null && 43==token.getSortCode()){//"*".equals(token.getText())
            ProductionSteps.add("rest6 -> / term rest6");

            analyticStep.pollFirst();
            analyticStep.addFirst("rest6");
            analyticStep.addFirst("unary");
            assistance.add(token.getText());
            analyticSteps.add(assistance.toString()+analyticStep.toString());

            this.read();
            unary();
            rest6();
        }else if(token!=null && 44==token.getSortCode()){//"/".equals(token.getText())
            ProductionSteps.add("rest6 -> / term rest6");

            analyticStep.pollFirst();
            analyticStep.addFirst("rest6");
            analyticStep.addFirst("unary");
            assistance.add(token.getText());
            analyticSteps.add(assistance.toString()+analyticStep.toString());

            this.read();
            unary();
            rest6();
        }else {
            ProductionSteps.add("rest6 -> ε");
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
