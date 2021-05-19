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
    private List<String> productionSteps = new ArrayList<>();
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
    *  加减乘除
    * expr -> term rest5
    * term -> unary rest6
    * rest5 -> + term rest5 | - term rest5 | ε
    * rest6 -> * unary rest6 | / unary rest6 |ε
    * unary -> factor
    * factor -> ( expr ) | num
    *
    *布尔运算
    * bool -> equality
    * equality -> rel rest4
    * rest4 -> == rel rest4 | != rel rest4 | ε
    * rel -> expr rol_expr
    * rol_expr -> < expr | <= expr | > expr | >= expr | ε
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
    /*
    * 注：
    * 一般的，如果非终结符函数识别出一个终结符，则要在调用下一个非终结符函数前调用read更新token
    * 例外：如果非终结符函数识别出一个终结符，但所调用的下一个非终结符函数在开始时就有read,则不再调用前read
    *       否则会跳过一个单词
    * */
    public void expr(){
        this.read();
        productionSteps.add("expr -> term rest5");

        analyticStep.pollFirst();
        analyticStep.addFirst("rest5");
        analyticStep.addFirst("term");
        record();

        term();
        rest5();
    }

    /*
    * term -> unary rest6
    * */
    public void term(){
        productionSteps.add("term -> unary rest6");

        analyticStep.pollFirst();
        analyticStep.addFirst("rest6");
        analyticStep.addFirst("unary");
        record();

        unary();
        rest6();
    }

    /*
    * rest5 -> + term rest5 | - term rest5 | ε
    * */
    public void rest5(){
        if(token!=null && 41==token.getSortCode()){//"+".equals(token.getText())
            productionSteps.add("rest5 -> + term rest5");

            analyticStep.pollFirst();
            analyticStep.addFirst("rest5");
            analyticStep.addFirst("term");
            assistance.add(token.getText());
            record();

            this.read();
            term();
            rest5();
        }else if(token!=null && 42==token.getSortCode()){//"-".equals(token.getText())
            productionSteps.add("rest5 -> - term rest5");

            analyticStep.pollFirst();
            analyticStep.addFirst("rest5");
            analyticStep.addFirst("term");
            assistance.add(token.getText());
            record();

            this.read();
            term();
            rest5();
        }else {
            productionSteps.add("rest5 -> ε");
            analyticStep.pollFirst();
            record();
            return;
        }
    }

    public void rest6(){
        if(token!=null && 43==token.getSortCode()){//"*".equals(token.getText())
            productionSteps.add("rest6 -> / term rest6");

            analyticStep.pollFirst();
            analyticStep.addFirst("rest6");
            analyticStep.addFirst("unary");
            assistance.add(token.getText());
            record();

            this.read();
            unary();
            rest6();
        }else if(token!=null && 44==token.getSortCode()){//"/".equals(token.getText())
            productionSteps.add("rest6 -> / term rest6");

            analyticStep.pollFirst();
            analyticStep.addFirst("rest6");
            analyticStep.addFirst("unary");
            assistance.add(token.getText());
            record();

            this.read();
            unary();
            rest6();
        }else {
            productionSteps.add("rest6 -> ε");
            analyticStep.pollFirst();
            record();

            return;
        }
    }
    /*
    * unary -> factor
    * */
    public void unary(){
        productionSteps.add("unary -> factor");
        analyticStep.pollFirst();
        analyticStep.addFirst("factor");
        record();

        factor();
    }

    /*
    * factor -> num | (expr)
    * */
    public void factor(){
        if (token!=null&&81==token.getSortCode()){//(
            productionSteps.add("factor -> (expr)");

            assistance.add(token.getText());
            analyticStep.pollFirst();
            analyticStep.addFirst("expr)");
            record();

            expr();
            this.read();
            if(token!=null&&82==token.getSortCode()){//)
                assistance.add(token.getText());
                analyticStep.pollFirst();
                record();

                this.read();
            }
        }else if(token!=null&&100==token.getSortCode()){
            productionSteps.add("factor -> num");

            assistance.add(token.getText());
            analyticStep.pollFirst();
            record();
            //已匹配，更新单词
            this.read();
        }
    }

    /*
    * bool -> equality
    * */
    public void bool(){
        productionSteps.add("bool -> equality");

        analyticStep.pollFirst();
        analyticStep.addFirst("equality");
        record();
        equality();
    }

    /*
    * equality ->rel rest4
    * */
    public void equality(){
        productionSteps.add("equality ->rel rest4");

        analyticStep.pollFirst();
        analyticStep.addFirst("rest4");
        analyticStep.addFirst("rel");
        record();

        rel();
        rest4();
    }

    /*
    * rel -> expr rol_expr
    * */
    public void rel(){
        productionSteps.add("rel -> expr rol_expr");

        analyticStep.pollFirst();
        analyticStep.addFirst("rol_expr");
        analyticStep.addFirst("expr");
        record();

        expr();
        rol_expr();
    }

    /*
    * rest4 -> == rel rest4 | != rel rest4 | ε
    * */
    public void rest4(){
        if(token!=null&&51==token.getSortCode()){// ==
            productionSteps.add("rest4 -> == rel rest4");

            assistance.add(token.getText());
            analyticStep.pollFirst();
            analyticStep.addFirst("rest4");
            analyticStep.addFirst("rel");
            record();

            //this.read();
            rel();
            rest4();

        }else if(token!=null&&52==token.getSortCode()){// !=
            productionSteps.add("rest4 -> != rel rest4");

            assistance.add(token.getText());
            analyticStep.pollFirst();
            analyticStep.addFirst("rest4");
            analyticStep.addFirst("rel");

            //this.read();
            rel();
            rest4();

        }else {
            productionSteps.add("rest4 -> ε");

            analyticStep.pollFirst();
            record();
            return;
        }
    }

    /*
    * rol_expr -> < expr | <= expr | > expr | >= expr | ε
    * */
    public void rol_expr(){
        if(token!=null&&49==token.getSortCode()){//<
            productionSteps.add("rol_expr -> < expr");

            assistance.add(token.getText());
            analyticStep.pollFirst();
            analyticStep.addFirst("expr");
            record();

            //this.read();
            expr();

        }else if(token!=null&&50==token.getSortCode()){//<=
            productionSteps.add("rol_expr -> <= expr");

            assistance.add(token.getText());
            analyticStep.pollFirst();
            analyticStep.addFirst("expr");
            record();

            //this.read();
            expr();

        }else if(token!=null&&47==token.getSortCode()){//>
            productionSteps.add("rol_expr -> > expr");

            assistance.add(token.getText());
            analyticStep.pollFirst();
            analyticStep.addFirst("expr");
            record();

            //this.read();
            expr();

        }else if(token!=null&&48==token.getSortCode()){//>=
            productionSteps.add("rol_expr -> >= expr");

            assistance.add(token.getText());
            analyticStep.pollFirst();
            analyticStep.addFirst("expr");
            record();

            //this.read();
            expr();

        }else if(token!=null){
            productionSteps.add("rol_expr -> ε");

            analyticStep.pollFirst();
            record();
            return;
        }
    }

    public void displayProductionSteps(){
        for(String str : productionSteps){
            System.out.println(str);
        }
    }

    public void displayAnalyticSteps(){
        for(String str : analyticSteps){
            System.out.println(str);
        }
    }
    public void record(){
        analyticSteps.add(assistance.toString()+analyticStep.toString());
    }
}
