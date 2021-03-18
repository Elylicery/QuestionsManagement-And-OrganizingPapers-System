import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
public class insertQuesUI extends JFrame implements ActionListener {

    private Dao con = new Dao();

    //多选菜单里的项目
    Object[] obj = {"选择题", "填空题", "判断题", "名词解释", "综合题", "论述题"};
    Vector<String> obj_kemu;

    //定义组件
    JButton button_insert;

    JComboBox combo_kemu, combo_Qtype1, combo_diff, combo_point;

    public insertQuesUI() {
        //主窗体
        super("选择题目类型");
        this.getContentPane().setLayout(new BorderLayout());//设置布局管理器
        this.setBounds(400, 300, 300, 150);
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        this.setLayout(new GridLayout(3, 2));

        //找出所有科目
        obj_kemu = new Vector<String>();
        String kemu_temp;
        String sql = "select * from Kemu order by Kno";
        ResultSet rs1 = con.getRs(sql);
        try {
            while (rs1.next()) {
                String Kname = rs1.getString("Kname");
                obj_kemu.add(Kname);
            }
        } catch (SQLException ex) {
            Logger.getLogger(zhishidianUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        //跳转至新页面
        //1.选择科目
        this.add(new JLabel("       选择科目:         "));
        combo_kemu = new JComboBox(obj_kemu);
        this.add(combo_kemu);
        //2.选择题型
        this.add(new JLabel("       选择题型:         "));
        combo_Qtype1 = new JComboBox(obj);
        this.add(combo_Qtype1);
        button_insert = new JButton("录入试题");
        button_insert.addActionListener(this);
        this.add(button_insert);
        this.setVisible(true);
    }

    //主窗体的main（）入口
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new insertQuesUI();
            }
        });
    }

    /************************辅助函数*********************/

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
            Logger.getLogger(zhishidianUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        //测试用
        System.out.println(kemu + "下的知识点有: ");
        for (int i = 0; i < obj_point.size(); i++) {
            System.out.println(obj_point.get(i) + ",");
        }
        return obj_point;
    }

    public void actionPerformed(ActionEvent e) {
        String sql;
        //获得  i 题型 j 科目
        int i = combo_Qtype1.getSelectedIndex();
        int j = combo_kemu.getSelectedIndex();
        String kemu = obj_kemu.get(j);
        if (e.getSource() == button_insert) {
            if (i == 0) {
                QSelectionDialog dialog = new QSelectionDialog("新建选择题",kemu,"");// 创建修改名片的对话框对象
                dialog.setVisible(true);// 设置修改名片的对话框为可见
            } else if (i == 1) {
                QBlankDialog dialog = new QBlankDialog("新建填空题",kemu,"");// 创建修改名片的对话框对象
                dialog.setVisible(true);// 设置修改名片的对话框为可见
            } else if (i == 2) {
                QJudgeDialog dialog = new QJudgeDialog("新建判断题",kemu,"");// 创建修改名片的对话框对象
                dialog.setVisible(true);// 设置修改名片的对话框为可见
            } else if (i == 3) {
                QExplainationDialog dialog = new QExplainationDialog("新建名词解释题",kemu,"");
                dialog.setVisible(true);
            } else if (i == 4) {
                QCompreDialog dialog = new QCompreDialog("新建综合题",kemu,"");
                dialog.setVisible(true);
            } else if (i == 5) {
                QDiscussDialog dialog = new QDiscussDialog("新建综合题",kemu,"");
                dialog.setVisible(true);
            }
        }
    }
}