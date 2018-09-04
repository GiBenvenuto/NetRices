/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import al_calculadora.AnalisadorLexico;
import java.awt.Color;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.EditorKit;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 *
 * @author Gi
 */
public class UIPrincipal extends javax.swing.JFrame {

    StyleContext sc;
    Style defaultStyle;
    DefaultStyledDocument doc;

    /**
     * Creates new form UIPrincipal
     */
    public UIPrincipal() {
        initComponents();
        TextLineNumber tln = new TextLineNumber(this.entradaText);
        scrollEntrada.setRowHeaderView(tln);
//        sc = new StyleContext();
//        defaultStyle = sc.getStyle(StyleContext.DEFAULT_STYLE);
//        doc = new DefaultStyledDocument(sc);
//        this.entradaText = new JTextPane(doc);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        scrollEntrada = new javax.swing.JScrollPane();
        entradaText = new javax.swing.JTextPane();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        alTable = new javax.swing.JTable();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        analisarBtn = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        menu_abrir = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(jTable1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        entradaText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                entradaTextKeyTyped(evt);
            }
        });
        scrollEntrada.setViewportView(entradaText);

        jTabbedPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        alTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Lexema", "Token", "Linha", "Coluna Inicio", "Coluna Fim"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        alTable.setSelectionForeground(new java.awt.Color(0, 0, 0));
        jScrollPane1.setViewportView(alTable);

        jTabbedPane1.addTab("Analisador Léxico", jScrollPane1);
        jTabbedPane1.addTab("Analisador Sintático", jTabbedPane2);

        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, null, null, new java.awt.Color(102, 255, 102)));

        analisarBtn.setText("Analisar");
        analisarBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                analisarBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(analisarBtn)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(analisarBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jMenu1.setText("Arquivos");

        menu_abrir.setText("Abrir");
        menu_abrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_abrirActionPerformed(evt);
            }
        });
        jMenu1.add(menu_abrir);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Compilar");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 802, Short.MAX_VALUE)
                    .addComponent(scrollEntrada)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName("aLex");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public String removeHTML(String entrada) {
        entrada = entrada.replaceAll("<html>", "");
        entrada = entrada.replaceAll("</html>", "");
        entrada = entrada.replaceAll("<body>", "");
        entrada = entrada.replaceAll("</body>", "");
        entrada = entrada.replaceAll("<head>", "");
        entrada = entrada.replaceAll("</head>", "");
        entrada = entrada.replaceAll("<b>", "");
        entrada = entrada.replaceAll("</b>", "");

        return entrada;
    }

    private void analisarBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_analisarBtnActionPerformed
        DefaultTableModel model = (DefaultTableModel) this.alTable.getModel();
        String entrada = this.entradaText.getText();
        entrada = removeHTML(entrada);
        AnalisadorLexico al = new AnalisadorLexico();
        al.lex(entrada);
        String[][] saida = al.toInterface(entrada);
        model.setRowCount(saida.length);
        boolean erros[] = new boolean[saida.length];

        for (int i = 0; i < saida.length; i++) {

            model.setValueAt(saida[i][0], i, 0);
            model.setValueAt(saida[i][1], i, 1);
            model.setValueAt(saida[i][2], i, 2);
            model.setValueAt(saida[i][3], i, 3);
            model.setValueAt(saida[i][4], i, 4);

            if (saida[i][1].contains("ERRO")) {
                erros[i] = true;
            } else {
                erros[i] = false;
            }

        }

        TableCellRenderer tcr = new tableRender(erros);
        TableColumn column;
        for (int i = 0; i < 5; i++) {
            column = this.alTable.getColumnModel().getColumn(i);
            column.setCellRenderer(tcr);
        }

    }//GEN-LAST:event_analisarBtnActionPerformed

    private void menu_abrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu_abrirActionPerformed
        JFileChooser jc = new JFileChooser("D:\\Users\\Gi\\Desktop\\Desktop\\BCC\\8SEMESTRE");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("txt", "txt");
        jc.setFileFilter(filter);
        int result;
        result = jc.showOpenDialog(null);

        if (result == javax.swing.JFileChooser.APPROVE_OPTION) {
            String filename = jc.getSelectedFile().getAbsolutePath();
            try {
                BufferedReader in = new BufferedReader(new FileReader(filename));
                String entrada, line;
                entrada = "";
                line = in.readLine();
                while (line != null) {
                    entrada += line + "\r\n";
                    line = in.readLine();
                }

                this.entradaText.setText(entrada);

            } catch (FileNotFoundException ex) {
                Logger.getLogger(UIPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(UIPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }//GEN-LAST:event_menu_abrirActionPerformed

    private void entradaTextKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_entradaTextKeyTyped
//        try {
//            Style mainStyle = sc.addStyle("MainStyle", defaultStyle);
//            StyleConstants.setBold(mainStyle, true);
//            Character c = evt.getKeyChar();
//            
//            String text = this.entradaText.getText();
//            
//            doc.setLogicalStyle(0, mainStyle);
//            doc.insertString(0, text, null);
//            doc.setCharacterAttributes(0, text.length(), mainStyle, false);
//            
//        } catch (BadLocationException ex) {
//            Logger.getLogger(UIPrincipal.class.getName()).log(Level.SEVERE, null, ex);
//        }


    }//GEN-LAST:event_entradaTextKeyTyped

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(UIPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UIPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UIPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UIPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new UIPrincipal().setVisible(true);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable alTable;
    private javax.swing.JButton analisarBtn;
    private javax.swing.JTextPane entradaText;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JMenuItem menu_abrir;
    private javax.swing.JScrollPane scrollEntrada;
    // End of variables declaration//GEN-END:variables
}

class tableRender extends DefaultTableCellRenderer {

    private boolean erros[];

    public tableRender(boolean[] erros) {
        this.erros = erros;
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table,
            Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        String token = (String) value;
        if (this.erros[row]) {
            if (row % 2 == 0) {
                setBackground(Color.RED);
            } //setText(token);
            else {
                setBackground(new Color(220, 0, 0));
            }
        } else {
            setBackground(table.getBackground());
        }
        return this;
    }

    @Override
    public void validate() {
    }

    @Override
    public void revalidate() {
    }

    @Override
    protected void firePropertyChange(String propertyName,
            Object oldValue, Object newValue) {
    }

    @Override
    public void firePropertyChange(String propertyName,
            boolean oldValue, boolean newValue) {
    }
}
