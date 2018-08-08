/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package al_calculadora;

/**
 *
 * @author fabio
 */
public class Token {
    private String lexema;
    private String token;
    private int lin;
    private int col;

    public String getLexema() {
        return lexema;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getLin() {
        return lin;
    }

    public void setLin(int lin) {
        this.lin = lin;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }
    
    
}
