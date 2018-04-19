package pm.ftm.tonto.model;

import java.util.List;

public class Query {
    private List<Token> tokens;

    public List<Token> getTokens() {
        return tokens;
    }

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    public void addToken(Token token) {
        this.tokens.add(token);
    }

    public void removeToken(Token token) {
        this.tokens.remove(token);
    }
}
