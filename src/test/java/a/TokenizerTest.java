package a;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TokenizerTest {
    
    private Tokenizer tokenizer;
    private ObjectMapper mapper;        

    public TokenizerTest() {
        mapper = new ObjectMapper();        
        
        Settings settings = Settings.createDefaultSettings();
        tokenizer = new Tokenizer(settings);
    }
    
    public String nextFor(String text) {
        
        List<Token> tokens = new ArrayList<Token>();
        tokenizer.init().setText(text);
        try {
            for(Token token = tokenizer.next(); token != null; token = tokenizer.next()) {
                tokens.add(token);
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        
        try {
            return mapper.writeValueAsString(tokens);
        } catch (JsonProcessingException e) {
            return e.getMessage();
        }
    }
    
    public static String d(String s) {
        return s.replace('^', '"');
    }

    @Test
    public void next() {
        assertEquals("Zero length source", "[]", nextFor(""));
        assertEquals("Zero length source", d("[{^text^:^a^,^term^:^id^}]"), nextFor("a"));

        assertEquals("Zero length source",                d("[]"                                                                                                                                                     ) , nextFor(""));              
        assertEquals("One token, id",                     d("[{^text^:^a^,^term^:^id^}]"                                                                                                                             ) , nextFor("a"));             
        assertEquals("One token, blank 1",                d("[{^text^:^ ^,^term^:^blank^}]"                                                                                                                          ) , nextFor(" "));             
        assertEquals("LF",                                d("[{^text^:^\\n^,^term^:^blank^}]"                                                                                                                        ) , nextFor("\n"));            
        assertEquals("Two tokens, blank-blank",           d("[{^text^:^ ^,^term^:^blank^},{^text^:^ ^,^term^:^blank^}]"                                                                                            ) , nextFor("  "));            
        assertEquals("Two tokens, id-blank",              d("[{^text^:^a^,^term^:^id^},{^text^:^ ^,^term^:^blank^}]"                                                                                                 ) , nextFor("a "));            
        assertEquals("Three tokens, blank-blank-id",      d("[{^text^:^ ^,^term^:^blank^},{^text^:^ ^,^term^:^blank^},{^text^:^a^,^term^:^id^}]"                                                                    ) , nextFor("  a"));           
        assertEquals("Line comment, start only",          d("[{^text^:^//^,^term^:^lc-start^}]"                                                                                                                      ) , nextFor("//"));            
        assertEquals("Line comment, start-end",           d("[{^text^:^//^,^term^:^lc-start^},{^text^:^\\n^,^term^:^lc-end^}]"                                                                                       ) , nextFor("//\n"));          
        assertEquals("Line comment, start-body-end",      d("[{^text^:^//^,^term^:^lc-start^},{^text^:^xxx^,^term^:^lc-body^},{^text^:^\\n^,^term^:^lc-end^}]"                                                       ) , nextFor("//xxx\n"));       
        assertEquals("id lc-s lc-b lc-e",                 d("[{^text^:^aaa^,^term^:^id^},{^text^:^//^,^term^:^lc-start^},{^text^:^xxx^,^term^:^lc-body^},{^text^:^\\n^,^term^:^lc-end^}]"                            ) , nextFor("aaa//xxx\n"));    
        assertEquals("id lc-s lc-b lc-e",                 d("[{^text^:^aaa^,^term^:^id^},{^text^:^//^,^term^:^lc-start^},{^text^:^xxx^,^term^:^lc-body^},{^text^:^\\n^,^term^:^lc-end^},{^text^:^bbb^,^term^:^id^}]" ) , nextFor("aaa//xxx\nbbb")); 
        assertEquals("Block comment, end",                d("[{^text^:^*^,^term^:^symbol^},{^text^:^/^,^term^:^symbol^}]"                                                                                            ) , nextFor("*/"));            
        assertEquals("Block comment, start",              d("[{^text^:^/*^,^term^:^bc-start^}]"                                                                                                                      ) , nextFor("/*"));            
        assertEquals("Block comment, start",              d("[{^text^:^/*^,^term^:^bc-start^},{^text^:^/^,^term^:^bc-body^}]"                                                                                        ) , nextFor("/*/"));           
        assertEquals("Block comment, start-body",         d("[{^text^:^/*^,^term^:^bc-start^},{^text^:^*^,^term^:^bc-body^}]"                                                                                        ) , nextFor("/**"));           
        assertEquals("Block comment, start-end",          d("[{^text^:^/*^,^term^:^bc-start^},{^text^:^*/^,^term^:^bc-end^}]"                                                                                        ) , nextFor("/**/"));          
        assertEquals("Block comment, start-body-end 1",   d("[{^text^:^/*^,^term^:^bc-start^},{^text^:^*^,^term^:^bc-body^},{^text^:^*/^,^term^:^bc-end^}]"                                                          ) , nextFor("/***/"));         
        assertEquals("Block comment, start-body-end 2",   d("[{^text^:^/*^,^term^:^bc-start^},{^text^:^z^,^term^:^bc-body^},{^text^:^*/^,^term^:^bc-end^}]"                                                          ) , nextFor("/*z*/"));         
        assertEquals("Block comment, start-body-end 3",   d("[{^text^:^/*^,^term^:^bc-start^},{^text^:^z*^,^term^:^bc-body^},{^text^:^*/^,^term^:^bc-end^}]"                                                         ) , nextFor("/*z**/"));        
        assertEquals("Block comment, id-start-end",       d("[{^text^:^aaa^,^term^:^id^},{^text^:^/*^,^term^:^bc-start^},{^text^:^*/^,^term^:^bc-end^}]"                                                             ) , nextFor("aaa/**/"));       
        assertEquals("Block comment, start-end-id",       d("[{^text^:^/*^,^term^:^bc-start^},{^text^:^*/^,^term^:^bc-end^},{^text^:^bbb^,^term^:^id^}]"                                                             ) , nextFor("/**/bbb"));       
        assertEquals("Block comment, id-start-end-id",    d("[{^text^:^aaa^,^term^:^id^},{^text^:^/*^,^term^:^bc-start^},{^text^:^*/^,^term^:^bc-end^},{^text^:^bbb^,^term^:^id^}]"                                  ) , nextFor("aaa/**/bbb"));
        assertEquals("Double quotes, start",              d("[{^text^:^\\^^,^term^:^dq-start^}]")                                                                                                                      , nextFor("\""));             
        assertEquals("Double quotes, start-end",          d("[{^text^:^\\^^,^term^:^dq-start^},{^text^:^\\^^,^term^:^dq-end^}]")                                                                                       , nextFor("\"\""));            
        assertEquals("Double quotes, start-body 1",       d("[{^text^:^\\^^,^term^:^dq-start^},{^text^:^a^,^term^:^dq-body^}]")                                                                                        , nextFor("\"a"));            
        assertEquals("Double quotes, start-body-end 1",   d("[{^text^:^\\^^,^term^:^dq-start^},{^text^:^a^,^term^:^dq-body^},{^text^:^\\^^,^term^:^dq-end^}]")                                                         , nextFor("\"a\""));           
        assertEquals("Double quotes, start-body 2",       d("[{^text^:^\\^^,^term^:^dq-start^},{^text^:^\\\\\\^^,^term^:^dq-body^}]")                                                                                  , nextFor("\"\\\""));          
        assertEquals("Double quotes, start-body-end 2",   d("[{^text^:^\\^^,^term^:^dq-start^},{^text^:^\\\\\\^^,^term^:^dq-body^},{^text^:^\\^^,^term^:^dq-end^}]")                                                   , nextFor("\"\\\"\""));         
        assertEquals("Double quotes, start-body-end 3",   d("[{^text^:^\\^^,^term^:^dq-start^},{^text^:^\\\\\\\\^,^term^:^dq-body^},{^text^:^\\^^,^term^:^dq-end^}]")                                                  , nextFor("\"\\\\\""));      
        assertEquals("Double quotes, start-body 3",       d("[{^text^:^\\^^,^term^:^dq-start^},{^text^:^\\\\\\\\\\\\\\^^,^term^:^dq-body^}]")                                                                          , nextFor("\"\\\\\\\""));      
        assertEquals("Double quotes, start-body-end 4",   d("[{^text^:^\\^^,^term^:^dq-start^},{^text^:^\\\\\\\\\\\\\\^^,^term^:^dq-body^},{^text^:^\\^^,^term^:^dq-end^}]")                                           , nextFor("\"\\\\\\\"\""));     
        assertEquals("Single quotes, start",              d("[{^text^:^\'^,^term^:^sq-start^}]")                                                                                                                       , nextFor("'"));             
        assertEquals("Single quotes, start-end",          d("[{^text^:^\'^,^term^:^sq-start^},{^text^:^\'^,^term^:^sq-end^}]")                                                                                         , nextFor("''"));            
        assertEquals("Single quotes, start-body 1",       d("[{^text^:^\'^,^term^:^sq-start^},{^text^:^a^,^term^:^sq-body^}]")                                                                                         , nextFor("'a"));            
        assertEquals("Single quotes, start-body-end 1",   d("[{^text^:^\'^,^term^:^sq-start^},{^text^:^a^,^term^:^sq-body^},{^text^:^\'^,^term^:^sq-end^}]")                                                           , nextFor("'a'"));           
        assertEquals("Single quotes, start-body 2",       d("[{^text^:^\'^,^term^:^sq-start^},{^text^:^\\\\\'^,^term^:^sq-body^}]")                                                                                    , nextFor("'\\'"));          
        assertEquals("Single quotes, start-body-end 2",   d("[{^text^:^\'^,^term^:^sq-start^},{^text^:^\\\\\'^,^term^:^sq-body^},{^text^:^\'^,^term^:^sq-end^}]")                                                      , nextFor("'\\''"));         
        assertEquals("Single quotes, start-body-end 3",   d("[{^text^:^\'^,^term^:^sq-start^},{^text^:^\\\\\\\\^,^term^:^sq-body^},{^text^:^\'^,^term^:^sq-end^}]")                                                    , nextFor("'\\\\'"));        
        assertEquals("Single quotes, start-body 3",       d("[{^text^:^\'^,^term^:^sq-start^},{^text^:^\\\\\\\\\\\\\'^,^term^:^sq-body^}]")                                                                            , nextFor("'\\\\\\'"));      
        assertEquals("Single quotes, start-body-end 4",   d("[{^text^:^\'^,^term^:^sq-start^},{^text^:^\\\\\\\\\\\\\'^,^term^:^sq-body^},{^text^:^\'^,^term^:^sq-end^}]")                                              , nextFor("'\\\\\\''"));
    }
    
    @Test
    public void noDefinitionMatched() {
        Settings settings = new Settings();
        Terminologic rootTerminologic = new Terminologic();
        rootTerminologic.add("nomutch", "nomutch");
        settings.setRootTerminologic(rootTerminologic);
        Tokenizer tokenizer = new Tokenizer(settings);
        try {
            tokenizer.init().setText("a");
            tokenizer.next();
            fail("No definition matched");
        } catch (Exception e) {
//            System.out.println(e.getMessage());
        }
    }

    @Test
    public void matchedTextLengthWasZero() {
        Settings settings = new Settings();
        Terminologic rootTerminologic = new Terminologic();
        rootTerminologic.add("", "zero-length");
        settings.setRootTerminologic(rootTerminologic);
        Tokenizer tokenizer = new Tokenizer(settings);
        try {
            tokenizer.init().setText("a");
            tokenizer.next();
            fail("Matched text length was zero");
        } catch (Exception e) {
//            System.out.println(e.getMessage());
        }
    }

}
