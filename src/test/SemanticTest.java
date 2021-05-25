package test;

import com.whc.lexer.Lexer;
import com.whc.parser.Parser;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class SemanticTest {
    @Test
    public void semanticTest() throws FileNotFoundException {
        File f = new File("C:\\Users\\whc\\Desktop\\编译原理\\lexer\\SemanticTest.txt");
        InputStream in = new FileInputStream(f);
        Parser parser = new Parser(new Lexer(in));
        parser.read();
        parser.stmts();

        parser.displayQuadruple();
    }
}
