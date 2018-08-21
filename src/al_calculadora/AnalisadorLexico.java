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
    private boolean ponto;

    public AnalisadorLexico() {
        this.hash = new Hashtable();
        this.hash.put("+", "OP_SOMA");
        this.hash.put("-", "OP_ SUB");
        this.hash.put("*", "OP_MULT");
        this.hash.put("=", "OP_IGUAL");
        this.hash.put("(", "P_ABRE");
        this.hash.put(")", "P_FECHA");
        this.hash.put(";", "PONTO_VIRGULA");
        this.hash.put(":=", "ATRIBUICAO");
        this.hash.put("<", "OP_MENOR");
        this.hash.put(">", "OP_MAIOR");
        this.hash.put("<=", "OP_MENOR_IGUAL");
        this.hash.put(">=", "OP_MAIOR_IGUAL");
        this.hash.put("<>", "OP_DIFERENTE");
        this.hash.put("or", "OP_OR");
        this.hash.put("not", "OP_NOT");
        this.hash.put("and", "OP_AND");
        this.hash.put("div", "OP_DIV");
        this.hash.put(",", "VIRGULA");
        this.hash.put("true", "ID_CONST"); //Verificar nomenclatura
        this.hash.put("false", "ID_CONST");
        this.hash.put("int", "ID_TIPO");
        this.hash.put("boolean", "ID_TIPO");
        this.hash.put("read", "ID_PROC");
        this.hash.put("write", "ID_PROC");
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
        this.hash.put("//", "COMENTARIO");
        this.hash.put("{", "ERRO_NÃO_FECHOU");
        this.hash.put("}", "ERRO_NÃO_ABRIU");

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

    public String analisa(String auxTokens, int lin, int col) {

        if (!auxTokens.equals("")) {
            Token tk = analisaLexema(auxTokens, lin, col);
            tokens.add(tk);
            auxTokens = "";
            ponto = true;
        }
        return auxTokens;
    }

    public void lex(String entrada) {
        char caracter;
        ponto = true;
        short comentario = 0;//0 - nada, 1 - linha, 2 - bloco
        String auxTokens = "";
        tokens = new ArrayList();
        Token tk;
        entrada += " ";
        int lin = 1, col = 0;
//        entrada = removeEspaco(entrada);
        for (int i = 0; i < entrada.length(); i++) {
            col++;
            caracter = entrada.charAt(i);
            if (caracter == (char) 13) {
                auxTokens = analisa(auxTokens, lin, col);
                if (comentario == 1) {
                    comentario = 0;
                }
                lin++;
                col = -1;
                continue;
            }
            if (comentario == 1) {
                col--;
                continue;
            }
            if (comentario == 2) {
                if (caracter == '}') {
                    comentario = 0;
                }
                continue;
            }
            if (this.hash.containsKey(String.valueOf(caracter)) || Character.isWhitespace(caracter)) { //Se for um caracter especial
                auxTokens = analisa(auxTokens, lin, col);
                if (caracter == '{') {
                    comentario = 2;
                    auxTokens = "";
                    continue;
                }

                if (caracter == '<' && (entrada.charAt(i + 1) == '=' || entrada.charAt(i + 1) == '>')) {
                    tk = analisaLexema("<" + entrada.charAt(i + 1), lin, ++col + 1);
                    tokens.add(tk);
                    i++;

                } else if (caracter == '>' && entrada.charAt(i + 1) == '=') {
                    tk = analisaLexema(">=", lin, ++col + 1);
                    tokens.add(tk);
                    i++;

                } else if (!Character.isWhitespace(caracter)) {
                    tk = analisaLexema(Character.toString(caracter), lin, col); //Analisa palavras reservdas
                    tokens.add(tk);
                }
                // Analisar o novo char

            } else {//Verificar se é um numero real ou natural
                if (caracter == ':') {
                    auxTokens = analisa(auxTokens, lin, col);
                    if (entrada.charAt(i + 1) == '=') {
                        tk = analisaLexema(":=", lin, ++col + 1);
                        tokens.add(tk);
                        i++;
                    } else {
                        tk = analisaLexema(":", lin, col);
                        tokens.add(tk);
                    }
                } else if (Character.isJavaIdentifierPart(caracter)) {
                    auxTokens += caracter;
                }//end else trata numero
                else {

                    //verificar se é ponto
                    if (caracter == '.' && ponto) {
                        auxTokens += caracter;
                        ponto = false;
                    } else {
                        if (caracter == '/' && entrada.charAt(i + 1) == '/') {
                            comentario = 1;
                            if (!auxTokens.equals("")) {
                                col--;
                            }
                            continue;
                        } else {
                            auxTokens = analisa(auxTokens, lin, col);
                        }
                        tk = analisaLexema(Character.toString(caracter), lin, col);
                        tokens.add(tk);
                    }

                }
            }

        }
        if (comentario == 2) {
            tk = analisaLexema("{", lin, col);
            tokens.add(tk);
        }
        if (comentario == 1) {
            col++;
        }
        analisa(auxTokens, lin, col);

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
        } else if (auxTokens.matches("^" + letra + "(" + letra + "|[0-9])*$")) {
            tk.setToken("IDENTIFICADOR");
        } else {
            tk.setToken("ERRO - CARACTERE DESCONHECIDO");
        }

        return tk;
    }
}
