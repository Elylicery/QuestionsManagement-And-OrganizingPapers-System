import javax.swing.*;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
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


public class QSelectionDialog extends JDialog {

    private JTextField text_QTno1,text_Qno1,text_question;
    private JTextField text_itemA,text_itemB,text_itemC,text_itemD;
    private JComboBox combo_diff,combo_point,combo_selection;
    Vector<String> obj_point;

    Object[] obj_selection = {"A", "B", "C", "D"};
    Object[] obj_diff = {"易(难度1)", "偏易(难度2)", "适中(难度3)", "偏难(难度4)", "难(难度5)"};

    private Dao con = new Dao();

    /**
     * Launch the application
     *
     * @param args
     */
    public static void main(String args[]) {
        try {
            QSelectionDialog dialog = new  QSelectionDialog("修改信息","科目1", "13");
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the dialog
     */
    public  QSelectionDialog(String title,String kemu,String Qno) {
        super();
        setModal(true);
        getContentPane().setLayout(new GridBagLayout());
        setTitle(title);
        setBounds(100, 100, 560, 300);
        setDefaultCloseOperation(HIDE_ON_CLOSE);//设置按"X"按钮时候的动作，一般都会把它设成退出程序//
        setResizable(true);
        setLayout(new FlowLayout());
        this.add(new JLabel("  题 号: "));
        text_QTno1 = new JTextField("1");
        text_QTno1.setEditable(false);
        this.add(text_QTno1);
        text_Qno1 = new JTextField(10);
        this.add(text_Qno1);
        this.add(new JLabel("　      难 度:"));
        combo_diff = new JComboBox(obj_diff);
        this.add(combo_diff);
        this.add(new JLabel("        知 识 点: "));
        obj_point = getPoint(kemu);
        combo_point = new JComboBox(obj_point);
        this.add(combo_point);
        this.add(new JLabel("   题 目: "));
        text_question = new JTextField(40);
        this.add(text_question);
        this.add(new JLabel("   A选项: "));
        text_itemA = new JTextField(40);
        this.add(text_itemA);
        this.add(new JLabel("   B选项: "));
        text_itemB = new JTextField(40);
        this.add(text_itemB);
        this.add(new JLabel("   C选项: "));
        text_itemC = new JTextField(40);
        this.add(text_itemC);
        this.add(new JLabel("   D选项: "));
        text_itemD = new JTextField(40);
        this.add(text_itemD);
        this.add(new JLabel("    答案:"));
        combo_selection = new JComboBox(obj_selection);
        this.add(combo_selection);


        final JButton submitButton = new JButton();
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (Qno.equals("")){
                    String point = combo_point.getSelectedItem().toString();
                    int diff = combo_diff.getSelectedIndex() + 1;
                    int k = combo_selection.getSelectedIndex();
                    String answer = obj_selection[k].toString();
                    try {
                        String sql = "SELECT FROM Selection where Qno="+text_QTno1.getText() + text_Qno1.getText()+";";
                        ResultSet rs = con.getRs(sql);
                        sql = "INSERT INTO Selection VALUES('" + text_QTno1.getText() + text_Qno1.getText() + "','" + text_question.getText() + "','";
                        if (text_itemA.getText().trim().length() == 0 || text_itemB.getText().trim().length() == 0 || text_itemC.getText().trim().length() == 0 || text_itemD.getText().trim().length() == 0 || text_question.getText().trim().length() == 0)
                            JOptionPane.showMessageDialog(null, "插入数据不完整！", "插入", JOptionPane.OK_OPTION);
                        else {
                            sql += text_itemA.getText() + "','" + text_itemB.getText() + "','" + text_itemC.getText() + "','" + text_itemD.getText() + "','";
                            sql += answer + "','" + point + "'," + diff + ",0,'" + kemu + "');";
                            System.out.println(sql);
                            con.dataUpdate(sql);
                            JOptionPane.showMessageDialog(null, "插入成功！", "插入", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "插入数据失败，请检查数据！", "插入", JOptionPane.OK_OPTION);
                    }
                }
                else if (Qno!=""){
                    //先删除
                    String sql1 = "delete from selection where Qno='"+Qno+"';";
                    try{
                        con.dataUpdate(sql1);
                    }catch (SQLException ex){
                        ex.printStackTrace();
                    }
                    //再重新添加新的内容
                    String point = combo_point.getSelectedItem().toString();
                    int diff = combo_diff.getSelectedIndex() + 1;
                    int k = combo_selection.getSelectedIndex();
                    String answer = obj_selection[k].toString();
                    try {
                        String sql = "INSERT INTO Selection VALUES('" + text_QTno1.getText() + text_Qno1.getText() + "','" + text_question.getText() + "','";
                        if (text_itemA.getText().trim().length() == 0 || text_itemB.getText().trim().length() == 0 || text_itemC.getText().trim().length() == 0 || text_itemD.getText().trim().length() == 0 || text_question.getText().trim().length() == 0)
                            JOptionPane.showMessageDialog(null, "插入数据不完整！", "插入", JOptionPane.OK_OPTION);
                        else {
                            sql += text_itemA.getText() + "','" + text_itemB.getText() + "','" + text_itemC.getText() + "','" + text_itemD.getText() + "','";
                            sql += answer + "','" + point + "'," + diff + ",0,'" + kemu + "');";
                            System.out.println(sql);
                            con.dataUpdate(sql);
                            JOptionPane.showMessageDialog(null, "修改成功！", "修改", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "修改数据失败，请检查数据！", "修改", JOptionPane.OK_OPTION);
                    }
                }
            }
        });
        submitButton.setText("确定");
        this.add(submitButton);

        final JButton exitButton = new JButton();
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        exitButton.setText("退出");
        this.add(exitButton);

        if (Qno!= "") {
            String sql = "select * from selection where Qno='" + Qno + "';";
            System.out.println(sql);
            ResultSet rs1 = con.getRs(sql);
            try {
                while (rs1.next()) {
                    String oldQno = rs1.getString("Qno");
                    text_Qno1.setText(oldQno.replaceFirst("1",""));
                    String oldqQues = rs1.getString("Question");
                    text_question.setText(oldqQues);
                    String olditemA = rs1.getString("ItemA");
                    text_itemA.setText(olditemA);
                    String olditemB = rs1.getString("ItemB");
                    text_itemB.setText(olditemB);
                    String olditemC = rs1.getString("ItemC");
                    text_itemC.setText(olditemC);
                    String olditemD = rs1.getString("ItemD");
                    text_itemD.setText(olditemC);
                    String oldAns = rs1.getString("Answer");
                    combo_selection.setSelectedItem(oldAns);
                    int oldDiff = rs1.getInt("Difficulty");
                    combo_diff.setSelectedItem(obj_diff[oldDiff-1]);
                    String oldPoint = rs1.getString("point");
                    combo_point.setSelectedItem(oldPoint);
                }
            } catch (SQLException ex) {
                Logger.getLogger(QSelectionDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }


    //传入科目，对应知识点
    public Vector<String> getPoint(String kemu) {
        Vector<String> obj_point = new Vector<String>();
        String point_temp;
        String sql = "select * from kemu_point where Kname='" + kemu + "';";
        ResultSet rs = con.getRs(sql);
        try {
            while (rs.next()) {
                String Point = rs.getString("Point");
                obj_point.add(Point);
            }
        } catch (SQLException ex) {
            Logger.getLogger(QSelectionDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
//        //测试用
//        System.out.println(kemu + "下的知识点有: ");
//        for (int i = 0; i < obj_point.size(); i++) {
//            System.out.println(obj_point.get(i) + ",");
//        }
        return obj_point;
    }

}

