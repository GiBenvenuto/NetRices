/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Compilador;

import UI.Saida;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author Gi
 */
public class Interpretador implements Runnable {

    private int i, s;
    private List<Codigo> cod;
    private List<Integer> dados;
    private Saida saida;

    public Interpretador(JFrame pai) {
        this.cod = new ArrayList();
        this.dados = new ArrayList();
        this.i = 0;
        this.s = -1;
        this.saida = new Saida(pai, false);
        this.saida.getEntrada().setEditable(false);
        this.saida.setVisible(true);

    }

    public void leByteCode(String name) {
        String[] str;
        String[] strAux;
        try {
            BufferedReader arq = new BufferedReader(new FileReader(name + ".txt"));
            while (arq.ready()) {
                strAux = arq.readLine().split("\t");
                if (strAux.length == 2) {
                    str = new String[3];
                    str[0] = strAux[0];
                    str[1] = strAux[1];
                    str[2] = "";
                } else {
                    str = strAux;
                }
                this.cod.add(new Codigo(str[0], str[1], str[2]));
            }
            arq.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Interpretador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Interpretador.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void interpreta() {
        String str;
        Integer a, b;
        boolean aux = false;
        while (!aux) {
            Codigo cod = this.cod.get(this.i);

            switch (cod.getCod()) {
                case "INPP":
                    this.s = -1;
                    break;
                case "AMEM":
                    this.s = this.s + Integer.parseInt(cod.getArg());
                    this.dados.add(0);
                    break;
                case "DMEM":
                    this.s = this.s - Integer.parseInt(cod.getArg());
                    break;
                case "PARA":
                    aux = true;
                    break;
                case "CRCT":
                    this.s++;
                    this.dados.add(this.s, Integer.valueOf(cod.getArg()));
                    break;
                case "CRVL":
                    this.s++;
                    this.dados.add(this.s, this.dados.get(Integer.valueOf(cod.getArg())));
                    break;
                case "ARMZ":
                    this.dados.set(Integer.valueOf(cod.getArg()), this.dados.get(this.s));
                    this.s--;
                    break;
                case "SOMA":
                    a = this.dados.get(this.s - 1);
                    b = this.dados.get(this.s);

                    this.dados.add(this.s - 1, a + b);
                    this.s--;
                    break;
                case "SUBT":
                    a = this.dados.get(this.s - 1);
                    b = this.dados.get(this.s);

                    this.dados.add(this.s - 1, a - b);
                    this.s--;
                    break;
                case "MULT":
                    a = this.dados.get(this.s - 1);
                    b = this.dados.get(this.s);

                    this.dados.add(this.s - 1, a * b);
                    this.s--;
                    break;
                case "DIVI":
                    a = this.dados.get(this.s - 1);
                    b = this.dados.get(this.s);
                    try {
                        this.dados.add(this.s - 1, a / b);
                    } catch (ArithmeticException e) {
                        this.addText("AVISO: Divisão por 0.");
                        this.dados.add(this.s - 1, 0);
                    }
                    this.s--;
                    break;
                case "MODI":
                    a = this.dados.get(this.s - 1);
                    b = this.dados.get(this.s);

                    this.dados.add(this.s - 1, a % b);
                    this.s--;
                    break;

                case "INVR":
                    b = this.dados.get(this.s);

                    this.dados.add(this.s - 1, -b);
                    this.s--;
                    break;

                case "CONJ":
                    a = this.dados.get(this.s - 1);
                    b = this.dados.get(this.s);
                    if (parseBoolean(a) && parseBoolean(b)) {
                        this.dados.add(this.s - 1, 1);
                    } else {
                        this.dados.add(this.s - 1, 0);
                    }
                    this.s--;
                    break;

                case "DISJ":
                    a = this.dados.get(this.s - 1);
                    b = this.dados.get(this.s);
                    if (parseBoolean(a) || parseBoolean(b)) {
                        this.dados.add(this.s - 1, 1);
                    } else {
                        this.dados.add(this.s - 1, 0);
                    }
                    this.s--;
                    break;

                case "NEGA":
                    this.dados.add(this.s, 1 - this.dados.get(this.s));
                    break;

                case "CMME":
                    a = this.dados.get(this.s - 1);
                    b = this.dados.get(this.s);
                    if (a < b) {
                        this.dados.add(this.s - 1, 1);
                    } else {
                        this.dados.add(this.s - 1, 0);
                    }
                    this.s--;
                    break;

                case "CMMA":
                    a = this.dados.get(this.s - 1);
                    b = this.dados.get(this.s);
                    if (a > b) {
                        this.dados.add(this.s - 1, 1);
                    } else {
                        this.dados.add(this.s - 1, 0);
                    }
                    this.s--;
                    break;

                case "CMIG":
                    a = this.dados.get(this.s - 1);
                    b = this.dados.get(this.s);
                    if (a == b) {
                        this.dados.add(this.s - 1, 1);
                    } else {
                        this.dados.add(this.s - 1, 0);
                    }
                    this.s--;
                    break;

                case "CMDG":
                    a = this.dados.get(this.s - 1);
                    b = this.dados.get(this.s);
                    if (a != b) {
                        this.dados.add(this.s - 1, 1);
                    } else {
                        this.dados.add(this.s - 1, 0);
                    }
                    this.s--;
                    break;

                case "CMAG":
                    a = this.dados.get(this.s - 1);
                    b = this.dados.get(this.s);
                    if (a >= b) {
                        this.dados.add(this.s - 1, 1);
                    } else {
                        this.dados.add(this.s - 1, 0);
                    }
                    this.s--;
                    break;

                case "CMEG":
                    a = this.dados.get(this.s - 1);
                    b = this.dados.get(this.s);
                    if (a >= b) {
                        this.dados.add(this.s - 1, 1);
                    } else {
                        this.dados.add(this.s - 1, 0);
                    }
                    this.s--;
                    break;

                case "DSVS":
                    this.i = getLabel(cod.getArg()) - 1;
                    break;

                case "DSVF":
                    b = this.dados.get(this.s);
                    if (parseBoolean(b)) {
                        this.i = getLabel(cod.getArg()) - 1;
                    }
                    break;

                case "NADA":
                    break;

                case "IMPR":
                    str = Integer.toString(this.dados.get(this.s));
                    this.addText("Impressão:");
                    this.addText(str);
                    this.s = this.s - 1;
                    break;

                case "LEIT":
                    this.s++;
                    str = JOptionPane.showInputDialog("Digite a entrada:");
                    if (str != null) {
                        switch (str) {
                            case "true":
                                str = "1";
                                break;
                            case "false":
                                str = "0";
                                break;
                        }
                    }
                    this.addText("Entrada do usuário:");
                    try {
                        this.dados.add(this.s, Integer.valueOf(str));
                        this.addText(str);
                    } catch (NumberFormatException | NullPointerException _np) {
                        this.dados.add(this.s, 0);
                        this.addText("Entrada não reconhecida: Valor alterado para 0");
                    }
                    break;

            }

            this.i++;
        }
    }

    public Integer getLabel(String arg) {
        for (int i = 0; i < this.cod.size(); i++) {
            if (this.cod.get(i).getLabel().equals(arg)) {
                return i;
            }
        }
        return null;
    }

    public Boolean parseBoolean(Integer a) {
        if (a == 1) {
            return true;
        }
        return false;
    }

    private void addText(String text) {
        String str = this.saida.getEntrada().getText();
        this.saida.getEntrada().setText(str + text + "\n");
    }

    @Override
    public void run() {
        this.interpreta();
    }
}
