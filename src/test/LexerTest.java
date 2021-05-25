package test;

import com.whc.lexer.Lexer;
import com.whc.lexer.token.*;

import java.io.*;

public class LexerTest {

    public static void main(String[] args) throws IOException {

        File f = new File("C:\\Users\\whc\\Desktop\\编译原理\\lexer\\LexerTest.txt");
        InputStream in = new FileInputStream(f);
        Lexer lexer = new Lexer(in);
        try{
            while(true) {
                Token token = lexer.read();
                if(token!=null) {
                    System.out.print("< " + token.getText() + " , ");
                    if(token instanceof NumToken){
                        System.out.println(((NumToken) token).getProperty()+" >");
                    }else if (token instanceof IdToken){
                        System.out.println(((IdToken) token).getProperty()+" >");
                    }else if (token instanceof DelimiterToken){
                        System.out.println(((DelimiterToken) token).getProperty()+" >");
                    }else if (token instanceof OperatorToken){
                        System.out.println(((OperatorToken) token).getProperty()+" >");
                    }
                }else {
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }finally {
            in.close();
        }

    }
}
