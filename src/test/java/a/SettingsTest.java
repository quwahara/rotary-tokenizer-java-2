package a;

import java.util.regex.Matcher;

import org.junit.Test;

import static org.junit.Assert.*;

public class SettingsTest {
    
    private Settings settings = Settings.createDefaultSettings();
    
    public Definition def(String term) {
        for(Definition de : settings.getRootTerminologic().getDefinitions()) {
            if(de.getTerm().equals(term)) {
                return de;
            }
        }
        return null;
    }
    
    public boolean matcher(String term, String text) {
        Definition d = def(term);
        if(d == null) {
            fail("No definition for the term:" + term);
        }
        Matcher matcher = d.getPattern().matcher(text);
        if(matcher == null) {
            fail("Couldn't create a matcherhe");
        }
        return matcher.lookingAt() && matcher.end() == text.length();
    }

    @Test
    public void patternDefinitions() {
        assertTrue("blank, s",    matcher("blank", " "));
        assertTrue("blank, t",    matcher("blank", "\t"));
        assertFalse("blank, s-s", matcher("blank", "  "));
        assertFalse("blank, s-t", matcher("blank", " \t"));
        assertTrue("id, a",       matcher("id", "a"));
        assertFalse("id, 0",      matcher("id", "0"));
        assertTrue("id, a-a",     matcher("id", "aa"));
        assertTrue("id, z-z",     matcher("id", "zz"));
        assertTrue("id, __",      matcher("id", "__"));
        assertTrue("id, A-A",     matcher("id", "AA"));
        assertTrue("id, Z-Z",     matcher("id", "ZZ"));
        assertTrue("id, a-0-9",   matcher("id", "a09"));
        assertTrue("symbol, ~",   matcher("symbol", "~"));
        assertTrue("symbol, !",   matcher("symbol", "!"));
        assertTrue("symbol, @",   matcher("symbol", "@"));
        assertTrue("symbol, #",   matcher("symbol", "#"));
        assertTrue("symbol, $",   matcher("symbol", "$"));
        assertTrue("symbol, %",   matcher("symbol", "%"));
        assertTrue("symbol, ^",   matcher("symbol", "^"));
        assertTrue("symbol, &",   matcher("symbol", "&"));
        assertTrue("symbol, *",   matcher("symbol", "*"));
        assertTrue("symbol, (",   matcher("symbol", "("));
        assertTrue("symbol, )",   matcher("symbol", ")"));
        assertTrue("symbol, +",   matcher("symbol", "+"));
        assertTrue("symbol, `",   matcher("symbol", "`"));
        assertTrue("symbol, -",   matcher("symbol", "-"));
        assertTrue("symbol, =",   matcher("symbol", "="));
        assertTrue("symbol, {",   matcher("symbol", "{"));
        assertTrue("symbol, }",   matcher("symbol", "}"));
        assertTrue("symbol, |",   matcher("symbol", "|"));
        assertTrue("symbol, [",   matcher("symbol", "["));
        assertTrue("symbol, ]",   matcher("symbol", "]"));
        assertTrue("symbol, \"",  matcher("symbol", "\\"));
        assertTrue("symbol, :",   matcher("symbol", ":"));
        assertTrue("symbol, ;",   matcher("symbol", ";"));
        assertTrue("symbol, <",   matcher("symbol", "<"));
        assertTrue("symbol, >",   matcher("symbol", ">"));
        assertTrue("symbol, ?",   matcher("symbol", "?"));
        assertTrue("symbol, ,",   matcher("symbol", ","));
        assertTrue("symbol, .",   matcher("symbol", "."));
        assertTrue("symbol, /",   matcher("symbol", "/"));        
    }
}
