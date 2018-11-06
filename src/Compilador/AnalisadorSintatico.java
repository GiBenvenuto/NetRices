/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Compilador;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
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

    public ArrayList<Erro> getErros() {
        return erros;
    }

    private AnalisadorSintatico() {
        this.lex = AnalisadorLexico.getInstance();
        this.sinc = new LinkedList();
        this.erros = new ArrayList();
        this.escopos = new HashMap();
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
        this.escopoAtual = "GLOBAL";
        this.escopos.clear();
        criaEscopo("GLOBAL", null);

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

        this.varUtilizada();

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
        Token tk, tkAux;
        do {
            identificador();
            tkAux = this.lex.currentToken();
            if (verificaVar(tkAux, false)) {
                erroSemantico(" Variável já declarada!\n",
                        tkAux.getLin(), tkAux.getColIni(), false);
            } else {
                addVar(tkAux, tipo);
            }
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
        Token tk, tkAux;
        String nomeEsc = null;
        this.sinc.clear();
        this.sinc.add("P_ABRE");
        this.sinc.add("PONTO_VIRGULA");
        identificador();
        tkAux = this.lex.currentToken();
        if (verificaProc(tkAux, false)) {
            erroSemantico(" Procedimento já declarado!\n",
                    tkAux.getLin(), tkAux.getColIni(), false);
        } else {
            nomeEsc = tkAux.getLexema();
            addProc(tkAux);
        }
        tk = this.lex.nextToken();
        if (tk.getToken().equals("P_ABRE")) {
            paramentrosFormais();
            tk = this.lex.nextToken();
        }
        if (tk.getToken().equals("PONTO_VIRGULA")) {
            String pai = this.escopoAtual;
            this.escopoAtual = nomeEsc;
            criaEscopo(this.escopoAtual, pai);
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
            this.lex.previousToken();
            tk = this.lex.currentToken();
            if (!verificaVar(this.lex.currentToken(), true)) {
                erroSemantico(" Variável não declarada!\n",
                        tk.getLin(), tk.getColIni(), false);
            } else {
                this.variavel = tk.getLexema();
                this.setUtilizada(tk, "var_");
            }
            this.lex.nextToken();

            expressao();
            if (verificaTipo() != isBoolean) {
                erroSemantico("Tipos incompatíveis", tk.getLin(), tk.getColIni(), false);
            }
        } else {
            this.lex.previousToken();
            tk = this.lex.currentToken();
            if (!verificaProc(tk, true)) {
                erroSemantico(" Procedimento não declarado!\n",
                        tk.getLin(), tk.getColIni(), false);
            }
            chamadaProcedimento();
        }
    }

    private void chamadaProcedimento() {//13
        Token tk = this.lex.currentToken();
        if (!verificaVar(tk, true)) {
            erroSemantico(" Procedimento não declarado!\n",
                    tk.getLin(), tk.getColIni(), true);
        } else {
            this.setUtilizada(tk, "proc_");
        }

        tk = this.lex.nextToken();
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
    }

    private void expressao() {//16
        this.isBoolean = false;
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
            if (tk.getToken().equals("OP_OR")) {
                this.isBoolean = true;
            }
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
            if (tk.getToken().equals("OP_AND")) {
                this.isBoolean = true;
            }
        } while (tk.getToken().equals("OP_MULT") || tk.getToken().equals("OP_AND") || tk.getToken().equals("OP_DIV"));
        this.lex.previousToken();
    }

    private void fator() {//20
        Token tk = this.lex.nextToken();
        if (tk.getToken().equals("IDENTIFICADOR")) {
            if (!verificaVar(this.lex.currentToken(), true)) {
                erroSemantico(" Variável não declarada!\n",
                        tk.getLin(), tk.getColIni(), false);
            } else {
                this.setUtilizada(tk, "var_");
                verificaBoolean(tk);
            }
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

    private void setUtilizada(Token tk, String tipo) {
        Escopo escopo = this.escopos.get(this.escopoAtual);
        HashMap<String, Simbolo> tab = escopo.getTab();
        tab.get(tipo + tk.getLexema()).setUtilizada(true);
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

    private void addVar(Token tk, String tipo) {
        HashMap tab = this.escopos.get(this.escopoAtual).getTab();
        tab.put("var_" + tk.getLexema(), new Simbolo(tk.getLexema(), tk.getToken(), "var", tipo, 0, false));
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

    private void verificaBoolean(Token tk) {
        if (tk.getLexema().equals("true") || tk.getLexema().equals("false")) {
            this.isBoolean = true;
            return;
        }
        Escopo escopo = this.escopos.get(this.escopoAtual);
        HashMap<String, Simbolo> tab = escopo.getTab();

        if (tab.get("var_" + tk.getLexema()).isBoolean()) {
            this.isBoolean = true;
        }

    }

    private boolean verificaTipo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
