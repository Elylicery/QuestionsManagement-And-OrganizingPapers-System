import javax.swing.*;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


public class UserNewDialog extends JDialog {

    private JTextField nameTextField;
    private JTextField pwdTextField;
    private JComboBox typeComboBox;

    private JTextField numTextField;

    private Dao con = new Dao();

    /**
     * Launch the application
     *
     * @param args
     */
    public static void main(String args[]) {
        try {
            UserNewDialog dialog = new UserNewDialog("新建",  "");
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the dialog
     */
    public UserNewDialog(String title,String userName) {
        super();
        setModal(true);
        getContentPane().setLayout(new GridBagLayout());
        setTitle(title);
        setBounds(100, 100, 500, 375);

        final JLabel nameLabel = new JLabel();
        nameLabel.setText(" 用 户 名 ：");
        getContentPane().add(nameLabel);

        nameTextField = new JTextField();
        nameTextField.setName("姓名");
        nameTextField.setColumns(13);
        getContentPane().add(nameTextField);

        final JLabel pwdLabel = new JLabel();
        pwdLabel.setText(" 密 码 ：");
        getContentPane().add(pwdLabel);

        pwdTextField = new JTextField();
        pwdTextField.setName("密码");
        pwdTextField.setColumns(13);
        getContentPane().add(pwdTextField);

        final JLabel typeLabel = new JLabel();
        typeLabel.setText("类    别：");
        final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
        gridBagConstraints_5.insets = new Insets(5, 0, 0, 0);
        gridBagConstraints_5.gridy = 1;
        gridBagConstraints_5.gridx = 0;
        getContentPane().add(typeLabel, gridBagConstraints_5);

        typeComboBox = new JComboBox();
        typeComboBox.addItem("超级管理员");
        typeComboBox.addItem("题库管理员");
        typeComboBox.addItem("组卷管理员员");
        final GridBagConstraints gridBagConstraints_17 = new GridBagConstraints();
        gridBagConstraints_17.anchor = GridBagConstraints.WEST;
        gridBagConstraints_17.insets = new Insets(5, 0, 0, 0);
        gridBagConstraints_17.gridy = 1;
        gridBagConstraints_17.gridx = 1;
        getContentPane().add(typeComboBox, gridBagConstraints_17);


        final JPanel panel_2 = new JPanel();
        final FlowLayout flowLayout_2 = new FlowLayout();
        flowLayout_2.setAlignment(FlowLayout.RIGHT);
        flowLayout_2.setVgap(0);
        panel_2.setLayout(flowLayout_2);
        final GridBagConstraints gridBagConstraints_20 = new GridBagConstraints();
        gridBagConstraints_20.anchor = GridBagConstraints.EAST;
        gridBagConstraints_20.insets = new Insets(10, 0, 0, 0);
        gridBagConstraints_20.gridy = 10;
        gridBagConstraints_20.gridx = 1;
        getContentPane().add(panel_2, gridBagConstraints_20);

        final JButton submitButton = new JButton();
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (userName.equals("")){
                    int i = typeComboBox.getSelectedIndex();
                    String sql = "insert into user values('" + nameTextField.getText() + "','" + pwdTextField.getText() + "',";
                    if (i == 0) {
                        sql += "'guanliyuan');";
                    } else if (i == 1) {
                        sql += "'tikuyuan');";
                    } else if (i == 2) {
                        sql += "'zujuanyuan');";
                    }
                    //System.out.println(sql);
                    try {
                        con.dataUpdate(sql);
                        JOptionPane.showMessageDialog(null, "添加新用户成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "添加新用户失败!", "失败！", JOptionPane.OK_OPTION);
                        ex.printStackTrace();
                    }
                }
                else if (userName!=""){
                    int i = typeComboBox.getSelectedIndex();
                    //先删除
                    String sql1 = "delete from user where userName='"+userName+"';";
                    try{
                        con.dataUpdate(sql1);
                    }catch (SQLException ex){
                        ex.printStackTrace();
                    }
                    //再重新添加新的内容
                    String sql = "insert into user values('" + nameTextField.getText() + "','" + pwdTextField.getText() + "',";
                    if (i == 0) {
                        sql += "'guanliyuan');";
                    } else if (i == 1) {
                        sql += "'tikuyuan');";
                    } else if (i == 2) {
                        sql += "'zujuanyuan');";
                    }
                    //System.out.println(sql);
                    try {
                        con.dataUpdate(sql);
                        JOptionPane.showMessageDialog(null, "修改用户信息成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "修改用户信息失败!", "失败！", JOptionPane.OK_OPTION);
                        ex.printStackTrace();
                    }
                }
            }
        });
        submitButton.setText("确定");
        panel_2.add(submitButton);

        final JButton exitButton = new JButton();
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        exitButton.setText("退出");
        panel_2.add(exitButton);

        if (userName != "") {
            String sql = "select * from User where userName='" + userName + "';";
            System.out.println(sql);
            ResultSet rs1 = con.getRs(sql);
            try {
                while (rs1.next()) {
                    String pwd = rs1.getString("userPwd");
                    String role = rs1.getString("role");
                    nameTextField.setText(userName);
                    pwdTextField.setText(pwd);
                    if (role.equals("guanliyuan")){
                        typeComboBox.setSelectedIndex(0);
                    }else if (role.equals("tikuyuan")){
                        typeComboBox.setSelectedIndex(1);
                    }else if (role.equals("zujuanyuan")){
                        typeComboBox.setSelectedIndex(2);
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(zhishidianUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}

