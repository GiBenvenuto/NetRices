/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package al_calculadora;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 *
 * @author Gi
 */
public class AnalisadorLexico {

    private Hashtable<String, String> hash;
    private ArrayList<Token> tokens;

    public AnalisadorLexico() {
        this.hash = new Hashtable();
        this.hash.put("+", "OP_SOMA");
        this.hash.put("-", "OP_ SUB");
        this.hash.put("*", "OP_MULT");
        this.hash.put("/", "OP_DIV");
        this.hash.put("=", "OP_IGUAL");
        this.hash.put("(", "P_ABRE");
        this.hash.put(")", "P_FECHA");
        this.hash.put("program", "PALAVRA_RESERVADA");
        this.hash.put("begin", "PALAVRA_RESERVADA");
        this.hash.put("var", "PALAVRA_RESERVADA");
        this.hash.put("procedure", "PALAVRA_RESERVADA");
        this.hash.put("end", "PALAVRA_RESERVADA");
        this.hash.put("if", "PALAVRA_RESERVADA");
        this.hash.put("then", "PALAVRA_RESERVADA");
        this.hash.put("else", "PALAVRA_RESERVADA");
        this.hash.put("while", "PALAVRA_RESERVADA");
        this.hash.put("do", "PALAVRA_RESERVADA");
        //    this.hash.put("program", "PR_PROGRAM");

    }

    public boolean isReservada(String palavra) {
        return (this.hash.get(palavra) != null);
    }

    public boolean isEspaco(char c) {

        if (c == (char) 8 || c == (char) 32 || c == (char) 13) {
            return true;
        }
        return false;

    }

    public void lex(String entrada) {
        char caracter;
        boolean ponto = true;
        String auxTokens = "";
        tokens = new ArrayList();
        Token tk;
        int lin = 1, col = 1;
//        entrada = removeEspaco(entrada);
        for (int i = 0; i < entrada.length(); i++) {
            caracter = entrada.charAt(i);

            if (this.hash.get(String.valueOf(caracter)) != null || Character.isWhitespace(caracter)) { //Se for um caracter especial
                if (!auxTokens.equals("")) {
                    tk = analisaLexema(auxTokens, lin, col);
                    tokens.add(tk);
                    auxTokens = "";
                    ponto = true;
                }
                if (!Character.isWhitespace(caracter)) {
                    tk = analisaLexema(Character.toString(caracter), lin, col); //Analisa palavras reservdas
                    tokens.add(tk);
                } else if (caracter == (char) 13) {
                    lin++;
                    col = 0;
                }
                // Analisar o novo char

            } else {//Verificar se é um numero real ou natural
                if (Character.isDigit(caracter) || Character.isAlphabetic(caracter) || caracter == '_') {
                    auxTokens += caracter;
                }//end else trata numero
                else {
                    //verificar se é ponto
                    if (caracter == '.' && ponto) {
                        auxTokens += caracter;
                        ponto = false;
                    } else {
                        if (!auxTokens.equals("")) {
                            tk = analisaLexema(auxTokens, lin, col);
                            tokens.add(tk);
                            auxTokens = "";
                            ponto = true;
                        }
                        tk = analisaLexema(Character.toString(caracter), lin, col);
                        tokens.add(tk);
                    }

                }
            }
            col++;
        }
        if (!auxTokens.equals("")) {
            tk = analisaLexema(auxTokens, lin, col);
            tokens.add(tk);
        }

    }

    public String[][] toInterface(String entrada) {
        String[][] arrayTokens = new String[tokens.size()][5];
        StringTokenizer st;
        int i = 0;
        for (Token at : tokens) {
            arrayTokens[i][0] = at.getLexema();
            arrayTokens[i][1] = at.getToken();
            arrayTokens[i][2] = Integer.toString(at.getLin());
            arrayTokens[i][3] = Integer.toString(at.getColIni());
            arrayTokens[i][4] = Integer.toString(at.getColFim());
            i++;
        }

        return arrayTokens;
    }

    private Token analisaLexema(String auxTokens, int lin, int col) {
        Token tk = new Token();
        tk.setLexema(auxTokens);
        tk.setLin(lin);
        tk.setColIni(col - auxTokens.length());
        tk.setColFim(col);
        String letra = "(_|[a-z]|[A-Z])";

        if (this.hash.containsKey(auxTokens)) {
            tk.setToken(this.hash.get(auxTokens));
            //tk.setColIni(col-1);
        } else if (auxTokens.matches("^([0-9])+$")) {
            tk.setToken("NUM_NAT");
        } else if (auxTokens.matches("^([0-9])*\\.([0-9])+$")) {
            tk.setToken("NUM_REAL");
        } else if (auxTokens.matches("^"+letra+"("+letra+"|[0-9])*$")) {
            tk.setToken("IDENTIFICADOR");
        } else {
            tk.setToken("ERRO");
        }

        return tk;
    }
}
