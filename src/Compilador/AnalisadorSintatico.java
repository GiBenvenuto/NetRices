/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Compilador;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Melhor analisador sintático de todos os tempos. <br>
 *
 * @author Gi
 */
public class AnalisadorSintatico {

    private final AnalisadorLexico lex;
    private LinkedList<String> sinc;
    private ArrayList<Erro> erros;
    private static AnalisadorSintatico instance;

    public ArrayList<Erro> getErros() {
        return erros;
    }

    private AnalisadorSintatico() {
        this.lex = AnalisadorLexico.getInstance();
        this.sinc = new LinkedList();
        this.erros = new ArrayList();
    }

    public static AnalisadorSintatico getInstance() {
        if (instance == null) {
            instance = new AnalisadorSintatico();
        }
        return instance;
    }

    private void erro(String tipo, String msg, int lin, int col) {

        this.erros.add(new Erro(tipo, msg, lin, col, false));
//       System.out.println(msg);
        this.lex.previousToken();
        Token tk = this.lex.nextToken();
        while (!this.sinc.contains(tk.getToken()) && this.lex.hasNext()) {
            tk = this.lex.nextToken();
        }
        this.lex.previousToken();

    }

    public void programa() {//1
        this.erros.clear();
        Token tk = lex.nextToken();
        if (!tk.getToken().equals("PALAVRA_RESERVADA_PROGRAM")) {
            this.sinc.clear();
            this.sinc.add("IDENTIFICADOR");
            erro("SINTÁTICO", " palavra reservada 'program' esperado!\n",
                    tk.getLin(), tk.getColIni());
        }
        this.sinc.clear();
        this.sinc.add("PONTO_VIRGULA");
        identificador();
        tk = lex.nextToken();
        if (!tk.getToken().equals("PONTO_VIRGULA")) {
            this.sinc.clear();
            this.sinc.add("ID_TIPO");
            this.sinc.add("PALAVRA_RESERVADA_BEGIN");
            this.sinc.add("PALAVRA_RESERVADA_PROCEDURE");
            erro("SINTÁTICO", " ';' esperado!\n",
                    tk.getLin(), tk.getColIni());
        }
        bloco();
        tk = lex.nextToken();
        if (!tk.getToken().equals("PONTO_FIM_PROG")) {
            this.sinc.clear();
            this.sinc.add("PONTO_FIM_PROG");
            erro("SINTÁTICO", " '.' esperado!\n",
                    tk.getLin(), tk.getColIni());
        }

    }

    public void bloco() {//2
        Token tk = this.lex.nextToken();

        if (tk.getToken().equals("ID_TIPO")) {
            parteDeclaracoesVariaveis();
            tk = this.lex.nextToken();
        }

        if (tk.getToken().equals("PALAVRA_RESERVADA_PROCEDURE")) {
            parteDeclaracoesSubrotinas();
            tk = this.lex.nextToken();
        }

        if (!tk.getToken().equals("PALAVRA_RESERVADA_BEGIN")) {
            this.sinc.clear();
            this.sinc.add("IDENTIFICADOR");
            this.sinc.add("PALAVRA_RESERVADA_IF");
            this.sinc.add("PALAVRA_RESERVADA_WHILE");
            this.sinc.add("PALAVRA_RESERVADA_READ");

            erro("SINTÁTICO", " palavra reservada 'begin' esperada!\n",
                    tk.getLin(), tk.getColIni());
        }
        comandoComposto();
    }

    public void parteDeclaracoesVariaveis() {//3
        Token tk;
        do {
            declaracaoVariaveis();
            tk = this.lex.nextToken();
            if (!tk.getToken().equals("PONTO_VIRGULA")) {
                this.sinc.clear();
                this.sinc.add("PALAVRA_RESERVADA_PROCEDURE");
                this.sinc.add("PALAVRA_RESERVADA_BEGIN");
                this.sinc.add("ID_TIPO");
                erro("SINTÁTICO", " ';' esperado!\n",
                        tk.getLin(), tk.getColIni());
            }
            tk = this.lex.nextToken();

        } while (tk.getToken().equals("ID_TIPO"));
        this.lex.previousToken();
    }

    public void declaracaoVariaveis() { //4
        Token tk;
        do {
            identificador();
            tk = this.lex.nextToken();

        } while (tk.getToken().equals("VIRGULA"));
        this.lex.previousToken();
    }

    private void listaIdentificadores() { //5
        Token tk;
        do {
            identificador();
            tk = this.lex.nextToken();
        } while (tk.getToken().equals("VIRGULA"));
        this.lex.previousToken();
    }

    private void parteDeclaracoesSubrotinas() { //6
        Token tk;
        do {
            declaracaoProcedimento();
            tk = this.lex.nextToken();
            if (!tk.getToken().equals("PONTO_VIRGULA")) {
                this.sinc.clear();
                this.sinc.add("PALAVRA_RESERVADA_BEGIN");
                erro("SINTÁTICO", " ';' esperado!\n",
                        tk.getLin(), tk.getColIni());
            }
            tk = this.lex.nextToken();
        } while (tk.getToken().equals("PALAVRA_RESERVADA_PROCEDURE"));
        this.lex.previousToken();
    }

    public void declaracaoProcedimento() { //7
        Token tk;
        this.sinc.clear();
        this.sinc.add("P_ABRE");
        this.sinc.add("PONTO_VIRGULA");
        identificador();
        tk = this.lex.nextToken();
        if (tk.getToken().equals("P_ABRE")) {
            paramentrosFormais();
            tk = this.lex.nextToken();
        }
        if (tk.getToken().equals("PONTO_VIRGULA")) {
            bloco();
        } else {
            this.sinc.clear();
            this.sinc.add("ID_TIPO");
            this.sinc.add("PALAVRA_RESERVADA_BEGIN");
            this.sinc.add("PALAVRA_RESERVADA_PROCEDURE");
            erro("SINTÁTICO", " ';' esperado!\n",
                    tk.getLin(), tk.getColIni());
        }

    }

    public void paramentrosFormais() { //8
        Token tk;
        secaoParametrosFormais();
        tk = this.lex.nextToken();
        while (tk.getToken().equals("PONTO_VIRGULA")) {
            secaoParametrosFormais();
            tk = this.lex.nextToken();
        }
        if (!tk.getToken().equals("P_FECHA")) {
            this.sinc.clear();
            this.sinc.add("PONTO_VIRGULA");
            erro("SINTÁTICO", " ')' esperado!\n",
                    tk.getLin(), tk.getColIni());
        }
    }

    public void secaoParametrosFormais() { //9
        Token tk = this.lex.nextToken();
        if (!tk.getToken().equals("PALAVRA_RESERVADA_VAR")) {
            this.lex.previousToken();
        }
        listaIdentificadores();
        tk = this.lex.nextToken();
        if (!tk.getToken().equals("DOIS_PONTOS")) {
            this.sinc.clear();
            this.sinc.add("IDENTIFICADOR");
            erro("SINTÁTICO", " ':' esperado!\n",
                    tk.getLin(), tk.getColIni());
        }
        tk = this.lex.nextToken();
        if (!tk.getToken().equals("ID_TIPO")) {
            this.sinc.clear();
            this.sinc.add("PALAVRA_RESERVADA_VAR");
            this.sinc.add("IDENTIFICADOR");
            this.sinc.add("P_FECHA");
            erro("SINTÁTICO", " ';' esperado!\n",
                    tk.getLin(), tk.getColIni());
        }

    }

    private void comandoComposto() { // 10
        Token tk;
        do {
            comando();
            tk = this.lex.nextToken();

        } while (tk.getToken().equals("PONTO_VIRGULA"));
        if (!tk.getToken().equals("PALAVRA_RESERVADA_END")) {
            this.sinc.clear();
            this.sinc.add("PALAVRA_RESERVADA_END");
            this.sinc.add("PALAVRA_RESERVADA_ELSE");
            this.sinc.add("PONTO_FIM_PROG");
            this.sinc.add("PONTO_VIRGULA");
            erro("SINTÁTICO", " 'end' esperado!\n",
                    tk.getLin(), tk.getColIni());
            this.lex.nextToken();
        }

    }

    private void comando() { //11
        Token tk = this.lex.nextToken();
        if (tk.getToken().equals("IDENTIFICADOR")) {
            atr_chProc();
        } else if (tk.getToken().equals("PALAVRA_RESERVADA_BEGIN")) {
            comandoComposto();
        } else if (tk.getToken().equals("PALAVRA_RESERVADA_IF")) {
            comandoCondicional1();
        } else if (tk.getToken().equals("PALAVRA_RESERVADA_WHILE")) {
            comandoRepetitivo();
        } else {
            this.sinc.clear();
            this.sinc.add("PALAVRA_RESERVADA_END");
            this.sinc.add("PALAVRA_RESERVADA_ELSE");
            this.sinc.add("PONTO_VIRGULA");
            erro("SINTÁTICO", " comando não reconhecido !\n",
                    tk.getLin(), tk.getColIni());
        }

    }

    private void atr_chProc() { //12 adaptado
        Token tk = this.lex.nextToken();
        if (tk.getToken().equals("ATRIBUICAO")) {
            expressao();
        } else {
            this.lex.previousToken();
            chamadaProcedimento();
        }
    }

    private void chamadaProcedimento() {//13
        Token tk = this.lex.nextToken();
        if (tk.getToken().equals("P_ABRE")) {
            listaExpressoes();
            tk = this.lex.nextToken();
            if (!tk.getToken().equals("P_FECHA")) {
                this.sinc.clear();
                this.sinc.add("PALAVRA_RESERVADA_END");
                this.sinc.add("PALAVRA_RESERVADA_ELSE");
                this.sinc.add("PONTO_VIRGULA");
                erro("SINTÁTICO", " ')' esperado!\n",
                        tk.getLin(), tk.getColIni());
            }
        } else if (tk.getToken().equals("PONTO_VIRGULA")
                || tk.getToken().equals("PALAVRA_RESERVADA_END") || tk.getToken().equals("PALAVRA_RESERVADA_ELSE")) {
            this.lex.previousToken();
        } else {
            this.sinc.clear();
            this.sinc.add("PALAVRA_RESERVADA_END");
            this.sinc.add("PALAVRA_RESERVADA_ELSE");
            this.sinc.add("PONTO_VIRGULA");
            erro("SINTÁTICO", " comando não reconhecido!!\n",
                    tk.getLin(), tk.getColIni());
        }
    }

    private void comandoCondicional1() {//14A
        Token tk = this.lex.nextToken();

        if (!tk.getToken().equals("P_ABRE")) {
            this.sinc.clear();
            this.sinc.add(tk.getToken());
            erro("SINTÁTICO", " '(' esperado!\n",
                    tk.getLin(), tk.getColIni());
        }
        expressao();//16   
        tk = this.lex.nextToken();

        if (!tk.getToken().equals("P_FECHA")) {
            this.sinc.clear();
            this.sinc.add(tk.getToken());
            erro("SINTÁTICO", " ')' esperado!\n",
                    tk.getLin(), tk.getColIni());
        }

        tk = this.lex.nextToken();
        if (!tk.getToken().equals("PALAVRA_RESERVADA_THEN")) {
            this.sinc.clear();
            this.sinc.add("IDENTIFICADOR");
            this.sinc.add("PALAVRA_RESERVADA_IF");
            this.sinc.add("PALAVRA_RESERVADA_WHILE");
            this.sinc.add("PALAVRA_RESERVADA_BEGIN");

            erro("SINTÁTICO", " 'then' esperado!\n",
                    tk.getLin(), tk.getColIni());
        }
        comando();
        comandoCondicional2();
    }

    private void comandoCondicional2() {//14B
        Token tk = this.lex.nextToken();
        if (tk.getToken().equals("PALAVRA_RESERVADA_ELSE")) {
            comando();
        } else {
            this.lex.previousToken();
        }

    }

    private void comandoRepetitivo() {//15
        Token tk = this.lex.nextToken();
        if (!tk.getToken().equals("P_ABRE")) {
            this.sinc.clear();
            this.sinc.add(tk.getToken());
            erro("SINTÁTICO", " '(' esperado!\n",
                    tk.getLin(), tk.getColIni());
        }
        expressao();//16   
        tk = this.lex.nextToken();

        if (!tk.getToken().equals("P_FECHA")) {
            this.sinc.clear();
            this.sinc.add(tk.getToken());
            erro("SINTÁTICO", " ')' esperado!\n",
                    tk.getLin(), tk.getColIni());
        }
        tk = this.lex.nextToken();
        if (!tk.getToken().equals("PALAVRA_RESERVADA_DO")) {
            this.sinc.clear();
            this.sinc.add("IDENTIFICADOR");
            this.sinc.add("PALAVRA_RESERVADA_IF");
            this.sinc.add("PALAVRA_RESERVADA_WHILE");
            this.sinc.add("PALAVRA_RESERVADA_BEGIN");

            erro("SINTÁTICO", " 'do' esperado!\n",
                    tk.getLin(), tk.getColIni());
        }
        comando();
    }

    private void expressao() {//16
        Token tk;
        expressaoSimples();
        tk = this.lex.nextToken();
        if (tk.getToken().contains("OP_REL")) { //relação 17
            expressaoSimples();
        } else {
            this.lex.previousToken();
        }

    }

    private void expressaoSimples() {//18
        Token tk = this.lex.nextToken();
        if (!(tk.getToken().equals("OP_SOMA") || tk.getToken().equals("OP_SUB"))) {
            this.lex.previousToken();
        }
        termo();
        tk = this.lex.nextToken();
        while (tk.getToken().equals("OP_SOMA") || tk.getToken().equals("OP_SUB") || tk.getToken().equals("OP_OR")) {
            termo();
            tk = this.lex.nextToken();
        }

        this.lex.previousToken();

    }

    private void termo() { //19
        Token tk;
        do {
            fator();
            tk = this.lex.nextToken();
        } while (tk.getToken().equals("OP_MULT") || tk.getToken().equals("OP_AND") || tk.getToken().equals("OP_DIV"));
        this.lex.previousToken();
    }

    private void fator() {//20
        Token tk = this.lex.nextToken();
        if (tk.getToken().equals("IDENTIFICADOR")) {

        } else if (tk.getToken().equals("NUM_NAT")) {

        } else if (tk.getToken().equals("P_ABRE")) {
            expressao();
            tk = this.lex.nextToken();
            if (!tk.getToken().equals("P_FECHA")) {
                this.sinc.clear();
                this.sinc.add("OP_DIV");
                this.sinc.add("OP_OR");
                this.sinc.add("OP_SOMA");
                this.sinc.add("OP_SUB");
                this.sinc.add("OP_MULT");
                this.sinc.add("OP_REL_IGUAL");
                this.sinc.add("OP_REL_MENOR");
                this.sinc.add("OP_REL_MENOR_IGUAL");
                this.sinc.add("OP_REL_MAIOR");
                this.sinc.add("OP_REL_MAIOR_IGUAL");
                this.sinc.add("OP_REL_DIFERENTE");
                this.sinc.add("OP_REL_NOT");
                this.sinc.add("OP_REL_AND");
                this.sinc.add("PALAVRA_RESERVADA_END");
                this.sinc.add("PALAVRA_RESERVADA_ELSE");
                this.sinc.add("PALAVRA_RESERVADA_THEN");
                this.sinc.add("VIRGULA");
                this.sinc.add("PALAVRA_RESERVADA_DO");
                this.sinc.add("PONTO_VIRGULA");
                this.sinc.add("P_FECHA");

                erro("SINTÁTICO", " ')' esperado!\n",
                        tk.getLin(), tk.getColIni());
            }

        } else if (tk.getToken()
                .equals("OP_NOT")) {
            fator();
        } else {
            this.sinc.clear();
            this.sinc.add("OP_DIV");
            this.sinc.add("OP_OR");
            this.sinc.add("OP_SOMA");
            this.sinc.add("OP_SUB");
            this.sinc.add("OP_MULT");
            this.sinc.add("OP_REL_IGUAL");
            this.sinc.add("OP_REL_MENOR");
            this.sinc.add("OP_REL_MENOR_IGUAL");
            this.sinc.add("OP_REL_MAIOR");
            this.sinc.add("OP_REL_MAIOR_IGUAL");
            this.sinc.add("OP_REL_DIFERENTE");
            this.sinc.add("OP_REL_NOT");
            this.sinc.add("OP_REL_AND");
            this.sinc.add("PALAVRA_RESERVADA_END");
            this.sinc.add("PALAVRA_RESERVADA_ELSE");
            this.sinc.add("PALAVRA_RESERVADA_THEN");
            this.sinc.add("VIRGULA");
            this.sinc.add("PALAVRA_RESERVADA_DO");
            this.sinc.add("PONTO_VIRGULA");
            this.sinc.add("P_FECHA");

            erro("SINTÁTICO", " fator não reconhecido!\n",
                    tk.getLin(), tk.getColIni());
        }

    }

    private void listaExpressoes() {//22
        Token tk;
        do {
            expressao();
            tk = this.lex.nextToken();
        } while (tk.getToken().equals("VIRGULA"));
        this.lex.previousToken();

    }

    public void identificador() {//25
        Token tk = this.lex.nextToken();

        if (tk.getToken().equals("IDENTIFICADOR")) {
            return;
        } else {
            erro("SINTÁTICO", " 'IDENTIFICADOR' esperado!\n",
                    tk.getLin(), tk.getColIni());
        }
        return;
    }

}
