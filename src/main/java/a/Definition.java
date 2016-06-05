package a;

import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Definition {
    @JsonIgnore
    private Pattern pattern;
    private String regex;
    private String term;
    public Pattern getPattern() {
        return pattern;
    }
    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }
    public String getTerm() {
        return term;
    }
    public void setTerm(String term) {
        this.term = term;
    }
    public String getRegex() {
        return regex;
    }
    public void setRegex(String regex) {
        setPattern(Pattern.compile(regex));
        this.regex = regex;
    }
    public Definition init(String regex, String term) {
        setRegex(regex);
        setTerm(term);
        return this;
    }
    @Override
    public String toString() {
        return U.toJSON(this);
    }
}
