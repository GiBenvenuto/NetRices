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
public class Codigo {
 private String label;
 private String cod;
 private String arg;

    public Codigo(String label, String cod, String arg) {
        this.label = label;
        this.cod = cod;
        this.arg = arg;
    }
 
 

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public String getArg() {
        return arg;
    }

    public void setArg(String arg) {
        this.arg = arg;
    }
 
 
}
