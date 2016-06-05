package a;


public class Token {
    private String text;
    private String term;
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public String getTerm() {
        return term;
    }
    public void setTerm(String term) {
        this.term = term;
    }
    public Token init(String text, String term) {
        setText(text);
        setTerm(term);
        return this;
    }
    @Override
    public String toString() {
        return U.toJSON(this);
    }
}
