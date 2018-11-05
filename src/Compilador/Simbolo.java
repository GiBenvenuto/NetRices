package Compilador;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author fabio
 */
public class Simbolo {
    private String lexema;
    private String token;
    private String cat;
    private String tipo;
    private Integer valor;
    private boolean utilizada;

    public Simbolo(String lexema, String token, String cat, String tipo, Integer valor, boolean utilizada) {
        this.lexema = lexema;
        this.token = token;
        this.cat = cat;
        this.tipo = tipo;
        this.valor = valor;
        this.utilizada = utilizada;
    }

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

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getValor() {
        return valor;
    }

    public void setValor(Integer valor) {
        this.valor = valor;
    }

    public boolean isUtilizada() {
        return utilizada;
    }

    public void setUtilizada(boolean utilizada) {
        this.utilizada = utilizada;
    }
    
    
    
    
}
