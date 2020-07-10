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
import javax.swing.JTextField;


public class QJudgeDialog extends JDialog {

    JTextField text_QTno1, text_Qno1, text_question,
            text_item1;

    JComboBox combo_diff, combo_point,combo_judge;
    Vector<String> obj_point;

    Object[] obj_judge = {"是", "否"};
    Object[] obj_diff = {"易(难度1)", "偏易(难度2)", "适中(难度3)", "偏难(难度4)", "难(难度5)"};

    private Dao con = new Dao();

    /**
     * Launch the application
     *
     * @param args
     */
//    public static void main(String args[]) {
//        try {
//            QJudgeDialog dialog = new QJudgeDialog("修改信息","科目1", "30");
//            dialog.setVisible(true);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * Create the dialog
     */
    public  QJudgeDialog(String title,String kemu,String Qno) {
        super();
        setModal(true);
        getContentPane().setLayout(new GridBagLayout());
        setTitle(title);
        setBounds(100, 100, 510, 200);
        setDefaultCloseOperation(HIDE_ON_CLOSE);//设置按"X"按钮时候的动作，一般都会把它设成退出程序//
        setResizable(true);
        setLayout(new FlowLayout());

        this.add(new JLabel("　  题 号: "));
        text_QTno1 = new JTextField("3");
        text_QTno1.setEditable(false);
        this.add(text_QTno1);
        text_Qno1 = new JTextField(5);
        this.add(text_Qno1);
        this.add(new JLabel("　    难 度:"));
        combo_diff = new JComboBox(obj_diff);
        this.add(combo_diff);
        this.add(new JLabel("      知 识 点: "));
        obj_point = getPoint(kemu);
        combo_point = new JComboBox(obj_point);
        this.add(combo_point);
        this.add(new JLabel("题 目: "));
        text_question = new JTextField(40);
        this.add(text_question);
        this.add(new JLabel("  答 案: "));
        combo_judge = new JComboBox(obj_judge);
        this.add(combo_judge);


        final JButton submitButton = new JButton("确定");
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (Qno.equals("")){
                    String point = combo_point.getSelectedItem().toString();
                    int diff = combo_diff.getSelectedIndex() + 1;
                    String answer_tmp[] = {"是","否"};
                    String answer = answer_tmp[combo_judge.getSelectedIndex()];
                    String sql = "INSERT INTO Judge VALUES('" + text_QTno1.getText() + text_Qno1.getText() + "','" + text_question.getText() + "','";
                    try{
                        if (text_question.getText().trim().length() == 0) {
                            JOptionPane.showMessageDialog(null, "插入数据不完整！", "插入", JOptionPane.OK_OPTION);
                        } else {
                            sql += answer+ "','" + point + "'," + diff + ",0,'"+kemu+"');";
                            System.out.println(sql);
                            con.dataUpdate(sql);
                            text_question.setText("");
                            JOptionPane.showMessageDialog(null, "插入判断题成功！", "插入", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "插入判断题失败，请检查数据！", "插入", JOptionPane.OK_OPTION);
                    }
                }
                else if (Qno!=""){
                    //先删除
                    String sql1 = "delete from judge where Qno='"+Qno+"';";
                    try{
                        con.dataUpdate(sql1);
                    }catch (SQLException ex){
                        ex.printStackTrace();
                    }
                    //再重新添加新的内容
                    String point = combo_point.getSelectedItem().toString();
                    int diff = combo_diff.getSelectedIndex() + 1;
                    String answer_tmp[] = {"是","否"};
                    String answer = answer_tmp[combo_judge.getSelectedIndex()];
                    String sql = "INSERT INTO Judge VALUES('" + text_QTno1.getText() + text_Qno1.getText() + "','" + text_question.getText() + "','";
                    try{
                        if (text_question.getText().trim().length() == 0) {
                            JOptionPane.showMessageDialog(null, "插入数据不完整！", "插入", JOptionPane.OK_OPTION);
                        } else {
                            sql += answer+ "','" + point + "'," + diff + ",0,'"+kemu+"');";
                            System.out.println(sql);
                            con.dataUpdate(sql);
                            text_question.setText("");
                            JOptionPane.showMessageDialog(null, "插入成功！", "插入", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (Exception ex) {
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
            String sql = "select * from judge where Qno='" + Qno + "';";
            System.out.println(sql);
            ResultSet rs1 = con.getRs(sql);
            try {
                while (rs1.next()) {
                    String oldQno = rs1.getString("Qno");
                    text_Qno1.setText(oldQno.replaceFirst("3",""));
                    String oldqQues = rs1.getString("Question");
                    text_question.setText(oldqQues);
                    String oldAns = rs1.getString("Answer");
                    combo_judge.setSelectedItem(oldAns);
                    int oldDiff = rs1.getInt("Difficulty");
                    combo_diff.setSelectedItem(obj_diff[oldDiff-1]);
                    String oldPoint = rs1.getString("point");
                    combo_point.setSelectedItem(oldPoint);
                }
            } catch (SQLException ex) {
                Logger.getLogger(QJudgeDialog.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(QJudgeDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
//        //测试用
//        System.out.println(kemu + "下的知识点有: ");
//        for (int i = 0; i < obj_point.size(); i++) {
//            System.out.println(obj_point.get(i) + ",");
//        }
        return obj_point;
    }

}

