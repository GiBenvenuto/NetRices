/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Compilador;

import java.util.LinkedList;

/**
 * Melhor analisador sintático de todos os tempos. <br>
 *
 * @author Gi
 */
public class AnalisadorSintatico {

    private final AnalisadorLexico lex;
    private LinkedList<String> sinc;
    private static AnalisadorSintatico instance;

    private AnalisadorSintatico() {
        this.lex = AnalisadorLexico.getInstance();
        this.sinc = new LinkedList();
    }

    public static AnalisadorSintatico getInstance() {
        if (instance == null) {
            instance = new AnalisadorSintatico();
        }
        return instance;
    }

    private void erro(String msg) {
        
        System.out.println(msg);
        Token tk = lex.nextToken();
        while(!this.sinc.contains(tk.getToken())){
            tk = this.lex.nextToken();
        }
        this.lex.previousToken();

    }

    public void programa() {//1
        Token tk = lex.nextToken();
        if (!tk.getToken().equals("PALAVRA_RESERVADA_PROGRAM")) {
            this.sinc.clear();
            this.sinc.add("IDENTIFICADOR");
            erro("ERRO - palavra reservada 'program' não encontrada!");
        }
        this.sinc.clear();
        this.sinc.add("PONTO_VIRGULA");
        identificador();
        tk = lex.nextToken();
        if (!tk.getLexema().equals(";")) {
            this.sinc.clear();
            this.sinc.add("ID_TIPO");
            this.sinc.add("PALAVRA_RESERVADA_BEGIN");
            this.sinc.add("PALAVRA_RESERVADA_PROCEDURE");
            erro("ERRO - ';' ");
            
        }
        bloco();
        tk = lex.nextToken();
        if (tk.getLexema().equals(".")) {
            System.out.println("Sucesso");
        } else {
            System.out.println("ERRO");
        }

    }

    public void bloco() {//2
        Token tk = this.lex.nextToken();

        if (tk.getToken().equals("ID_TIPO")) {
            parteDeclaracoesVariaveis();
            tk = this.lex.nextToken();
        }

        if (tk.getLexema().equals("procedure")) {
            parteDeclaracoesSubrotinas();
            tk = this.lex.nextToken();
        }

        if (tk.getLexema().equals("begin")) {
            comandoComposto();
        } else {
            System.out.println("ERRO");
        }
    }

    public void parteDeclaracoesVariaveis() {//3
        Token tk;
        do {
            declaracaoVariaveis();
            tk = this.lex.nextToken();
            if (!tk.getToken().equals("PONTO_VIRGULA")) {
                System.out.println("ERRO");
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
                System.out.println("ERRO");
            }
            tk = this.lex.nextToken();
        } while (tk.getLexema().equals("procedure"));
        this.lex.previousToken();
    }

    public void declaracaoProcedimento() { //7
        Token tk;
        identificador();
        tk = this.lex.nextToken();
        if (tk.getToken().equals("P_ABRE")) {
            paramentrosFormais();
            tk = this.lex.nextToken();
        }
        if (tk.getToken().equals("PONTO_VIRGULA")) {
            bloco();
        } else {
            System.out.println("ERRO");
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
            System.out.println("ERRO");
        }
    }

    public void secaoParametrosFormais() { //9
        Token tk = this.lex.nextToken();
        if (!tk.getLexema().equals("var")) {
            this.lex.previousToken();
        }
        listaIdentificadores();
        tk = this.lex.nextToken();
        if (!tk.getToken().equals("DOIS_PONTOS")) {
            System.out.println("ERRO");
        }
        tk = this.lex.nextToken();
        if (!tk.getToken().equals("ID_TIPO")) {
            System.out.println("ERRO - 171");
        }

    }

    private void comandoComposto() { // 10
        Token tk;
        do {
            comando();
            tk = this.lex.nextToken();

        } while (tk.getToken().equals("PONTO_VIRGULA"));
        if (!tk.getLexema().equals("end")) {
            System.out.println("ERRO");
        }

    }

    private void comando() { //11
        Token tk = this.lex.nextToken();
        if (tk.getToken().equals("IDENTIFICADOR")) {
            atr_chProc();
        } else if (tk.getLexema().equals("begin")) {
            comandoComposto();
        } else if (tk.getLexema().equals("if")) {
            comandoCondicional1();
        } else if (tk.getLexema().equals("while")) {
            comandoRepetitivo();
        } else {
            System.out.println("ERRO");
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
                System.out.println("ERRO");
            }
        }
        this.lex.previousToken();
    }

    private void comandoCondicional1() {//14A
        Token tk;
        expressao();//16
        tk = this.lex.nextToken();
        if (!tk.getLexema().equals("then")) {
            System.out.println("ERRO");
        }
        comando();
        comandoCondicional2();
    }

    private void comandoCondicional2() {//14B
        Token tk = this.lex.nextToken();
        if (tk.getLexema().equals("else")) {
            comando();
        } else {
            this.lex.previousToken();
        }

    }

    private void comandoRepetitivo() {//15
        Token tk;
        expressao();
        tk = this.lex.nextToken();
        if (!tk.getLexema().equals("do")) {
            System.out.println("ERRO");
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
                System.out.println("ERRO - 312");
            }
        } else if (tk.getToken().equals("OP_NOT")) {
            fator();
        } else {
            System.out.println("ERRO - 317");
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
            System.out.println("ERRO-IDENTIFICADOR NÃO ENCONTRADO");
        }
        return;
    }

}
