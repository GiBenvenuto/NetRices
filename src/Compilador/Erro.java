/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Compilador;

/**
 *
 * @author fabio
 */
public class Erro {
    private final String tipo;
    private final String msg;
    private final Integer lin;
    private final Integer col;
    private final boolean warning;

    public Erro(String tipo, String msg, Integer lin, Integer col, boolean warning) {
        this.tipo = tipo;
        this.msg = msg;
        this.lin = lin;
        this.col = col;
        this.warning = warning;
    }

    public String getTipo() {
        return tipo;
    }

    public String getMsg() {
        return msg;
    }

    public Integer getLin() {
        return lin;
    }

    public Integer getCol() {
        return col;
    }

    public boolean isWarning() {
        return warning;
    }
    
}
