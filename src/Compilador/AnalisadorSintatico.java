/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Compilador;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
    private Map<String, Escopo> escopos;
    private String escopoAtual;
    private boolean isBoolean;
    private String variavel;
    private int cont;
    private List<Codigo> executavel;
    private int endereco;
    private int labelCont;
    private String nomeProg;

    public ArrayList<Erro> getErros() {
        return erros;
    }

    private AnalisadorSintatico() {
        this.lex = AnalisadorLexico.getInstance();
        this.sinc = new LinkedList();
        this.erros = new ArrayList();
        this.escopos = new HashMap();
        this.executavel = new ArrayList();
        this.nomeProg = null;
    }

    public static AnalisadorSintatico getInstance() {
        if (instance == null) {
            instance = new AnalisadorSintatico();
        }
        return instance;
    }

    private void erro(String tipo, String msg, Integer lin, Integer col) {

        this.erros.add(new Erro(tipo, msg, lin, col, false));
//       System.out.println(msg);
        this.lex.previousToken();
        Token tk = this.lex.nextToken();
        while (!this.sinc.contains(tk.getToken()) && this.lex.hasNext()) {
            tk = this.lex.nextToken();
        }
        this.lex.previousToken();

    }

    private void erroSemantico(String msg, Integer lin, Integer col, boolean tipoErro) {
        this.erros.add(new Erro("SEMÂNTICO", msg, lin, col, tipoErro));
    }

    public void criaEscopo(String esocopo, String pai) {
        HashMap<String, Simbolo> tab = new HashMap();
        tab.put("var_true", new Simbolo("true", "IDENTIFICADOR", "var", "boolean", 1, true));
        tab.put("var_false", new Simbolo("false", "IDENTIFICADOR", "var", "boolean", 0, true));
        tab.put("proc_read", new Simbolo("read", "IDENTIFICADOR", "proc", null, null, true));
        tab.put("proc_write", new Simbolo("write", "IDENTIFICADOR", "proc", null, null, true));
        Escopo esc = new Escopo(pai, tab);
        this.escopos.put(esocopo, esc);
    }

    public void programa() {//1
        this.erros.clear();
        this.escopos.clear();
        this.executavel.clear();
        this.endereco = 0;
        this.labelCont = 0;
        this.escopoAtual = "GLOBAL";
        criaEscopo("GLOBAL", null);
        this.executavel.add(new Codigo(null, "INPP", null));
        if (lex.isEmpty()) {
            return;
        }
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
        this.nomeProg = this.lex.currentToken().getLexema();
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
        int numErro = this.erros.size();
        this.varUtilizada();
        this.executavel.add(new Codigo(null, "PARA", null));

        if (numErro == 0) {
            this.salvaExec();
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

            erro("SINTÁTICO", " Palavra reservada 'begin' esperada!\n",
                    tk.getLin(), tk.getColIni());
        }
        comandoComposto();
    }

    public void parteDeclaracoesVariaveis() {//3
        Token tk;
        String tipo;
        do {
            tipo = this.lex.currentToken().getLexema();
            declaracaoVariaveis(tipo);
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

    public void declaracaoVariaveis(String tipo) { //4
        Token tk;
        do {
            identificador();
            tk = this.lex.currentToken();
            if (verificaVar(tk, false)) {
                erroSemantico(" Variável já declarada!\n",
                        tk.getLin(), tk.getColIni(), false);
            } else {
                addVar(tk, tipo, "var");
                this.executavel.add(new Codigo(null, "AMEM", "1"));
                this.setEndereco(tk);

            }
            tk = this.lex.nextToken();

        } while (tk.getToken().equals("VIRGULA"));
        this.lex.previousToken();
    }

    private void listaIdentificadores() { //5
        Token tk;
        do {
            identificador();
            tk = this.lex.currentToken();
            if (verificaVar(tk, false)) {
                erroSemantico(" Variável já declarada!\n",
                        tk.getLin(), tk.getColIni(), false);
            } else {
                addVar(tk, null, "param_" + this.cont);
            }
            tk = this.lex.nextToken();
            this.cont++;
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
        Token tk, tkAux;
        String nomeEsc;
        String pai = this.escopoAtual;
        this.sinc.clear();
        this.sinc.add("P_ABRE");
        this.sinc.add("PONTO_VIRGULA");
        identificador();
        tkAux = this.lex.currentToken();
        if (verificaProc(tkAux, false)) {
            erroSemantico(" Procedimento já declarado!\n",
                    tkAux.getLin(), tkAux.getColIni(), false);
        } else if (!this.escopoAtual.equals("GLOBAL")) {

            erroSemantico("O Procedimento não pode ser declarado!\n",
                    tkAux.getLin(), tkAux.getColIni(), false);
        }
        nomeEsc = tkAux.getLexema();
        addProc(tkAux);
        criaEscopo(nomeEsc, this.escopoAtual);
        this.escopoAtual = nomeEsc;
        tk = this.lex.nextToken();
        if (tk.getToken().equals("P_ABRE")) {
            paramentrosFormais();
            tk = this.lex.nextToken();
        }
        if (tk.getToken().equals("PONTO_VIRGULA")) {
            bloco();
            this.escopoAtual = pai;
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
        this.cont = 0;
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
        } else {
            setTipo(tk.getLexema());
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
            this.lex.previousToken();
            tk = this.lex.currentToken();
            if (!verificaVar(this.lex.currentToken(), true)) {
                erroSemantico(" Variável não declarada!\n",
                        tk.getLin(), tk.getColIni(), false);
            } else {
                this.variavel = tk.getLexema();
                this.setUtilizada(tk, "var_");

            }
            Simbolo s = this.getVar(tk);
            this.lex.nextToken();

            expressao();
            if (verificaTipo() != isBoolean) {
                erroSemantico("Tipos incompatíveis na atribuição", tk.getLin(), tk.getColIni(), false);
            }
            this.executavel.add(new Codigo(null, "ARMZ", s.getValor().toString()));
        } else {
            this.lex.previousToken();
            chamadaProcedimento();
        }
    }

    private void chamadaProcedimento() {//13
        Token tk = this.lex.currentToken();
        if (!verificaProc(tk, true)) {
            erroSemantico(" Procedimento não declarado!\n",
                    tk.getLin(), tk.getColIni(), false);
        } else {
            this.setUtilizada(tk, "proc_");
        }
        Token token = tk;
        tk = this.lex.nextToken();
        if (tk.getToken().equals("P_ABRE")) {
            listaExpressoes(token);
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
            verificaParam(token, 0);
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
        int sai = this.labelCont++;
        expressao();//16  
        this.executavel.add(new Codigo(null, "DSVF", "L_" + sai));

        if (!isBoolean) {
            erroSemantico("A expressão deve ser booleana", tk.getLin(), tk.getColIni(), false);
        }
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
        comandoCondicional2(sai);
    }

    private void comandoCondicional2(int sai) {//14B
        Token tk = this.lex.nextToken();
        if (tk.getToken().equals("PALAVRA_RESERVADA_ELSE")) {
            int saiElse = this.labelCont++;
            this.executavel.add(new Codigo(null, "DSVS", "L_" + saiElse));
            this.executavel.add(new Codigo("L_" + sai, "NADA", null));
            comando();
            this.executavel.add(new Codigo("L_" + saiElse, "NADA", null));

        } else {
            this.lex.previousToken();
            this.executavel.add(new Codigo("L_" + sai, "NADA", null));

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
        int volta = this.labelCont++;
        this.executavel.add(new Codigo("L_" + volta, "NADA", null));
        expressao();//16

        int sai = this.labelCont++;
        this.executavel.add(new Codigo(null, "DSVF", "L_" + sai));
        if (!isBoolean) {
            erroSemantico("A expressão deve ser booleana", tk.getLin(), tk.getColIni(), false);
        }
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
        this.executavel.add(new Codigo(null, "DSVS", "L_" + volta));
        this.executavel.add(new Codigo("L_" + sai, "NADA", null));
    }

    private void expressao() {//16
        this.isBoolean = false;
        Token tk;
        Boolean isInt = expressaoSimples();
        tk = this.lex.nextToken();
        if (tk.getToken().contains("OP_REL")) { //relação 17
            if (expressaoSimples().booleanValue() != isInt.booleanValue()) {
                erroSemantico("Operação com tipos incompatíveis", tk.getLin(), tk.getColIni(), false);
            }

            switch (tk.getToken()) {
                case "OP_REL_MAIOR":
                    this.executavel.add(new Codigo(null, "CMMA", null));
                    break;
                case "OP_REL_MENOR":
                    this.executavel.add(new Codigo(null, "CMME", null));
                    break;

                case "OP_REL_MAIOR_IGUAL":
                    this.executavel.add(new Codigo(null, "CMAG", null));
                    break;

                case "OP_REL_MENOR_IGUAL":
                    this.executavel.add(new Codigo(null, "CMEG", null));
                    break;

                case "OP_REL_IGUAL":
                    this.executavel.add(new Codigo(null, "CMEG", null));
                    break;

                case "OP_REL_DIFERENTE":
                    this.executavel.add(new Codigo(null, "CMDG", null));
                    break;

            }
            this.isBoolean = true;
        } else {
            this.lex.previousToken();
        }

    }

    private Boolean expressaoSimples() {//18
        Token tk = this.lex.nextToken();
        boolean inv = false;
        if (!(tk.getToken().equals("OP_SOMA") || tk.getToken().equals("OP_SUB"))) {
            this.lex.previousToken();

        } else {
            if (tk.getToken().equals("OP_SUB")) {
                inv = true;
            }
        }
        Boolean isInt = termo();
        if (inv) {
            this.executavel.add(new Codigo(null, "INVR", null));
        }
        tk = this.lex.nextToken();
        while (tk.getToken().equals("OP_SOMA") || tk.getToken().equals("OP_SUB") || tk.getToken().equals("OP_OR")) {
            if (tk.getToken().equals("OP_OR")) {
                this.isBoolean = true;
            }
            if ((termo().booleanValue() != isInt.booleanValue()) || (tk.getToken().equals("OP_OR") && isInt) || (!tk.getToken().equals("OP_OR") && !isInt)) {
                erroSemantico("Operação com tipos incompatíveis", tk.getLin(), tk.getColIni(), false);
            }
            switch (tk.getToken()) {
                case "OP_SOMA":
                    this.executavel.add(new Codigo(null, "SOMA", null));
                    break;
                case "OP_SUB":
                    this.executavel.add(new Codigo(null, "SUBT", null));
                    break;
                case "OP_OR":
                    this.executavel.add(new Codigo(null, "DISJ", null));
                    break;
            }

            tk = this.lex.nextToken();
        }

        this.lex.previousToken();
        return isInt;

    }

    private Boolean termo() { //19
        Token tk;
        Boolean isInt = null;

        isInt = fator();
        tk = this.lex.nextToken();
        if (tk.getToken().equals("OP_AND")) {
            this.isBoolean = true;
        }
        while (tk.getToken().equals("OP_MULT") || tk.getToken().equals("OP_AND") || tk.getToken().equals("OP_DIV")) {
            if ((fator().booleanValue() != isInt.booleanValue()) || (tk.getToken().equals("OP_AND") && isInt) || (!tk.getToken().equals("OP_AND") && !isInt)) {
                erroSemantico("Operação com tipos incompatíveis", tk.getLin(), tk.getColIni(), false);
            }
            switch (tk.getToken()) {
                case "OP_MULT":
                    this.executavel.add(new Codigo(null, "MULT", null));
                    break;
                case "OP_DIV":
                    this.executavel.add(new Codigo(null, "DIVI", null));
                    break;
                case "OP_AND":
                    this.executavel.add(new Codigo(null, "CONJ", null));
                    break;
            }
            tk = this.lex.nextToken();
            if (tk.getToken().equals("OP_AND")) {
                this.isBoolean = true;
            }
        }
        this.lex.previousToken();
        return isInt;
    }

    private Boolean fator() {//20
        Token tk = this.lex.nextToken();
        Boolean isInt = false;
        if (tk.getToken().equals("IDENTIFICADOR")) {
            if (!verificaVar(this.lex.currentToken(), true)) {
                erroSemantico(" Variável não declarada!\n",
                        tk.getLin(), tk.getColIni(), false);
            } else {
                this.setUtilizada(tk, "var_");
                isInt = !verificaBoolean(tk);
                Simbolo s = this.getVar(tk);
                switch (s.getLexema()) {
                    case "true":
                        this.executavel.add(new Codigo(null, "CRCT", "1"));
                        break;
                    case "false":
                        this.executavel.add(new Codigo(null, "CRCT", "0"));
                        break;
                    default:
                        this.executavel.add(new Codigo(null, "CRVL", s.getValor().toString()));

                }
            }
        } else if (tk.getToken().equals("NUM_NAT")) {
            isInt = true;
            this.executavel.add(new Codigo(null, "CRCT", tk.getLexema()));

        } else if (tk.getToken().equals("P_ABRE")) {
            expressao();
            isInt = !isBoolean;
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

        } else if (tk.getToken().equals("OP_NOT")) {
            fator();
            this.executavel.add(new Codigo(null, "NEGA", null));
            isInt = false;
        } else {
            isInt = false;
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
        return isInt;
    }

    private void listaExpressoes(Token token) {//22
        Token tk;
        int contParam = 0;
        Boolean isInt = null;
        do {
            if (token.getLexema().equals("read")) {
                tk = this.lex.nextToken();
                if (tk.getToken().equals("IDENTIFICADOR")) {
                    Simbolo s = this.getVar(tk);
                    if (s == null) {
                        this.erroSemantico("A variável não foi declarada", tk.getLin(), tk.getColIni(), false);
                    } else {
                        this.setUtilizada(tk, "var_");
                        this.executavel.add(new Codigo(null, "LEIT", null));
                        this.executavel.add(new Codigo(null, "ARMZ", s.getValor().toString()));
                    }
                } else {
                    this.sinc.clear();
                    this.sinc.add("VIRGULA");
                    this.sinc.add("P_FECHA");
                    this.erro("Sintático", "Os argumentos da função read devem ser identificadores", tk.getLin(), tk.getColIni());
                }
            } else {
                expressao();
            }
            tk = this.lex.nextToken();
            if (isInt == null) {
                isInt = !this.isBoolean;
            }
            if (token.getLexema().equals("read") || token.getLexema().equals("write")) {
                if (isInt == this.isBoolean) {
                    erroSemantico("Tipo incompatível no parâmetro " + (contParam + 1) + " do procedimento " + token.getLexema(), tk.getLin(), tk.getColIni(), false);
                }
                if (token.getLexema().equals("write")) {
                    this.executavel.add(new Codigo(null, "IMPR", null));
                }
            } else {
                verificaPar(token, contParam);
            }

            contParam++;
        } while (tk.getToken().equals("VIRGULA"));
        if (!token.getLexema().equals("read") && !token.getLexema().equals("write")) {
            verificaParam(token, contParam);
        }
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

    private void setUtilizada(Token tk, String tipo) {
        Escopo escopo = this.escopos.get(this.escopoAtual);
        HashMap<String, Simbolo> tab = escopo.getTab();
        String esc = escopo.getPai();
        boolean resp = tab.containsKey(tipo + tk.getLexema());
        while (esc != null && !resp) {
            escopo = this.escopos.get(esc);
            tab = escopo.getTab();
            resp = tab.containsKey(tipo + tk.getLexema());
            esc = escopo.getPai();
        }
        if (resp) {
            tab.get(tipo + tk.getLexema()).setUtilizada(true);
        }
    }

    private boolean verificaVar(Token tk, boolean encadeada) {
        Escopo escopo = this.escopos.get(this.escopoAtual);
        HashMap tab = escopo.getTab();
        boolean resp = tab.containsKey("var_" + tk.getLexema());
        String esc = escopo.getPai();
        if (encadeada) {
            while (esc != null && !resp) {
                escopo = this.escopos.get(esc);
                tab = escopo.getTab();
                resp = tab.containsKey("var_" + tk.getLexema());
                esc = escopo.getPai();
            }
        }
        return resp;
    }

    private boolean verificaProc(Token tk, boolean encadeada) {
        Escopo escopo = this.escopos.get(this.escopoAtual);
        HashMap tab = escopo.getTab();
        boolean resp = tab.containsKey("proc_" + tk.getLexema());
        String esc = escopo.getPai();
        if (encadeada) {
            while (esc != null && !resp) {
                escopo = this.escopos.get(esc);
                tab = escopo.getTab();
                resp = tab.containsKey("proc_" + tk.getLexema());
                esc = escopo.getPai();
            }
        }
        return resp;
    }

    private void addVar(Token tk, String tipo, String cat) {
        HashMap tab = this.escopos.get(this.escopoAtual).getTab();
        tab.put("var_" + tk.getLexema(), new Simbolo(tk.getLexema(), tk.getToken(), cat, tipo, 0, false));
    }

    private void addProc(Token tk) {
        HashMap tab = this.escopos.get(this.escopoAtual).getTab();
        tab.put("proc_" + tk.getLexema(), new Simbolo(tk.getLexema(), tk.getToken(), "var", null, 0, false));
    }

    private void varUtilizada() {
        for (String str1 : escopos.keySet()) {
            HashMap<String, Simbolo> tab = this.escopos.get(str1).getTab();
            for (String str2 : tab.keySet()) {
                Simbolo s = tab.get(str2);
                if (!s.isUtilizada()) {
                    erroSemantico("O símbolo " + s.getLexema() + " no escopo " + str1 + " não foi utilazado", null, null, true);
                }
            }
        }
    }

    private boolean verificaBoolean(Token tk) {
        if (tk.getLexema().equals("true") || tk.getLexema().equals("false")) {
            this.isBoolean = true;
            return true;
        }
        Escopo escopo = this.escopos.get(this.escopoAtual);
        HashMap<String, Simbolo> tab = escopo.getTab();
        boolean resp = tab.containsKey("var_" + tk.getLexema());
        String pai = escopo.getPai();
        while (!resp && pai != null) {
            escopo = this.escopos.get(pai);
            pai = escopo.getPai();
            tab = escopo.getTab();
            resp = tab.containsKey("var_" + tk.getLexema());
        }

        if (tab.get("var_" + tk.getLexema()).isBoolean()) {
            this.isBoolean = true;
            return true;
        }
        return false;

    }

    private boolean verificaTipo() {
        Escopo escopo = this.escopos.get(this.escopoAtual);
        HashMap<String, Simbolo> tab = escopo.getTab();
        boolean resp = tab.containsKey("var_" + this.variavel);
        String esc = escopo.getPai();

        while (esc != null && !resp) {
            escopo = this.escopos.get(esc);
            tab = escopo.getTab();
            resp = tab.containsKey("var_" + this.variavel);
            esc = escopo.getPai();
        }
        if (esc == null && resp == false) {
            return this.isBoolean;
        }
        return (tab.get("var_" + this.variavel).getTipo().equals("boolean"));
    }

    private void setTipo(String lexema) {
        Escopo escopo = this.escopos.get(this.escopoAtual);
        HashMap<String, Simbolo> tab = escopo.getTab();
        for (String str : tab.keySet()) {
            Simbolo s = tab.get(str);
            if (s.getTipo() == null) {
                s.setTipo(lexema);
            }
        }
    }

    private void verificaPar(Token tk, int cont) {
        String nomeProc = tk.getLexema();
        Escopo esc = this.escopos.get(nomeProc);
        if (esc == null) {
            return;
        }
        HashMap<String, Simbolo> tab = esc.getTab();
        boolean achou = false;
        for (String str : tab.keySet()) {
            Simbolo simb = tab.get(str);
            if (simb.getCat().equals("param_" + cont)) {
                achou = true;
                if (simb.getTipo().equals("boolean") != this.isBoolean) {
                    erroSemantico("Tipo incompatível no parâmetro " + (cont + 1), tk.getLin(), tk.getColIni(), false);
                } else {
                    break;
                }
            }
        }
        if (!achou) {
            erroSemantico("Número de parâmetros informados é maior que o númeor de parâmetros do procedimento", tk.getLin(), tk.getColIni(), false);

        }
    }

    private void verificaParam(Token tk, int cont) {
        String nomeProc = tk.getLexema();
        Escopo esc = this.escopos.get(nomeProc);
        if (esc == null) {
            return;
        }
        HashMap<String, Simbolo> tab = esc.getTab();
        for (String str : tab.keySet()) {
            Simbolo simb = tab.get(str);
            if (simb.getCat().equals("param_" + cont)) {
                erroSemantico("Número de parâmetros informados é menor que o númeor de parâmetros do procedimento", tk.getLin(), tk.getColIni(), false);
            }
        }
    }

    private void setEndereco(Token tk) {
        Escopo escopo = this.escopos.get(this.escopoAtual);
        HashMap tab = escopo.getTab();
        Simbolo s = (Simbolo) tab.get("var_" + tk.getLexema());
        s.setValor(this.endereco++);
    }

    private Simbolo getVar(Token tk) {
        Escopo escopo = this.escopos.get(this.escopoAtual);
        HashMap tab = escopo.getTab();
        Simbolo s;
        if (tab.containsKey("var_" + tk.getLexema())) {
            s = (Simbolo) tab.get("var_" + tk.getLexema());
        } else {
            s = null;
        }
        return s;
    }

    private void salvaExec() {

        try {
            FileOutputStream out = new FileOutputStream(new File(this.nomeProg + ".txt"));
            String linha, label, arg;
            for (Codigo c : this.executavel) {
                label = c.getLabel();
                arg = c.getArg();

                if (label == null) {
                    label = "";
                }
                if (arg == null) {
                    arg = "";
                }
                linha = label + "\t" + c.getCod() + "\t" + arg + "\r\n";
                out.write(linha.getBytes());
            }
        } catch (Exception e) {

        }
    }

    public String getNomeProg() {
        return nomeProg;
    }

}
