package pm.ftm.tonto.model;

import java.io.Serializable;

public final class Token implements Serializable {
    private String lexeme;
    private int tag;

    public String getLexeme() {
        return lexeme;
    }

    public void setLexeme(String lexeme) {
        this.lexeme = lexeme;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }
}
