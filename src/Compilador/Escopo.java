/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Compilador;

import java.util.HashMap;

/**
 *
 * @author fabio
 */
public class Escopo {
    private String pai;
    private HashMap<String,Simbolo> tab;

    public Escopo(String pai, HashMap<String, Simbolo> tab) {
        this.pai = pai;
        this.tab = tab;
    }

    public String getPai() {
        return pai;
    }

    public void setPai(String pai) {
        this.pai = pai;
    }

    public HashMap<String, Simbolo> getTab() {
        return tab;
    }

    public void setTab(HashMap<String, Simbolo> tab) {
        this.tab = tab;
    }
    
}
