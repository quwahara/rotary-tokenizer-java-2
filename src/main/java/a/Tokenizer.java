package a;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;

public class Tokenizer {

    private Settings settings;
    private String text;
    private Terminologic terminologic;
    private int nextIndex;
    private Stack<Terminologic> restitutionStack = new Stack<Terminologic>();
    
    public Tokenizer(Settings settings) {
        this.settings = settings;
    }
    
    public Tokenizer init() {
        this.terminologic = settings.getRootTerminologic();
        nextIndex = 0;
        restitutionStack.clear();
        return this;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Token execDefinition(Definition d, String subtext) {
        Matcher matcher = d.getPattern().matcher(subtext);
        if(! matcher.lookingAt()) {
            return null;
        }
        return new Token().init(subtext.substring(matcher.start(), matcher.end()), d.getTerm());
    }

    public Token next() throws Exception {
        Map<Token, Definition> candidates = new LinkedHashMap<Token, Definition>();
        Token token = null;
        if(nextIndex >= text.length()) {
            return null;
        }
        String subtext = text.substring(nextIndex);
        if(terminologic.getEndDefinition() != null) {
            token = execDefinition(terminologic.getEndDefinition(), subtext); 
            if(token != null) {
                candidates.put(token, terminologic.getEndDefinition());
            }
        }
        for(Terminologic sub: terminologic.getSubstitutes()) {
            if(sub.getStartDefinition() != null) {
                token = execDefinition(sub.getStartDefinition(), subtext);
                if(token != null) {
                    candidates.put(token, sub.getStartDefinition());
                }
            }
        }
        for(Definition de: terminologic.getDefinitions()) {
            token = execDefinition(de, subtext);
            if(token != null) {
                candidates.put(token, de);
            }
        }
        if(candidates.size() == 0) {
            throw new Exception("No definition matched at " + nextIndex);
        }

        Map.Entry<Token, Definition> candidate = null;
        for(Map.Entry<Token, Definition> entry : candidates.entrySet()) {
            if(candidate == null) {
                candidate = entry;
            }
            else if(candidate.getKey().getText().length() < entry.getKey().getText().length()) {
                candidate = entry;
            }
        }
        int len = candidate.getKey().getText().length();
        if(len == 0) {
            throw new Exception("Matched text length was 0.");
        }
        nextIndex += len;
        if(candidate.getValue() == terminologic.getEndDefinition()) {
            if(settings.getRootTerminologic() != terminologic) {
                terminologic = restitutionStack.pop();
            }
        }
        else {
            for(Terminologic sub: terminologic.getSubstitutes()) {
                if(sub.getStartDefinition() == candidate.getValue()) {
                    restitutionStack.push(terminologic);
                    terminologic = sub;
                    break;
                }
            }
        }
        return candidate.getKey();
    }

}
