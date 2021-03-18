import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class zhishidianUI extends JFrame {

    private Object model;
    private Dao con = new Dao();

    /**
     * Creates new form GradeClassManager
     */
    public zhishidianUI() {
        initComponents();
    }

    DefaultMutableTreeNode createTreeNodes() {

        String root = "root";
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(root);
        DefaultMutableTreeNode kemuNode;
        DefaultMutableTreeNode pointNode;
        String sql = "select * from Kemu order by Kno";
        ResultSet rs1= con.getRs(sql);
        try {
            while (rs1.next()) {
                String Kname = rs1.getString("Kname");
                kemuNode = new DefaultMutableTreeNode("科目:"+Kname);
                rootNode.add(kemuNode);
                sql = "select Point from kemu_point where Kname='" + Kname+"';";
                ResultSet rs2 = con.getRs(sql);
                while (rs2.next()) {
                    pointNode = new DefaultMutableTreeNode(rs2.getString("Point"));
                    kemuNode.add(pointNode);
                    }
            }
        } catch (SQLException ex) {
            Logger.getLogger(zhishidianUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rootNode;
    }

    //定制页面内容
    private void initComponents() {

        jPopupMenu1 = new JPopupMenu();
        jMenuItemAdd = new JMenuItem();
        jScrollPane2 = new JScrollPane();
        //定制树的初始化代码，创建树的语句改为定制创建
        jTree1 = new JTree(createTreeNodes());
        jScrollPane1 = new JScrollPane();
        jTextArea1 = new JTextArea();
        jButtonQuit = new JButton();

        jMenuItemAdd.setText("请单击 科目 或 知识点 节点");
        jMenuItemAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAddActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItemAdd);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLocation(400,300);
        setTitle("设置科目下知识点");

        jTree1.setToolTipText("右键单击弹出操作菜单");
        jTree1.setComponentPopupMenu(jPopupMenu1);
        jTree1.setRootVisible(false);
        jTree1.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTree1ValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(jTree1);

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(16);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("[添加知识点]\n"+"在添加知识的科目项单击右键，会弹出菜单，可添加该科目下的知识点");
        jTextArea1.setToolTipText("");
        jScrollPane1.setViewportView(jTextArea1);

        jButtonQuit.setText("退出");
        jButtonQuit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonQuitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addContainerGap())
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(jButtonQuit)
                                                .addGap(33, 33, 33))))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addComponent(jButtonQuit)
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap(40, Short.MAX_VALUE)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(23, 23, 23))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItemAddActionPerformed(java.awt.event.ActionEvent evt) {
        TreePath selPath = jTree1.getSelectionPath();
       Object[] nodes = selPath.getPath();
        if("添加知识点".equals(jMenuItemAdd.getText())) {
            System.out.println("添加知识点啦！！");
            String newPoint = JOptionPane.showInputDialog("输入知识点名称：");
            if (newPoint.equals("")){
                JOptionPane.showMessageDialog(null,"知识点名称不能为空","提示",JOptionPane.WARNING_MESSAGE);
            }else {
                addChildMyNode(newPoint);
                String Knametmp = ((DefaultMutableTreeNode) nodes[1]).getUserObject().toString();
                String Kname = Knametmp.replace("科目:", "");
                String sql = "INSERT INTO kemu_point values ('" + Kname + "'," + "'" + newPoint + "');";
                try {
                    con.dataUpdate(sql);
                    JOptionPane.showMessageDialog(rootPane, "添加成功并保存");
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "添加知识点失败", "提示", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_jMenuItemAddActionPerformed

    private void jButtonQuitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonQuitActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jButtonQuitActionPerformed

    private void jTree1ValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTree1ValueChanged
        // TODO add your handling code here:
        showMenu();
    }//GEN-LAST:event_jTree1ValueChanged

    //根据用户选择的不同结点修改弹出式菜单项的文字
    void showMenu() {
        DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) jTree1.getSelectionPath().getLastPathComponent();
        Object userObj = null;
        if (selNode != null) {
            userObj = selNode.getUserObject();
        }
        if (((String)userObj).startsWith("科目")){
            jMenuItemAdd.setText("添加知识点");
        } else {
            jMenuItemAdd.setText("请单击科目添加知识点");
        }
    }

    void addChildMyNode(String nodeText) {
        DefaultTreeModel modelThis = (DefaultTreeModel) jTree1.getModel();
        DefaultMutableTreeNode selectedNode;
        selectedNode = (DefaultMutableTreeNode) jTree1.getLastSelectedPathComponent();
//如果节点为空，直接返回
        if (selectedNode == null) {
            return;
        }
//创建一个新节点
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(nodeText);
//直接通过model来添加新节点，则无需通过调用JTree的updateUI方法
        modelThis.insertNodeInto(newNode, selectedNode, selectedNode.getChildCount());
//--------下面代码实现显示新节点（自动展开父节点）-------
        TreeNode[] nodes = modelThis.getPathToRoot(newNode);
        TreePath path = new TreePath(nodes);
        jTree1.scrollPathToVisible(path);
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
            java.util.logging.Logger.getLogger(zhishidianUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(zhishidianUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(zhishidianUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(zhishidianUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new zhishidianUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonQuit;
    private javax.swing.JMenuItem jMenuItemAdd;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTree jTree1;
    // End of variables declaration//GEN-END:variables
}
