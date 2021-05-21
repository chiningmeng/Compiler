package com.whc.parser;

import com.whc.exception.UnknownSymbolException;
import com.whc.lexer.Lexer;
import com.whc.lexer.token.Token;

import java.io.IOException;
import java.util.*;

public class Parser {
    private Lexer lexer;
    //每次读入的单词，语法分析器分析一个就读取更新一个
    private Token token;

    //产生式步骤
    private List<String> productionSteps = new LinkedList<>();
    //推导过程步骤，把assistance+analyticStep拼接后装入
    private List<String> analyticSteps = new LinkedList<>();
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
     * factor -> ( expr ) | loc | num
     *
     *布尔运算
     * bool -> equality
     * equality -> rel rest4
     * rest4 -> == rel rest4 | != rel rest4 | ε
     * rel -> expr rol_expr
     * rol_expr -> < expr | <= expr | > expr | >= expr | ε
     *
     *语句与数组
     * stmts -> stmt rest0
     * rest0 -> stmt rest0 | ε
     * stmt -> loc = expr ; | if ( bool ) stmt else stmt | while ( bool ) stmt
     * loc -> id resta
     * resta -> [ elist ] | ε
     * elist -> expr rest1
     * rest1 -> , expr rest1 | ε
     *
     * */


    public void read() {
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
    public void expr() {
        //this.read();
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
    public void term() {
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
    public void rest5() {
        if (token != null && 41 == token.getSortCode()) {//"+".equals(token.getText())
            productionSteps.add("rest5 -> + term rest5");

            analyticStep.pollFirst();
            analyticStep.addFirst("rest5");
            analyticStep.addFirst("term");
            assistance.add(token.getText());
            record();

            this.read();
            term();
            rest5();
        } else if (token != null && 42 == token.getSortCode()) {//"-".equals(token.getText())
            productionSteps.add("rest5 -> - term rest5");

            analyticStep.pollFirst();
            analyticStep.addFirst("rest5");
            analyticStep.addFirst("term");
            assistance.add(token.getText());
            record();

            this.read();
            term();
            rest5();
        } else {
            productionSteps.add("rest5 -> ε");
            analyticStep.pollFirst();
            record();
            return;
        }
    }

    public void rest6() {
        if (token != null && 43 == token.getSortCode()) {//"*".equals(token.getText())
            productionSteps.add("rest6 -> * term rest6");

            analyticStep.pollFirst();
            analyticStep.addFirst("rest6");
            analyticStep.addFirst("unary");
            assistance.add(token.getText());
            record();

            this.read();
            unary();
            rest6();
        } else if (token != null && 44 == token.getSortCode()) {//"/".equals(token.getText())
            productionSteps.add("rest6 -> / term rest6");

            analyticStep.pollFirst();
            analyticStep.addFirst("rest6");
            analyticStep.addFirst("unary");
            assistance.add(token.getText());
            record();

            this.read();
            unary();
            rest6();
        } else {
            productionSteps.add("rest6 -> ε");
            analyticStep.pollFirst();
            record();

            return;
        }
    }

    /*
     * unary -> factor
     * */
    public void unary() {
        productionSteps.add("unary -> factor");
        analyticStep.pollFirst();
        analyticStep.addFirst("factor");
        record();

        factor();
    }

    /*
     * factor -> num | (expr) | loc
     * */
    public void factor() {
        if (token != null && 81 == token.getSortCode()) {//(
            productionSteps.add("factor -> (expr)");

            assistance.add(token.getText());
            analyticStep.pollFirst();
            analyticStep.addFirst("expr)");
            this.read();

            expr();
            //this.read();
            if (token != null && 82 == token.getSortCode()) {//)
                assistance.add(token.getText());
                analyticStep.pollFirst();
                record();

                this.read();
            }
        } else if (token != null && 100 == token.getSortCode()) {
            productionSteps.add("factor -> num");

            assistance.add(token.getText());
            analyticStep.pollFirst();
            record();
            //已匹配，更新单词
            this.read();
        } else if (token != null) {
            productionSteps.add("factor -> loc");

            analyticStep.pollFirst();
            analyticStep.addFirst("loc");
            record();

            loc();
        }
    }

    /*
     * bool -> equality
     * */
    public void bool() {
        productionSteps.add("bool -> equality");

        analyticStep.pollFirst();
        analyticStep.addFirst("equality");
        record();
        equality();
    }

    /*
     * equality ->rel rest4
     * */
    public void equality() {
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
    public void rel() {
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
    public void rest4() {
        if (token != null && 51 == token.getSortCode()) {// ==
            productionSteps.add("rest4 -> == rel rest4");

            assistance.add(token.getText());
            analyticStep.pollFirst();
            analyticStep.addFirst("rest4");
            analyticStep.addFirst("rel");
            record();

            this.read();
            rel();
            rest4();

        } else if (token != null && 52 == token.getSortCode()) {// !=
            productionSteps.add("rest4 -> != rel rest4");

            assistance.add(token.getText());
            analyticStep.pollFirst();
            analyticStep.addFirst("rest4");
            analyticStep.addFirst("rel");

            this.read();
            rel();
            rest4();

        } else {
            productionSteps.add("rest4 -> ε");

            analyticStep.pollFirst();
            record();
            return;
        }
    }

    /*
     * rol_expr -> < expr | <= expr | > expr | >= expr | ε
     * */
    public void rol_expr() {
        if (token != null && 49 == token.getSortCode()) {//<
            productionSteps.add("rol_expr -> < expr");

            assistance.add(token.getText());
            analyticStep.pollFirst();
            analyticStep.addFirst("expr");
            record();

            this.read();
            expr();

        } else if (token != null && 50 == token.getSortCode()) {//<=
            productionSteps.add("rol_expr -> <= expr");

            assistance.add(token.getText());
            analyticStep.pollFirst();
            analyticStep.addFirst("expr");
            record();

            this.read();
            expr();

        } else if (token != null && 47 == token.getSortCode()) {//>
            productionSteps.add("rol_expr -> > expr");

            assistance.add(token.getText());
            analyticStep.pollFirst();
            analyticStep.addFirst("expr");
            record();

            this.read();
            expr();

        } else if (token != null && 48 == token.getSortCode()) {//>=
            productionSteps.add("rol_expr -> >= expr");

            assistance.add(token.getText());
            analyticStep.pollFirst();
            analyticStep.addFirst("expr");
            record();

            this.read();
            expr();

        } else if (token != null) {
            productionSteps.add("rol_expr -> ε");

            analyticStep.pollFirst();
            record();
            return;
        }
    }

    /*
     * stmts -> stmt rest0
     * */
    public void stmts() {
        productionSteps.add("stmts -> stmt rest0");

        analyticStep.pollFirst();
        analyticStep.addFirst("rest0");
        analyticStep.addFirst("stmt");
        record();

        stmt();
        rest0();
    }

    /*
    这咋处理ε不太清楚
    * rest0 -> stmt rest0 | ε
    * */
    public void rest0() {
        if(token!=null){
            productionSteps.add("rest0 -> stmt rest0");

            analyticStep.pollFirst();
            analyticStep.addFirst("rest0");
            analyticStep.addFirst("stmt");
            record();

            stmt();
            rest0();
        }else {
            productionSteps.add("rest0 -> ε");

            analyticStep.pollFirst();
            record();
        }

    }

    /*
     * stmt -> loc = expr ; | if ( bool ) stmt else stmt | while ( bool ) stmt
     * */
    public void stmt() {
        if (token != null && 17 == token.getSortCode()) {//if
            productionSteps.add("stmt -> if ( bool ) stmt else stmt");

            analyticStep.pollFirst();
            assistance.add(token.getText());
            analyticStep.addFirst("stmt");
            analyticStep.addFirst("else");
            analyticStep.addFirst("stmt");
            analyticStep.addFirst(")");
            analyticStep.addFirst("bool");
            analyticStep.addFirst("(");
            record();
            this.read();

            if (token != null && 81 == token.getSortCode()) {//(
                analyticStep.pollFirst();
                assistance.add(token.getText());
                record();
                this.read();

                bool();

                if (token != null && 82 == token.getSortCode()) {//)
                    assistance.add(token.getText());
                    analyticStep.pollFirst();
                    record();
                    this.read();

                    stmt();
                    if (token!=null&&15==token.getSortCode()){//else
                        assistance.add(token.getText());
                        analyticStep.pollFirst();
                        record();
                        this.read();

                        stmt();
                    }
                }
            }
        } else if (token != null && 20 == token.getSortCode()) {//while
            productionSteps.add("while ( bool ) stmt");

            analyticStep.pollFirst();
            assistance.add(token.getText());
            record();
            this.read();

            if (token != null && 81 == token.getSortCode()) {//(
                analyticStep.pollFirst();
                assistance.add(token.getText());
                record();
                this.read();

                bool();
                if (token != null && 82 == token.getSortCode()) {//)
                    assistance.add(token.getText());
                    analyticStep.pollFirst();
                    record();
                    this.read();

                    stmt();
                }
            }
        }else if (token!=null){
            productionSteps.add("stmt -> loc = expr ;");

            analyticStep.pollFirst();
            analyticStep.addFirst(";");
            analyticStep.addFirst("expr");
            analyticStep.addFirst("=");
            analyticStep.addFirst("loc");
            record();

            loc();
            if (token!=null&&46==token.getSortCode()){//=
                assistance.add(token.getText());
                analyticStep.pollFirst();
                record();
                this.read();

                expr();

                if (token!=null&&84==token.getSortCode()) {//;
                    assistance.add(token.getText());
                    analyticStep.pollFirst();
                    record();
                    this.read();
                }
            }
        }
    }

    /*
    * loc -> id resta
    * */
    public void loc(){
        if (token!=null&&111==token.getSortCode()){
            productionSteps.add("loc -> id resta");
            assistance.add(token.getText());
            analyticStep.pollFirst();
            analyticStep.addFirst("resta");
            record();
            this.read();

            resta();
        }
    }

    /*
    * resta -> [ elist ] | ε
    * */
    public void resta(){
        if (token!=null&&88==token.getSortCode()){//[
            productionSteps.add("resta -> [ elist ]");

            assistance.add(token.getText());
            analyticStep.pollFirst();
            analyticStep.addFirst("]");
            analyticStep.addFirst("elist");
            record();
            this.read();

            elist();

            if (token!=null&&89==token.getSortCode()){//]
                assistance.add(token.getText());
                analyticStep.pollFirst();
                record();
                this.read();
            }
        }else if(token!=null){
            productionSteps.add("resta -> ε");
            analyticStep.pollFirst();
            record();
        }
    }

    /*
    * elist -> expr rest1
    * */
    public void elist(){
        productionSteps.add("elist -> expr rest1");

        analyticStep.pollFirst();
        analyticStep.addFirst("rest1");
        analyticStep.addFirst("expr");
        record();

        expr();
        rest1();
    }

    /*
    * rest1 -> , expr rest1 | ε
    * */
    public void rest1(){
        if (token != null && 90 == token.getSortCode()) {//,
            productionSteps.add("rest1 -> , expr rest1");

            analyticStep.pollFirst();
            analyticStep.addFirst("rest1");
            analyticStep.addFirst("expr");
            assistance.add(token.getText());
            record();

            this.read();
            expr();
            rest1();
        } else {
            productionSteps.add("rest1 -> ε");
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
