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
    private ArrayList<String> tokens;

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

    public ArrayList lex(String entrada) {
        char caracter;
        String auxTokens = "";
        tokens = new ArrayList<>();

//        entrada = removeEspaco(entrada);
        for (int i = 0; i < entrada.length(); i++) {
            caracter = entrada.charAt(i);

            if (this.hash.get(String.valueOf(caracter)) != null) { //Se for um caracter especial
                tokens.add(caracter + ":" + this.hash.get(String.valueOf(caracter))); //Caracter lido:Token

            } else {//Verificar se é um numero real ou natural
                if (Character.isDigit(caracter)) {

                    while (Character.isDigit(caracter)) {
                        auxTokens += caracter;
                        i++;
                        if (i < entrada.length()) {
                            caracter = entrada.charAt(i);
                        } else {
                            break;
                        }//end else
                    }//end while

                    if (caracter != '.' || i > entrada.length()) {
                        tokens.add(auxTokens + ":" + "NUM_NAT"); //Numero natural:Token
                        auxTokens = "";
                        i--;

                    } else {
                        i++;
                        if (i < entrada.length()) {
                            caracter = entrada.charAt(i);
                            if (Character.isDigit(caracter)) {
                                auxTokens += "." + caracter;
                                i++;
                                
                                if (i < entrada.length()) {
                                    caracter = entrada.charAt(i);
                                    while (Character.isDigit(caracter)) {
                                        auxTokens += caracter;
                                        i++;
                                        if (i < entrada.length()) {
                                            caracter = entrada.charAt(i);
                                        } else {
                                            break;
                                        }//end else
                                    }//end while
                                }

                                tokens.add(auxTokens + ":" + "NUM_REAL"); //Numero real:Token
                                auxTokens = "";
                                i--;

                            }//end num real
                            else {//volta
                                tokens.add(auxTokens + ":" + "NUM_NAT"); //Numero natural:Token
                                auxTokens = "";
                                i -= 2;
                            }
                        }//end if tam entrada
                        else {
                            tokens.add("." + ":" + "ERRO - CARACTERE DESCONHECIDO"); //Numero real:Token
                            auxTokens = "";
                        }
                    }//end else real
                }//end else trata numero
                else {
                    if (!isEspaco(caracter)) {
                        tokens.add(caracter + ":" + "ERRO - CARACTERE DESCONHECIDO"); //Numero real:Token
                        auxTokens = "";
                    }

                }
            }
        }

        return tokens;
    }

    public String[][] toInterface(String entrada) {
        ArrayList<String> tokens = lex(entrada);
        String[][] arrayTokens = new String[tokens.size()][2];
        StringTokenizer st;
        int i = 0;
        for (String at : tokens) {
            st = new StringTokenizer(at, ":");
            arrayTokens[i][0] = st.nextToken();
            arrayTokens[i][1] = st.nextToken();
            i++;
        }

        return arrayTokens;
    }

}
