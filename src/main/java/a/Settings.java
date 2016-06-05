package a;

public class Settings {
    private Terminologic rootTerminologic;
    
    public static Settings createDefaultSettings() {
        Settings settings = new Settings();
        Terminologic root = new Terminologic();
        settings.rootTerminologic = root;
        root.add("\\s", "blank");
        root.add("[a-zA-Z_][a-zA-Z_0-9]*", "id");
        root.add("[~!@#$%^&*()+`\\-={}|\\[\\]\\\\:;<>?,./]", "symbol");
        // Line comment
        Terminologic lc = new Terminologic();
        lc.setStartDefinition("//", "lc-start");
        lc.setEndDefinition("(\\r\\n|\\r|\\n)", "lc-end");
        lc.add("[^\\r\\n]+", "lc-body");
        root.getSubstitutes().add(lc);
        // Block comment
        Terminologic bc = new Terminologic();
        bc.setStartDefinition("/\\*", "bc-start");
        bc.setEndDefinition("\\*/", "bc-end");
        bc.add("((?!\\*/).)+", "bc-body");
        root.getSubstitutes().add(bc);
        // Double quotes
        Terminologic dq = new Terminologic();
        dq.setStartDefinition("\"", "dq-start");
        dq.setEndDefinition("\"", "dq-end");
        dq.add("(\\\\.|[^\"])+", "dq-body");
        root.getSubstitutes().add(dq);
        // Single quotes
        Terminologic sq = new Terminologic();
        sq.setStartDefinition("\'", "sq-start");
        sq.setEndDefinition("\'", "sq-end");
        sq.add("(\\\\.|[^\'])+", "sq-body");
        root.getSubstitutes().add(sq);
        return settings;        
    }

    public Terminologic getRootTerminologic() {
        return rootTerminologic;
    }

    public void setRootTerminologic(Terminologic rootTerminologic) {
        this.rootTerminologic = rootTerminologic;
    }

}
