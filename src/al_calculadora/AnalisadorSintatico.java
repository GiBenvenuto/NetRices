/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package al_calculadora;

import java.util.ArrayList;

/**
 *
 * @author Gi
 */
public class AnalisadorSintatico {
    private final AnalisadorLexico lex;
    private static AnalisadorSintatico instance;
    
    private AnalisadorSintatico(){
        this.lex = AnalisadorLexico.getInstance();
    }
    
    public static AnalisadorSintatico getInstance(){
        if (instance == null){
            instance = new AnalisadorSintatico();
        }
        return instance;
    }
    
    public void programa(){
        Token tk = lex.nextToken();
        if(tk.getLexema().equals("program")){
            identificador();
            tk = lex.nextToken();
            if (tk.getLexema().equals(";")){
                bloco();
                tk = lex.nextToken();
                if(tk.getLexema().equals(".")){
                    
                }else{
                    System.out.println("Erro");
                }
            }else{
                System.out.println("Erro");
            }
                
        }else{
            System.out.println("ERRO");
        }
    }

    private void identificador() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void bloco() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
    
}
