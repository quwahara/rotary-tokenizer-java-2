package a;

import java.util.ArrayList;
import java.util.List;

public class Terminologic {

    private Definition startDefinition;
    private Definition endDefinition;
    private List<Definition> definitions = new ArrayList<Definition>();
    private List<Terminologic> substitutes = new ArrayList<Terminologic>();

    public Terminologic add(String regex, String term) {
        definitions.add(new Definition().init(regex, term));
      return this;
    }

    public Definition getStartDefinition() {
        return startDefinition;
    }

    public void setStartDefinition(Definition startDefinition) {
        this.startDefinition = startDefinition;
    }

    public void setStartDefinition(String regex, String term) {
        setStartDefinition(new Definition().init(regex, term));
    }
    
    public Definition getEndDefinition() {
        return endDefinition;
    }

    public void setEndDefinition(Definition endDefinition) {
        this.endDefinition = endDefinition;
    }

    public void setEndDefinition(String regex, String term) {
        setEndDefinition(new Definition().init(regex, term));
    }
    
    public List<Definition> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(List<Definition> definitions) {
        this.definitions = definitions;
    }

    public List<Terminologic> getSubstitutes() {
        return substitutes;
    }

    public void setSubstitutes(List<Terminologic> substitutes) {
        this.substitutes = substitutes;
    }

}
