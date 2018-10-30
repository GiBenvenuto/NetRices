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
    private final int lin;
    private final int col;
    private final boolean warning;

    public Erro(String tipo, String msg, int lin, int col, boolean warning) {
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

    public int getLin() {
        return lin;
    }

    public int getCol() {
        return col;
    }

    public boolean isWarning() {
        return warning;
    }
    
}
