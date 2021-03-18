import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.sql.*;
import javax.swing.table.*;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import java.awt.event.ActionListener;
import javax.swing.table.DefaultTableModel;
import java.net.URL;


public class tikuyuanUI extends JFrame implements ActionListener{
    private JLabel backLabel;
    // 创建窗体的Map类型集合对象
    private Map<String, JInternalFrame> ifs = new HashMap<String, JInternalFrame>();

    //数据库表
    private String []table={"selection","blank","judge","explanation","comprehensive","discussion"};
    Object []obj={"选择题","填空题","判断题","名词解释","综合题","论述题"};

    private Dao con = new Dao();
    private JScrollPane scrollpane_table,scrollpane_text;

    //表格相关
    Object []obj_editRow;
    private JTable table_show;
    private DefaultTableModel dtm;

    //文本展示相关
    private JTextArea text_paper;

    //-----------------------------------------题库员部门-------------------------------------------
    //menu下选择试题
    private JDialog dialog_select;
    private JButton button_select;
    private JRadioButton radio_all,radio_Qno,radio_require;
    JTextField text_QTno2,text_Qno2;
    JComboBox combo_Qtype2;
    private JCheckBox check_question,check_difficulty;
    private JComboBox combo_operator1,combo_logic,combo_operator2;
    //menu下查询试题
    private JButton button_ques;
    private JTextField text_Qinclude;
    private JTextField text_Darrange1,text_Darrange2;


    final JPanel rightPanel = new JPanel();

    public tikuyuanUI() {
        //主窗体
        super("题库管理与试卷生成系统");
        //this.setExtendedState(MAXIMIZED_BOTH);
        this.getContentPane().setLayout(new BorderLayout());//设置布局管理器
        this.setBounds(50, 50, 1200, 900);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //this.setResizable(false);

        //上部导航栏
        final JTabbedPane navigationPanel = createNavigationPanel(); // 创建导航标签面板
        getContentPane().add(navigationPanel,BorderLayout.NORTH);

        //下部内容面板
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(0, 0));
        rightPanel.setBackground(new Color(255, 255, 247));
        rightPanel.setBorder(new TitledBorder(null, "",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, null, null));
        getContentPane().add(rightPanel, BorderLayout.CENTER);

        URL resource = this.getClass().getResource("image/welcome.jpg");
        ImageIcon icon = new ImageIcon(resource);
        backgroundLabel.setIcon(icon);
        backgroundLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        backgroundLabel.setHorizontalAlignment(SwingConstants.CENTER);
        rightPanel.add(backgroundLabel, BorderLayout.CENTER);

        //设置题库Jtable
        dtm = new DefaultTableModel();
        table_show = new JTable();
        table_show.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        //table_show.getModel().addTableModelListener(this);
//        table_show.getSelectionModel().addListSelectionListener(this);
        table_show.getTableHeader().setReorderingAllowed(false);
        table_show.getTableHeader().setResizingAllowed(false);
        scrollpane_table = new JScrollPane(table_show);
        //设置试卷JtextArea
        text_paper = new JTextArea();
        text_paper.setFont(new Font("宋体",Font.PLAIN,16));
        text_paper.setLineWrap(true);
        scrollpane_text = new JScrollPane(text_paper);
        //设置背景
        backLabel = new JLabel();// 背景标签
        backLabel.setVerticalAlignment(SwingConstants.TOP);//设置背景标签垂直对齐方式
        backLabel.setHorizontalAlignment(SwingConstants.CENTER);//水平对齐方式
        updateBackImage(); // 调用初始化背景标签的方法

        this.createDialogSelect();
        this.setVisible(true);

    }


    //主窗体的main（）入口
    public static void main(String[] args) {
        try{
            tikuyuanUI frame = new tikuyuanUI();
            frame.setVisible(true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /************************窗口GUI**********************/
    //创建导航标签面板
    private JTabbedPane createNavigationPanel() {
        //导航面板
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFocusable(false);
        tabbedPane.setBackground(Color.white);
        tabbedPane.setBorder(new BevelBorder(BevelBorder.RAISED));

        // 题库管理面板
        JPanel quesManagePanel = new JPanel();
        quesManagePanel.setLayout(new BoxLayout(quesManagePanel, BoxLayout.X_AXIS));
        quesManagePanel.add(menuitem_kemu);//科目
        menuitem_kemu.addActionListener(this);
        quesManagePanel.add(menuitem_point);//知识点
        menuitem_point.addActionListener(this);
        quesManagePanel.add(menuitem_ques);//题库管理
        menuitem_ques.addActionListener(this);
        quesManagePanel.add(menuitem_insert);//录入新题
        menuitem_insert.addActionListener(this);
        quesManagePanel.add(menuitem_select);//查询题目
        menuitem_select.addActionListener(this);


        tabbedPane.addTab("   题库管理   ", null, quesManagePanel, "题库管理");


        return tabbedPane;
    }

    //查询试题
    private void createDialogSelect()
    {
        dialog_select=new JDialog(this,"查询试题",true);
        dialog_select.setSize(280,260);
        dialog_select.setDefaultCloseOperation(HIDE_ON_CLOSE);
        dialog_select.setResizable(false);
        dialog_select.setLayout(new FlowLayout(FlowLayout.LEFT));
        dialog_select.add(new JLabel("请选择要查询的题型: "));
        combo_Qtype2=new JComboBox(obj);
        dialog_select.add(combo_Qtype2);
        radio_all=new JRadioButton("查询全部",true);
        dialog_select.add(radio_all);
        dialog_select.add(new JLabel("　　　　　　　　　　　"));
        radio_Qno=new JRadioButton("按题号");
        text_QTno2=new JTextField("1");
        text_QTno2.setEditable(false);
        text_Qno2=new JTextField(6);
        dialog_select.add(radio_Qno);
        dialog_select.add(text_QTno2);
        dialog_select.add(text_Qno2);
        dialog_select.add(new JLabel("　　　　　　　"));
        radio_require=new JRadioButton("按条件查询");
        dialog_select.add(radio_require);
        dialog_select.add(new JLabel("　　　　　　　　　"));
        ButtonGroup bg_select=new ButtonGroup();
        bg_select.add(radio_all);
        bg_select.add(radio_Qno);
        bg_select.add(radio_require);
        check_question=new JCheckBox("题目包含",true);
        dialog_select.add(check_question);
        text_Qinclude=new JTextField(16);
        dialog_select.add(text_Qinclude);
        check_difficulty=new JCheckBox("难度",true);
        dialog_select.add(check_difficulty);
        Object []obj_operator={">=",">","=","<","<="};
        Object []obj_logic={"AND","OR"};
        combo_operator1=new JComboBox(obj_operator);
        combo_operator2=new JComboBox(obj_operator);
        combo_operator2.setSelectedIndex(4);
        combo_logic=new JComboBox(obj_logic);
        text_Darrange1=new JTextField(2);
        text_Darrange2=new JTextField(2);
        dialog_select.add(combo_operator1);
        dialog_select.add(text_Darrange1);
        dialog_select.add(combo_logic);
        dialog_select.add(combo_operator2);
        dialog_select.add(text_Darrange2);
        button_select=new JButton("查 询");
        button_select.addActionListener(this);
        dialog_select.add(new JLabel("　　　　　　　"));
        dialog_select.add(button_select);
    }


    /*****************************table与text展现***********************************/

    //查询试题
    public void selectSQL(String sql,int i)
    {
        try{
            int n;
            Object[] obj_selection={"题号","题目","A选项","B选项","C选项","D选项","答案","知识点","难度","出题次数"};
            Object[] obj_blank={"题号","题目","填空1","填空2","填空3","填空4","填空5","填空6","填空7","填空8","知识点","难度","出题次数"};
            Object[] obj_other={"题号","题目","答案","知识点","难度","出题次数"};
            table_show.getSelectionModel().clearSelection();
            dtm = (DefaultTableModel)table_show.getModel();
            dtm.setRowCount(0);
            //选择题
            if (i==0)
            {
                dtm.setColumnCount(10);
                n=con.select(sql,dtm);
                dtm.setColumnIdentifiers(obj_selection);
            }else if(i==1){
                dtm.setColumnCount(13);
                n=con.select(sql,dtm);
                dtm.setColumnIdentifiers(obj_blank);
            }else{
                dtm.setColumnCount(6);
                n=con.select(sql,dtm);
                dtm.setColumnIdentifiers(obj_other);
            }
            int wid = this.getWidth();
            TableColumn []column = new TableColumn[table_show.getColumnCount()];
            for(int k=0;k<table_show.getColumnCount();k++)
                column[k]=table_show.getColumnModel().getColumn(k);
            column[0].setPreferredWidth(50);
            //选择题
            if(i==0)
            {
                column[1].setPreferredWidth(Math.max(200,(wid-160)/3));
                for(int p=2; p<6; p++)
                    column[p].setPreferredWidth(Math.max(100,(wid-170)/6));
                column[6].setPreferredWidth(30);
                column[7].setPreferredWidth(50);
                column[8].setPreferredWidth(30);
                column[9].setPreferredWidth(50);
            }
            //填空题
            else if(i==1)
            {
                column[1].setPreferredWidth(150);
                for(int p=2; p<4; p++)
                    column[p].setPreferredWidth(Math.max(100,(wid-170)/6));
                column[9].setPreferredWidth(30);
                column[10].setPreferredWidth(50);
                column[11].setPreferredWidth(30);
                column[12].setPreferredWidth(50);
            }
            //判断题
            else if (i==2)
            {
                column[1].setPreferredWidth(Math.max(100,wid-220));
                column[2].setPreferredWidth(100);
                column[3].setPreferredWidth(50);
            }
            else
            {
                column[1].setPreferredWidth(140);
                column[2].setPreferredWidth(Math.max(100,wid-270));
                column[3].setPreferredWidth(50);
                column[4].setPreferredWidth(30);
                column[5].setPreferredWidth(50);
            }
            table_show.invalidate();
            dialog_select.setVisible(false);
            if(n==0)
            {
                JOptionPane.showMessageDialog(this,"没有找到符合条件的结果！","查询",JOptionPane.OK_OPTION);
            }
        }catch (Exception e){
            JOptionPane.showMessageDialog(this,"无法执行查询，请检查数据！","查询",JOptionPane.OK_OPTION);
        }

    }

    /**********************抽象方法实现*****************************/

    //发生动作时调用
    public void actionPerformed(ActionEvent e) {
        String sql;
        if (e.getSource()== menuitem_kemu){
            kemuUI kemu  = new kemuUI();
            kemu.setVisible(true);
        }
        else if (e.getSource() == menuitem_point){
            zhishidianUI zhishidian = new zhishidianUI();
            zhishidian.setVisible(true);
        }
        else if (e.getSource()==menuitem_ques){
            rightPanel.removeAll();
            rightPanel.add(new QuesPanel(), BorderLayout.CENTER);
            SwingUtilities.updateComponentTreeUI(rightPanel);
        }
        else if (e.getSource() == menuitem_insert){
            insertQuesUI insert = new insertQuesUI();
        }
        else if(e.getSource()==button_select){
            int i;
            i=combo_Qtype2.getSelectedIndex();
            sql="SELECT * FROM "+table[i];
            if(radio_all.isSelected())
                sql+=";";
            else if(radio_Qno.isSelected())
                sql+=" WHERE Qno='"+text_QTno2.getText()+text_Qno2.getText()+"';";
            else
            {
                String str_q,str_d;
                str_q="Question LIKE '%"+text_Qinclude.getText()+"%'";
                str_d="(Difficulty"+combo_operator1.getSelectedItem().toString()+text_Darrange1.getText()+" "+
                        combo_logic.getSelectedItem().toString()+" Difficulty"+combo_operator2.getSelectedItem().toString()
                        +text_Darrange2.getText()+")";
                if(check_question.isSelected()&&check_difficulty.isSelected())
                    sql+=" WHERE "+str_q+" AND "+str_d+";";
                else if(check_question.isSelected())
                    sql+=" WHERE "+str_q+";";
                else if(check_difficulty.isSelected())
                    sql+=" WHERE "+str_d+";";
                else
                    sql+=";";
            }
            this.selectSQL(sql,i);
            rightPanel.removeAll();
            scrollpane_table.setEnabled(false);
            rightPanel.add(scrollpane_table,BorderLayout.CENTER);
            SwingUtilities.updateComponentTreeUI(rightPanel);

        }
        else if(e.getSource()==menuitem_select){
            dialog_select.setLocation(this.getX()+this.getWidth()/2-140,this.getY()+this.getHeight()/2-130);
            dialog_select.setVisible(true);
        }
    }


    /************************试卷table方法*************************/

    //table中选中题目
    public void valueChanged(ListSelectionEvent e) {
        if(table_show.getSelectedRowCount()>0)
        {
            int row = table_show.getSelectedRow();
            int col = table_show.getColumnCount();
            obj_editRow = new Object[col];
            for (int i=0;i<col;i++)
                obj_editRow[i]=table_show.getValueAt(row,i);
        }
    }


    /** *********************辅助方法************************* */

    //找出所有科目
    public Vector<String> getKemu() {
        Vector<String> obj_kemu = new Vector<String>();
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
        return obj_kemu;
    }

    DefaultListModel loadListItems(String kemu) {
        String sql = "select Point from kemu_point where Kname='" + kemu +"';";
        ResultSet rs= con.getRs(sql);
        DefaultListModel listModel = new DefaultListModel();
        listModel.clear();
        try {
            while(rs.next()) {
                listModel.addElement(rs.getString("Point"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(kemuUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listModel ;
    }

    // 设置背景图片
    private void updateBackImage() {
        if (backLabel != null) {
            int backw = tikuyuanUI.this.getWidth();
            int backh = this.getHeight();
            backLabel.setSize(backw, backh);
            backLabel.setText("<html><body><image width='" + backw
                    + "' height='" + (backh - 110) + "' src="
                    + tikuyuanUI.this.getClass().getResource("image/welcome.jpg")
                    + "'></img></body></html>");
        }
    }



    //添加导航面板的按钮
    //2 题库管理
    private JButton menuitem_ques = new JButton("题库管理");
    private JButton menuitem_kemu = new JButton("科目管理");
    private JButton menuitem_point = new JButton("添加知识点");
    private JButton menuitem_insert = new JButton("录入新题");
    private JButton menuitem_select = new JButton("查询题目");


    final JLabel backgroundLabel = new JLabel();



}