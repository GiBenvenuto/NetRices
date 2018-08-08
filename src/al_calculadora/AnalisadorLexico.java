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
                //tokens.add(caracter + ":" + this.hash.get(String.valueOf(caracter))); //Caracter lido:Token
                if (!auxTokens.equals("")) {
                    tk = analisaLexema(auxTokens);
                    tokens.add(tk);
                    auxTokens = "";
                    ponto = true;
                }
                if (!Character.isWhitespace(caracter)) {
                    tk = analisaLexema(Character.toString(caracter));
                    tokens.add(tk); //Numero real:Token
                }
                // Analisar o novo char

            } else {//Verificar se é um numero real ou natural
                if (Character.isDigit(caracter)) {
                    auxTokens += caracter;
                }//end else trata numero
                else {
                    //verificar se é ponto
                    if (caracter == '.' && ponto) {
                        auxTokens += caracter;
                        ponto = false;
                    } else {
                        if (!auxTokens.equals("")) {
                            tk = analisaLexema(auxTokens);
                            tokens.add(tk);
                            auxTokens = "";
                            ponto = true;
                        }
                        tk = analisaLexema(Character.toString(caracter));
                        tokens.add(tk);
                    }

                }
            }
            col++;
        }
        if (!auxTokens.equals("")) {
            tk = analisaLexema(auxTokens);
            tokens.add(tk);
        }

    }

    public String[][] toInterface(String entrada) {
        String[][] arrayTokens = new String[tokens.size()][2];
        StringTokenizer st;
        int i = 0;
        for (Token at : tokens) {
            arrayTokens[i][0] = at.getLexema();
            arrayTokens[i][1] = at.getToken();
            i++;
        }

        return arrayTokens;
    }

    private Token analisaLexema(String auxTokens) {
        Token tk = new Token();
        tk.setLexema(auxTokens);

        if (this.hash.containsKey(auxTokens)) {
            tk.setToken(this.hash.get(auxTokens));
        } else if (auxTokens.matches("^([0-9])+$")) {
            tk.setToken("NUM_NAT");
        } else if (auxTokens.matches("^([0-9])*.([0-9])+$")) {
            tk.setToken("NUM_REAL");
        } else {
            tk.setToken("ERRO");
        }

        return tk;
    }
}
