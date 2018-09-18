/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Compilador;

/**
 * Melhor analisador sintático de todos os tempos. <br>
 *
 * @author Gi
 */
public class AnalisadorSintatico {

    private final AnalisadorLexico lex;
    private static AnalisadorSintatico instance;

    private AnalisadorSintatico() {
        this.lex = AnalisadorLexico.getInstance();
    }

    public static AnalisadorSintatico getInstance() {
        if (instance == null) {
            instance = new AnalisadorSintatico();
        }
        return instance;
    }

    public void programa() {//1
        Token tk = lex.nextToken();
        if (tk.getLexema().equals("program")) {
            identificador();
            tk = lex.nextToken();
            if (tk.getLexema().equals(";")) {
                bloco();
                tk = lex.nextToken();
                if (tk.getLexema().equals(".")) {
                    System.out.println("Sucesso");
                } else {
                    System.out.println("Erro");
                }
            } else {
                System.out.println("Erro");
            }

        } else {
            System.out.println("ERRO");
        }
    }

    public void bloco() {//2
        Token tk = this.lex.nextToken();
        if (tk.getToken().equals("ID_TIPO")) {
            parteDeclaracoesVariaveis();
            tk = this.lex.nextToken();
            if (tk.getLexema().equals("begin")) {
                comandoComposto();
            } else {
                System.out.println("Erro");
            }
        } else if (tk.getLexema().equals("procedure")) {
            parteDeclaracoesSubrotinas();
        } else if (tk.getLexema().equals("begin")) {
            comandoComposto();
        } else {
            System.out.println("Erro");
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
        Token tk = this.lex.nextToken();
        do {
            identificador();
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

    public void declaracaoProcedimento() {
        Token tk;
        identificador();
        tk = this.lex.nextToken();
        if (tk.getToken().equals("P_ABRE")) {
            paramentrosFormais();
        } else if (tk.getToken().equals("PONTO_VIRGULA")) {
            bloco();
        } else {
            System.out.println("Erro");
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

    private void parteDeclaracoesSubrotinas() { //6
        Token tk;
        do {
            declaracaoProcedimento();
            tk = this.lex.nextToken();
            if (!tk.getToken().equals("PONTO_VIRGULA")) {
                System.out.println("Erro");
                break;
            }
            tk = this.lex.nextToken();
        } while (tk.getLexema().equals("procedure"));
        this.lex.previousToken();
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
        if (tk.getLexema().equals("var")) {
            tk = this.lex.nextToken();
        }
        listaIdentificadores();
        tk = this.lex.nextToken();
        if (!tk.getToken().equals("DOIS_PONTOS")) {
            System.out.println("ERRO");
        }
        identificador();

    }

    private void listaIdentificadores() { //5
        Token tk;
        do {
            identificador();
            tk = this.lex.nextToken();
        } while (tk.getToken().equals("VIRGULA"));
        this.lex.previousToken();
    }

    public void atr_chProc() { //12 adaptado
        Token tk = this.lex.nextToken();
        if (tk.getToken().equals("ATRIBUICAO")) {
            expressao();
        } else {
            this.lex.previousToken();
            chamadaProcedimento();
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

    private void comandoCondicional1() {//14

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

    private void expressao() {//16
        Token tk;
        expressaoSimples();
        tk = this.lex.nextToken();
        if (tk.getToken().contains("OP_REL")) { //relação 17
            tk = this.lex.nextToken();
            expressaoSimples();
        } else {
            this.lex.previousToken();
        }

    }

    private void listaExpressoes() {
        Token tk;
        do {
            expressao();
            tk = this.lex.nextToken();
        } while (tk.getToken().equals("VIRGULA"));
        this.lex.previousToken();

    }

    private void expressaoSimples() {
        Token tk = this.lex.nextToken();
        if (tk.getToken().equals("OP_SOMA") || tk.getToken().equals("OP_SUB")) {
            tk = this.lex.nextToken();
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

    private void fator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
