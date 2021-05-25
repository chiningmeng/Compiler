package com.whc.parser;

import com.whc.exception.UnknownSymbolException;
import com.whc.lexer.Lexer;
import com.whc.lexer.token.NumToken;
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


    //协助生成临时变量名
    private  int tmp = 1;
    //存放四元式
    private List<List<String>> quadruple = new ArrayList<>();

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }
    /*
     *设文法符号X，其属性如下：
     *      X.place 存放X值的变量名
     *      X.inArray 指向符号表中相应数组名字表项的指针，若不使用符号表，则X.inArray即为数组名字
     *                  (实验实验 4 中因不处理数组 则X.inArray 为存放X值的变量的名字)
     *      函数 emit( ) ：将生成的四元式语句发送到输出文件中
     *      函数 newtemp( ): 生成 一个临时变量的名字 如 t1 。
     *
     *  加减乘除
     *      文法                          语义动作
     * expr -> term                 {rest5.in=term.place}
     *          rest5               {expr.place=rest5.place}
     *
     * term -> unary                {rest6.in = unary.place}
     *          rest6               {term.place = rest6.place}
     *
     * rest5 -> + term              {rest5'.in=newtemp();
     *                               emit('+',rest5.in, term.place, rest5'.in)}
     *              rest5'           {rest5.place= rest5'.place}
     *
     *          | - term           {rest5'.in=newtemp();
     *                               emit('-',rest5.in, term.place, rest5'.in)}
     *              rest5'           {rest5.place= rest5'.place}
     *
     *          | ε                {rest5.place = rest5.in}
     *
     * rest6 -> * unary             {rest6'.in=newtemp();
     *                               emit('*',rest6.in, unary.place, rest6'.in)}
     *              rest6'           {rest6.place= rest6'.place}
     *
     *          | / unary           {rest6'.in=newtemp();
     *                               emit('/',rest6.in, unary.place, rest6'.in)}
     *              rest6'           {rest6.place= rest6'.place}
     *
     *          |ε                  {rest6.place = rest6.in}
     *
     * unary -> factor               {unary.place = factor.place}
     *
     * factor -> ( expr )           {unary.place = expr.palce}
     *
     *          | loc               {factor.place = loc.place}
     *
     *          | num               {factor.place = num.value}
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
     * stmt -> loc = expr ;     {emit( '=' , expr.place , '-' , loc.place);}
     *       | if ( bool ) stmt else stmt
     *       | while ( bool ) stmt
     *
     * loc -> id            {resta.inArray=id.place}
     *        resta         {loc.place=resta.place;}
     *
     *
     * resta -> [ elist ]
     *          | ε         {resta.place=resta.inArray;}
     *
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
    参考视频： https://www.bilibili.com/video/BV12741147J3?p=145&spm_id_from=pageDriver
    语义分析器：
       翻译模式：
        1.当只需综合属性时，为每一个语义规则建立一个包含赋值的动作，并把这个动作放在相应产生式右边的末尾
        2.当既有综合属性又有继承属性，在建立翻译模式时就必须保证：
            （1）产生式右边的符号的继承属性必须在这个符号以前的动作中计算出来
            （2）一个动作不能引用这个动作右边符号的综合属性
            （3）产生式左边非终结符的综合属性只有在它所引用的所有属性都计算出来才能计算。
                计算这种属性的动作通常可放在产生式右端的末尾
        递归下降翻译器的设计：
            1.对每个非终结符A构造一个函数过程
            2.A的属性实现为参数和变量
                继承属性：对A的每个继承属性设置为函数的一个形式参数
                综合属性：实现为函数返回值
                            若有多个综合属性，可打包返回
            3.A的产生式中的每一个文法符号的每一个属性：实现为A对应的函数过程中的局部变量

    * 函数 emit( ) )：将生成的 四元式 语句发送到输出文件中
      函数 newtemp( )): 生成 一个 临时 变量 的 名字 如 t1 。
    * */
    public void emit(String op,String arg1,String arg2,String arg3){
        List<String> temp = new ArrayList<>();
        temp.add(op);
        temp.add(arg1);
        temp.add(arg2);
        temp.add(arg3);

        quadruple.add(temp);
    }

    public String newTemp(){
        return new String("t"+ tmp++);
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
    public String expr() {
        //this.read();
        productionSteps.add("expr -> term rest5");

        analyticStep.pollFirst();
        analyticStep.addFirst("rest5");
        analyticStep.addFirst("term");
        record();

        String termPlace = term();  //{rest5.in=term.place}
        return rest5(termPlace);    //{expr.place=rest5.place}
    }

    /*
     * term -> unary rest6
     * */
    public String term() {
        productionSteps.add("term -> unary rest6");

        analyticStep.pollFirst();
        analyticStep.addFirst("rest6");
        analyticStep.addFirst("unary");
        record();

        String unaryPlace = unary();    //{rest6.in = unary.place}
        return rest6(unaryPlace);       //{term.place = rest6.place}
    }

    /*
     * rest5 -> + term rest5 | - term rest5 | ε
     *
     * rest5 -> + term              {rest5'.in=newtemp();
     *                               emit('+',rest5.in, term.place, rest5'.in)}
     *              rest5'           {rest5.place= rest5'.place}
     *
     *          | - term           {rest5'.in=newtemp();
     *                               emit('-',rest5.in, term.place, rest5'.in)}
     *              rest5'           {rest5.place= rest5'.place}
     *
     *          | ε                {rest5.place = rest5.in}
     * */
    public String rest5(String rest5In) {
        if (token != null && 41 == token.getSortCode()) {//rest5 -> + term rest5
            productionSteps.add("rest5 -> + term rest5");

            analyticStep.pollFirst();
            analyticStep.addFirst("rest5");
            analyticStep.addFirst("term");
            assistance.add(token.getText());
            record();

            this.read();

            String termPlace = term();
            String newTemp = newTemp();     //{rest5'.in=newtemp();
            emit("+",rest5In,termPlace,newTemp);    //emit('+',rest5.in, term.place, rest5'.in)}

            return rest5(newTemp);     //{rest5.place= rest5'.place}

        } else if (token != null && 42 == token.getSortCode()) {// rest5 -> - term rest5
            productionSteps.add("rest5 -> - term rest5");

            analyticStep.pollFirst();
            analyticStep.addFirst("rest5");
            analyticStep.addFirst("term");
            assistance.add(token.getText());
            record();

            this.read();

            String termPlace = term();
            String newTemp = newTemp();     //{rest5'.in=newtemp();
            emit("-",rest5In,termPlace,newTemp);    //emit('-',rest5.in, term.place, rest5'.in)}

            return rest5(newTemp);     //{rest5.place= rest5'.place}

        } else {
            productionSteps.add("rest5 -> ε");
            analyticStep.pollFirst();
            record();
            return rest5In; //{rest5.place = rest5.in}
        }

    }

    /*
    *  rest6 -> * unary             {rest6'.in=newtemp();
     *                               emit('*',rest6.in, unary.place, rest6'.in)}
     *              rest6'           {rest6.place= rest6'.place}
     *
     *          | / unary           {rest6'.in=newtemp();
     *                               emit('/',rest6.in, unary.place, rest6'.in)}
     *              rest6'           {rest6.place= rest6'.place}
     *
     *          |ε                  {rest6.place = rest6.in}
    * */
    public String rest6(String rest6In) {
        if (token != null && 43 == token.getSortCode()) {//   * unary rest6
            productionSteps.add("rest6 -> * term rest6");

            analyticStep.pollFirst();
            analyticStep.addFirst("rest6");
            analyticStep.addFirst("unary");
            assistance.add(token.getText());
            record();

            this.read();

            String arg2 = unary();
            String newTemp = newTemp();     //{rest6'.in=newtemp();
            emit("*",rest6In,arg2,newTemp);    //emit('*',rest6.in, unary.place, rest6'.in)}

            return rest6(newTemp);  //{rest6.place= rest6'.place}

        } else if (token != null && 44 == token.getSortCode()) {//  / unary rest6
            productionSteps.add("rest6 -> / term rest6");

            analyticStep.pollFirst();
            analyticStep.addFirst("rest6");
            analyticStep.addFirst("unary");
            assistance.add(token.getText());
            record();

            this.read();

            String arg2 = unary();
            String newTemp = newTemp();     //{rest6'.in=newtemp();
            emit("/",rest6In,arg2,newTemp);    //emit('/',rest6.in, unary.place, rest6'.in)}

            return rest6(newTemp);  //{rest6.place= rest6'.place}

        } else {
            productionSteps.add("rest6 -> ε");
            analyticStep.pollFirst();
            record();

            return rest6In;    //{rest6.place = rest6.in}
        }
    }

    /*
     * unary -> factor    {unary.place = factor.place}
     * */
    public String unary() {
        productionSteps.add("unary -> factor");
        analyticStep.pollFirst();
        analyticStep.addFirst("factor");
        record();

        return factor();    // {unary.place = factor.place}
    }

    /*
     * factor ->  (expr) | num | loc
     *
     * factor -> ( expr )           {unary.place = expr.palce}
     *
     *          | loc               {factor.place = loc.place}
     *
     *          | num               {factor.place = num.value}
     * */
    public String factor() {
        if (token != null && 81 == token.getSortCode()) {//(
            productionSteps.add("factor -> (expr)");

            assistance.add(token.getText());
            analyticStep.pollFirst();
            analyticStep.addFirst("expr)");
            this.read();

            String temp = expr();

            if (token != null && 82 == token.getSortCode()) {//)
                assistance.add(token.getText());
                analyticStep.pollFirst();
                record();

                this.read();
            }

            return temp;    //{unary.place = expr.palce}

        } else if (token != null && 100 == token.getSortCode()) {
            productionSteps.add("factor -> num");

            assistance.add(token.getText());
            analyticStep.pollFirst();
            record();

            String factorPlace = token.getText();

            //已匹配，更新单词
            this.read();

            return factorPlace;     //{factor.place = num.value}

        } else if (token != null) {
            productionSteps.add("factor -> loc");

            analyticStep.pollFirst();
            analyticStep.addFirst("loc");
            record();

            return loc();   //{factor.place = loc.place}
        }
        return null;
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
     * stmt -> loc = expr ; {emit( '=' , expr.place , '-' , loc.place);}
     *      | if ( bool ) stmt else stmt
     *      | while ( bool ) stmt
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
            String exprPlace = "null";
            productionSteps.add("stmt -> loc = expr ;");

            analyticStep.pollFirst();
            analyticStep.addFirst(";");
            analyticStep.addFirst("expr");
            analyticStep.addFirst("=");
            analyticStep.addFirst("loc");
            record();

            String locPlace = loc();
            if (token!=null&&46==token.getSortCode()){//=
                assistance.add(token.getText());
                analyticStep.pollFirst();
                record();
                this.read();

                exprPlace = expr();

                if (token!=null&&84==token.getSortCode()) {//;
                    assistance.add(token.getText());
                    analyticStep.pollFirst();
                    record();
                    this.read();
                }
            }
            emit("=",exprPlace,"-",locPlace);   //{emit( '=' , expr.place , '-' , loc.place);}
        }
    }

    /*
    * loc -> id resta
    *
    * loc -> id            {resta.inArray=id.place}
    *        resta         {loc.place=resta.place;}
    * */
    public String loc(){
        String restaPlace = null;
        if (token!=null&&111==token.getSortCode()){
            productionSteps.add("loc -> id resta");
            assistance.add(token.getText());
            analyticStep.pollFirst();
            analyticStep.addFirst("resta");
            record();
            String idPlace = token.getText();
            this.read();

            restaPlace = resta(idPlace);    //{resta.inArray=id.place}

        }
        return restaPlace;  //{loc.place=resta.place;}
    }

    /*
    * resta -> [ elist ] | ε
    *
    * resta -> [ elist ]
     *          | ε         {resta.place=resta.inArray;}
    * */
    public String resta(String restaIn){
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
            return restaIn;     //{resta.place=resta.inArray;}
        }
        return restaIn;     //{resta.place=resta.inArray;}
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
    public void displayQuadruple(){
        for(List<String> list :quadruple){
            for(String str : list){
                System.out.print(str+" ");
            }
            System.out.println();
        }
    }
}
