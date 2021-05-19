package test;

import com.whc.lexer.Lexer;
import com.whc.parser.Parser;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ParserTest {
    @Test
    public void ParserTest() throws FileNotFoundException {
        File f = new File("C:\\Users\\whc\\Desktop\\编译原理\\lexer\\ParserTest.txt");
        InputStream in = new FileInputStream(f);
        Parser parser = new Parser(new Lexer(in));
        parser.bool();
        parser.displayProductionSteps();
        System.out.println();
        parser.displayAnalyticSteps();
    }
}
