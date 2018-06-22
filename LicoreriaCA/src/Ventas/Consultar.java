/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ventas;

import java.sql.*;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Rodriguez
 */
public class Consultar extends javax.swing.JPanel {

    /**
     * Creates new form Consultar
     */
    public Consultar() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        consulta = new javax.swing.JTextField();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(436, 434));

        jLabel1.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 51, 102));
        jLabel1.setText("Consultar Venta");

        jTable1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jTable1.setForeground(new java.awt.Color(0, 0, 51));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jButton1.setFont(new java.awt.Font("Arial", 3, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(0, 51, 102));
        jButton1.setText("Consultar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Arial", 3, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 51));
        jLabel2.setText("Cod Venta:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(122, 122, 122)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(111, 111, 111)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(consulta, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(156, 156, 156)
                        .addComponent(jButton1)))
                .addContainerGap(139, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 32, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 374, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(56, 56, 56)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(consulta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(35, 35, 35)
                .addComponent(jButton1)
                .addGap(47, 47, 47)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private Connection conn = null;

    public void realizarConexion() {
        String urlDatabase = "jdbc:postgresql://localhost:5432/LicoreraDB";
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(urlDatabase, "postgres", "1234");
        } catch (Exception e) {
            System.out.println("Ocurrio un error : " + e.getMessage());
        }
        if (conn != null) {
            System.out.println("La conexión se realizo sin problemas! ");
        }
    }
    public void desconectar() {
        try {
            conn.close();
            conn = null;
        } catch (SQLException ex) {
            System.out.println("problema al desconectar la BD");
        }
        System.out.println("conn cerrada");
    }
    
    public void mostrarDatos() {

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("id");
        model.addColumn("CodProd");
        model.addColumn("Nombre");
        model.addColumn("CodVenta");
        model.addColumn("Total");
        model.addColumn("Fecha");
        model.addColumn("Cantidad");
        model.addColumn("Descto");
        jTable1.setModel(model);

        String tabla[] = new String[8];

        if (conn != null) {
            try {

                ResultSet n = state();
                while (n.next()) {
                    tabla[0] = n.getString(1);
                    tabla[1] = n.getString(2);
                    tabla[2] = n.getString(3);
                    tabla[3] = n.getString(4);
                    tabla[4] = n.getString(5);
                    tabla[5] = n.getString(6);
                    tabla[6] = n.getString(7);
                    tabla[7] = n.getString(8);
                    model.addRow(tabla);
                }

                jTable1.setModel(model);
            } catch (SQLException ex) {
                ex.getMessage();
            }
        }
    }

    public ResultSet state() {
        ResultSet n = null;
        try {
            Statement st = conn.createStatement();
            if (consulta.getText().isEmpty()) {
                n = st.executeQuery("SELECT idventa,codprod,nombreprod,codventa,"
                        + "totalventa,fechaventa,descuentos,cantidad FROM venta");
            } else {
                n = st.executeQuery("SELECT idventa,codprod,nombreprod,codventa,"
                        + "totalventa,fechaventa,descuentos,cantidad FROM venta "
                        + "WHERE codventa=" + consulta.getText());
            }
        } catch (SQLException ex) {
            ex.getMessage();
        }
        return n;
    }
    
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        realizarConexion();
        mostrarDatos();
        desconectar();
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField consulta;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
