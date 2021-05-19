package test;

import com.whc.lexer.Lexer;
import com.whc.lexer.token.Token;

import java.io.*;

public class LexerTest {

    public static void main(String[] args) throws IOException {
        //Reader br = new BufferedReader(new InputStreamReader(System.in));
        File f = new File("C:\\Users\\whc\\Desktop\\编译原理\\lexer\\ParserTest.txt");
        InputStream in = new FileInputStream(f);
        Lexer lexer = new Lexer(in);
        try{
            while(true) {
                Token token = lexer.read();
                if(token!=null) {
                    System.out.println("< " + token.getText() + " , " + token.getProperty() + " >");
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
