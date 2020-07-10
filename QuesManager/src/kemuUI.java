 /*
  * To change this license header, choose License Headers in Project Properties.
  * To change this template file, choose Tools | Templates
  * and open the template in the editor.
  */
 import java.awt.*;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.logging.Level;
 import java.util.logging.Logger;
 import javax.swing.DefaultListModel;
 import javax.swing.JOptionPane;
 import javax.swing.*;

 /**
  *
  * @author lenovo
  */
 public class kemuUI extends JFrame {

     Dao con = new Dao();

     /**
      * Creates new form DepartmentManager
      */
     public kemuUI() {
         jToolBar1 = new JToolBar();
         jButtonAdd = new JButton();
         jButtonModify = new JButton();
         jButtonDelete = new JButton();
         jButtonQuit = new JButton();
         jScrollPane1 = new JScrollPane();
         jList1 = new JList();
         filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(50, 0), new java.awt.Dimension(80, 0), new java.awt.Dimension(32767, 0));
         filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(50, 0), new java.awt.Dimension(80, 0), new java.awt.Dimension(32767, 0));
         filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 30), new java.awt.Dimension(0, 30), new java.awt.Dimension(32767, 30));

         setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
         setLocation(400,300);

         jToolBar1.setRollover(true);

         jButtonAdd.setText("添加");
         jButtonAdd.setFocusable(false);
         jButtonAdd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
         jButtonAdd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
         jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
             public void actionPerformed(java.awt.event.ActionEvent evt) {
                 jButtonAddActionPerformed(evt);
             }
         });
         jToolBar1.add(jButtonAdd);

         jButtonModify.setText("修改");
         jButtonModify.setFocusable(false);
         jButtonModify.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
         jButtonModify.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
         jButtonModify.addActionListener(new java.awt.event.ActionListener() {
             public void actionPerformed(java.awt.event.ActionEvent evt) {
                 jButtonModifyActionPerformed(evt);
             }
         });
         jToolBar1.add(jButtonModify);

         jButtonDelete.setText("删除");
         jButtonDelete.setFocusable(false);
         jButtonDelete.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
         jButtonDelete.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
         jButtonDelete.addActionListener(new java.awt.event.ActionListener(){
             public void actionPerformed(java.awt.event.ActionEvent evt){
                 jButtonDeleteActionPerformed(evt);
             }
         });
         jToolBar1.add(jButtonDelete);

         jButtonQuit.setText("退出");
         jButtonQuit.setFocusable(false);
         jButtonQuit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
         jButtonQuit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
         jButtonQuit.addActionListener(new java.awt.event.ActionListener() {
             public void actionPerformed(java.awt.event.ActionEvent evt) {
                 jButtonQuitActionPerformed(evt);
             }
         });
         jToolBar1.add(jButtonQuit);

         getContentPane().add(jToolBar1, java.awt.BorderLayout.PAGE_START);

         jList1.setModel(loadListItems());
         jScrollPane1.setViewportView(jList1);

         getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);
         getContentPane().add(filler1, java.awt.BorderLayout.LINE_START);
         getContentPane().add(filler2, java.awt.BorderLayout.LINE_END);
         getContentPane().add(filler3, java.awt.BorderLayout.PAGE_END);

         pack();
         //add my code
     }

     private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {
         System.out.println("添加科目！！");
         String sql;
         String kemu = JOptionPane.showInputDialog(rootPane, "输入要添加的科目名称");
         if (kemu!=null && !"".equals(kemu)){
             //添加科目
             sql = "insert into Kemu(Kname) values ('"+kemu+"');";
             try{
                 con.dataUpdate(sql);
             }catch (SQLException e){
                 JOptionPane.showMessageDialog(null,"添加科目失败","提示消息",JOptionPane.ERROR_MESSAGE);
             }
             ((DefaultListModel)jList1.getModel()).addElement(kemu);
             //为科目添加一个默认知识点
             sql = "insert into kemu_point(Kname,Point) values('"+kemu+"','默认知识点')";
             try{
                 con.dataUpdate(sql);
             }catch (SQLException e){
                 e.printStackTrace();
             }

         }else{
             JOptionPane.showMessageDialog(null,"请输入科目！","提示消息",JOptionPane.WARNING_MESSAGE);
         }
     }

     private void jButtonModifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonModifyActionPerformed
         System.out.println("修改执行");
         String sql,sql1;
         int selIdx = jList1.getSelectedIndex();
         String selItem = (String)jList1.getSelectedValue();
         String selModi = JOptionPane.showInputDialog(rootPane, "请修改下列专业名称。", selItem);
         if(selModi!=null && !selModi.equals("") && !selModi.equals(selItem)) {
             selModi.trim();
             sql = "update kemu set Kname ='"+selModi+"' where Kname='"+selItem+"';";
             sql1="update kemu_point set Kname ='"+selModi+"' where Kname='"+selItem+"';";
             try{
                 con.dataUpdate(sql);
                 con.dataUpdate(sql1);
                 ((DefaultListModel)jList1.getModel()).set(selIdx,selModi);
             }catch (SQLException e){
                 e.printStackTrace();
                 JOptionPane.showMessageDialog(null,"无法修改科目名称","提示消息",JOptionPane.ERROR_MESSAGE);
             }
         }
     }

     private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt){
         String sql;
         String sql2;
         int seltIdx = jList1.getSelectedIndex();
         String selItem = (String)jList1.getSelectedValue();
         int r=JOptionPane.showConfirmDialog(this,"是否删除该科目","删除",JOptionPane.OK_CANCEL_OPTION);
         if (r==JOptionPane.OK_OPTION)
         {
             try {
                 sql = "DELETE FROM kemu WHERE Kname='" + selItem + "';";
                 System.out.println(sql);
                 sql2 = "DELETE FROM kemu_point WHERE Kname='"+selItem+"';";
                 con.dataUpdate(sql);
                 con.dataUpdate(sql2);
                 ((DefaultListModel)jList1.getModel()).remove(seltIdx);
                 JOptionPane.showMessageDialog(this, "已成功删除该科目！", "删除", JOptionPane.INFORMATION_MESSAGE);
             }
             catch(Exception ex)
             {
                 JOptionPane.showMessageDialog(this,"未能成功删除该科目！","错误",JOptionPane.OK_OPTION);
             }
         }
     }

     private void jButtonQuitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonQuitActionPerformed
         this.dispose();
     }//GEN-LAST:event_jButtonQuitActionPerformed

     DefaultListModel loadListItems() {
         String sql = "select * from Kemu;";
         ResultSet rs= con.getRs(sql);
         DefaultListModel listModel = new DefaultListModel();
         listModel.clear();
         try {
             while(rs.next()) {
                 listModel.addElement(rs.getString("Kname"));
            }
         } catch (SQLException ex) {
             Logger.getLogger(kemuUI.class.getName()).log(Level.SEVERE, null, ex);
         }
         return listModel ;
     }

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
             java.util.logging.Logger.getLogger(kemuUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
         } catch (InstantiationException ex) {
             java.util.logging.Logger.getLogger(kemuUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
         } catch (IllegalAccessException ex) {
             java.util.logging.Logger.getLogger(kemuUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
         } catch (javax.swing.UnsupportedLookAndFeelException ex) {
             java.util.logging.Logger.getLogger(kemuUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
         }
         //</editor-fold>

         /* Create and display the form */
         java.awt.EventQueue.invokeLater(new Runnable() {
             public void run() {
                 new kemuUI().setVisible(true);
             }
         });
     }

     // Variables declaration - do not modify//GEN-BEGIN:variables
     //private java.util.List<book.stdscore.data.Department> departmentList;
     //private javax.persistence.Query departmentQuery;
     //private javax.persistence.EntityManager entityManager;
     private javax.swing.Box.Filler filler1;
     private javax.swing.Box.Filler filler2;
     private javax.swing.Box.Filler filler3;
     private javax.swing.JButton jButtonAdd;
     private javax.swing.JButton jButtonDelete;
     private javax.swing.JButton jButtonModify;
     private javax.swing.JButton jButtonQuit;
     private javax.swing.JList jList1;
     private javax.swing.JScrollPane jScrollPane1;
     private javax.swing.JToolBar jToolBar1;
     // End of variables declaration//GEN-END:variables
 }
