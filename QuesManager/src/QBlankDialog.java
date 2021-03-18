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


public class QBlankDialog extends JDialog {

    JTextField text_QTno1, text_Qno1, text_question, text_itemB, text_itemC, text_itemD,
            text_item1, text_item2, text_item3, text_item4, text_item5, text_item6, text_item7, text_item8;

    JComboBox combo_kemu, combo_Qtype1, combo_diff, combo_point;
    Vector<String> obj_point;

    Object[] obj_diff = {"易(难度1)", "偏易(难度2)", "适中(难度3)", "偏难(难度4)", "难(难度5)"};

    private Dao con = new Dao();

    /**
     * Launch the application
     *
     * @param args
     */
    public static void main(String args[]) {
        try {
            QBlankDialog dialog = new QBlankDialog("修改信息","科目1", "21");
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the dialog
     */
    public  QBlankDialog(String title,String kemu,String Qno) {
        super();
        setModal(true);
        getContentPane().setLayout(new GridBagLayout());
        setTitle(title);
        setBounds(100, 100, 550, 500);
        setDefaultCloseOperation(HIDE_ON_CLOSE);//设置按"X"按钮时候的动作，一般都会把它设成退出程序//
        setResizable(true);
        setLayout(new FlowLayout());
        this.add(new JLabel("　  题 号: "));
        text_QTno1 = new JTextField("2");
        text_QTno1.setEditable(false);
        this.add(text_QTno1);
        text_Qno1 = new JTextField(10);
        this.add(text_Qno1);
        this.add(new JLabel("　  难 度:"));
        combo_diff = new JComboBox(obj_diff);
        this.add(combo_diff);
        this.add(new JLabel("   知识点: "));
        obj_point = getPoint(kemu);
        combo_point = new JComboBox(obj_point);
        this.add(combo_point);
        this.add(new JLabel("题 目: "));
        text_question = new JTextField(40);
        this.add(text_question);
        this.add(new JLabel("答案1: "));
        text_item1 = new JTextField(40);
        this.add(text_item1);
        this.add(new JLabel("答案2: "));
        text_item2 = new JTextField(40);
        this.add(text_item2);
        this.add(new JLabel("答案3: "));
        text_item3 = new JTextField(40);
        this.add(text_item3);
        this.add(new JLabel("答案4: "));
        text_item4 = new JTextField(40);
        this.add(text_item4);
        this.add(new JLabel("答案5: "));
        text_item5 = new JTextField(40);
        this.add(text_item5);
        this.add(new JLabel("答案6: "));
        text_item6 = new JTextField(40);
        this.add(text_item6);
        this.add(new JLabel("答案7: "));
        text_item7 = new JTextField(40);
        this.add(text_item7);
        this.add(new JLabel("答案8: "));
        text_item8 = new JTextField(40);
        this.add(text_item8);
        this.add(new JLabel("[ 提示 ] : 填空题的填空个数范围为1-8。其中答案1必填，其余可为空                                     "));

        final JButton submitButton = new JButton("确定");
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (Qno.equals("")){
                    String sql;
                    String point = combo_point.getSelectedItem().toString();
                    int diff = combo_diff.getSelectedIndex() + 1;
                    try{
                        sql = "INSERT INTO Blank VALUES('" + text_QTno1.getText() + text_Qno1.getText() + "','" + text_question.getText() + "','";
                        if (text_item1.getText().trim().length() == 0 || text_question.getText().trim().length() == 0)
                            JOptionPane.showMessageDialog(null, "插入数据不完整！", "插入", JOptionPane.OK_OPTION);
                        else {
                            sql += text_item1.getText() + "','" + text_item2.getText() + "','" + text_item3.getText() + "','" + text_item4.getText() + "','" + text_item5.getText() + "','" + text_item6.getText() + "','" + text_item7.getText() + "','" + text_item8.getText() + "','";
                            sql += point+ "'," + diff + ",0,'"+kemu+"');";
                            System.out.println(sql);
                            con.dataUpdate(sql);
                            text_question.setText("");
                            text_item1.setText("");
                            text_item2.setText("");
                            text_item3.setText("");
                            text_item4.setText("");
                            text_item5.setText("");
                            text_item6.setText("");
                            text_item7.setText("");
                            text_item8.setText("");
                            JOptionPane.showMessageDialog(null, "插入填空题成功！", "插入", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }catch (Exception ex){
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "插入数据失败，请检查数据！", "插入", JOptionPane.OK_OPTION);
                    }
                }
                else if (Qno!=""){
                    //先删除
                    String sql1 = "delete from blank where Qno='"+Qno+"';";
                    try{
                        con.dataUpdate(sql1);
                    }catch (SQLException ex){
                        ex.printStackTrace();
                    }
                    //再重新添加新的内容
                    String sql;
                    String point = combo_point.getSelectedItem().toString();
                    int diff = combo_diff.getSelectedIndex() + 1;
                    try{
                        sql = "INSERT INTO Blank VALUES('" + text_QTno1.getText() + text_Qno1.getText() + "','" + text_question.getText() + "','";
                        if (text_item1.getText().trim().length() == 0 || text_question.getText().trim().length() == 0)
                            JOptionPane.showMessageDialog(null, "插入数据不完整！", "插入", JOptionPane.OK_OPTION);
                        else {
                            sql += text_item1.getText() + "','" + text_item2.getText() + "','" + text_item3.getText() + "','" + text_item4.getText() + "','" + text_item5.getText() + "','" + text_item6.getText() + "','" + text_item7.getText() + "','" + text_item8.getText() + "','";
                            sql += point+ "'," + diff + ",0,'"+kemu+"');";
                            System.out.println(sql);
                            con.dataUpdate(sql);
                            text_question.setText("");
                            text_item1.setText("");
                            text_item2.setText("");
                            text_item3.setText("");
                            text_item4.setText("");
                            text_item5.setText("");
                            text_item6.setText("");
                            text_item7.setText("");
                            text_item8.setText("");
                            JOptionPane.showMessageDialog(null, "插入填空题成功！", "插入", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }catch (Exception ex){
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "插入数据失败，请检查数据！", "插入", JOptionPane.OK_OPTION);
                    }
                }
            }
        });
        this.add(submitButton);

        final JButton exitButton = new JButton("退出");
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        this.add(exitButton);

        if (Qno!= "") {
            String sql = "select * from blank where Qno='" + Qno + "';";
            System.out.println(sql);
            ResultSet rs1 = con.getRs(sql);
            try {
                while (rs1.next()) {
                    String oldQno = rs1.getString("Qno");
                    text_Qno1.setText(oldQno.replaceFirst("2",""));
                    String oldqQues = rs1.getString("Question");
                    text_question.setText(oldqQues);
                    String olditem1 = rs1.getString("Answer1");
                    text_item1.setText(olditem1);
                    String olditem2 = rs1.getString("Answer2");
                    text_item1.setText(olditem2);
                    String olditem3 = rs1.getString("Answer3");
                    text_item1.setText(olditem3);
                    String olditem4 = rs1.getString("Answer4");
                    text_item1.setText(olditem4);
                    String olditem5 = rs1.getString("Answer5");
                    text_item1.setText(olditem5);
                    String olditem6 = rs1.getString("Answer6");
                    text_item1.setText(olditem6);
                    String olditem7 = rs1.getString("Answer7");
                    text_item1.setText(olditem7);
                    String olditem8 = rs1.getString("Answer8");
                    text_item1.setText(olditem8);
                    int oldDiff = rs1.getInt("Difficulty");
                    combo_diff.setSelectedItem(obj_diff[oldDiff-1]);
                    String oldPoint = rs1.getString("point");
//                    System.out.println("old pooint :"+oldPoint);
                    combo_point.setSelectedItem(oldPoint);
                }
            } catch (SQLException ex) {
                Logger.getLogger(zhishidianUI.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(QBlankDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
//        //测试用
//        System.out.println(kemu + "下的知识点有: ");
//        for (int i = 0; i < obj_point.size(); i++) {
//            System.out.println(obj_point.get(i) + ",");
//        }
        return obj_point;
    }

}

