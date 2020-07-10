import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.sql.*;
import javax.swing.table.*;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
public class guanliyuanUI extends JFrame implements ActionListener,ListSelectionListener,CaretListener{
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
    //menu下题
    private JDialog dialog_select;
    private JButton button_select;
    private JRadioButton radio_all,radio_Qno,radio_require;
    JTextField text_QTno2,text_Qno2;
    JComboBox combo_Qtype2;
    private JCheckBox check_question,check_difficulty;
    private JComboBox combo_operator1,combo_logic,combo_operator2;

    //---------------------------------------------组卷部分------------------------------
    Vector<String> obj_kemu;
    JComboBox combo_kemu2;

    private int nextid ;
    private int Pno;
    //查询试题
    JDialog dialog_paper;
    JRadioButton radio_paperall,radio_papername;
    JButton button_paper;
    JTextField text_Qinclude;
    JTextField text_papername,text_Darrange1,text_Darrange2;

    //(自动创建新试卷）
    JDialog dialog_new;
    JButton button_add1;

    //自动创建结束
    private  JTextField text_Pdate;
    private JTextField text_Pno2,text_Pname2,text_Pname3;
    private JDialog dialog_newfinish;
    private JPanel[] panel;
    private JTextField []text_quantity,text_score;
    private JLabel []label_score;
    private JLabel label_total;
    private JButton button_addfinish1;
    private JTextField text_dif1,text_dif2;

    //生成试卷
    private String []chineseNum={"一","二","三","四","五","六"};

    final JPanel rightPanel = new JPanel();

    public guanliyuanUI() {
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
            guanliyuanUI frame = new guanliyuanUI();
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

        //系统管理面板
        JPanel sysManagePanel = new JPanel();
        sysManagePanel.setBackground(Color.white);
        sysManagePanel.setLayout(new BoxLayout(sysManagePanel, BoxLayout.X_AXIS));
        sysManagePanel.add(menuitem_user);//查找用户
        //sysManagePanel.add(menuitem_help);//帮助
        menuitem_user.addActionListener(this);
        //menuitem_help.addActionListener(this);

        // 题库管理面板
        JPanel quesManagePanel = new JPanel();
        quesManagePanel.setLayout(new BoxLayout(quesManagePanel, BoxLayout.X_AXIS));
        quesManagePanel.add(menuitem_kemu);//科目
        menuitem_kemu.addActionListener(this);
        quesManagePanel.add(menuitem_point);//知识点
        menuitem_point.addActionListener(this);
        quesManagePanel.add(menuitem_insert);//录入新题
        menuitem_insert.addActionListener(this);
        quesManagePanel.add(menuitem_ques);//题库管理
        menuitem_ques.addActionListener(this);
        quesManagePanel.add(menuitem_select);//查询题目
        menuitem_select.addActionListener(this);


        //组卷管理面板
        JPanel paperManagePanel = new JPanel();
        paperManagePanel.setLayout(new BoxLayout(paperManagePanel, BoxLayout.X_AXIS));
        paperManagePanel.add(menuitem_new1);
        menuitem_new1.addActionListener(this);
        paperManagePanel.add(menuitem_new2);
        menuitem_paper.addActionListener(this);
        paperManagePanel.add(menuitem_paper);
        menuitem_new2.addActionListener(this);
        paperManagePanel.add(menuitem_open);
        menuitem_open.addActionListener(this);
        paperManagePanel.add(menuitem_remove);
        menuitem_remove.addActionListener(this);
        paperManagePanel.add(menuitem_save);
        menuitem_save.addActionListener(this);

        tabbedPane.addTab("   系统管理   ", null, sysManagePanel, "系统管理");
        tabbedPane.addTab("   题库管理   ", null, quesManagePanel, "题库管理");
        tabbedPane.addTab("   组卷管理   ", null, paperManagePanel, "组卷管理");


        return tabbedPane;
    }

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

    //查询试卷页面
    public void createDialogPaper(){
        dialog_paper=new JDialog(this,"查看试题",true);
        dialog_paper.setSize(230,200);
        dialog_paper.setDefaultCloseOperation(HIDE_ON_CLOSE);
        dialog_paper.setResizable(true);
        dialog_paper.setLayout(new FlowLayout(FlowLayout.LEFT));
        dialog_paper.add(new JLabel("选择要查看的试卷:   "));
        //两个查看条件
        //查看全部试卷
        radio_paperall = new JRadioButton("查看所有试卷",true);
        dialog_paper.add(radio_paperall);
        //查看题目的试卷
        radio_papername = new JRadioButton("按试卷名搜索");
        dialog_paper.add(radio_papername);
        text_papername=new JTextField(16);
        dialog_paper.add(text_papername);
        ButtonGroup bg_ques = new ButtonGroup();
        bg_ques.add(radio_paperall);
        bg_ques.add(radio_papername);
        button_paper=new JButton("查找试卷");
        button_paper.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jButton_paperActionPerformed(e);
            }
        });
        dialog_paper.add(new JLabel("　　　　　　　"));
        dialog_paper.add(button_paper);
    }

    //自动组卷页面(选择科目和输入名称）
    public void createDialogNew(){
        dialog_new = new JDialog(this,"试卷基本信息",true);
        dialog_new.getContentPane().setLayout(new FlowLayout(FlowLayout. LEFT));//设置布局管理器
        dialog_new.setBounds(400, 300, 300, 250);
        dialog_new.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        dialog_new.setResizable(true);
        //找出所有科目
        obj_kemu = new Vector<String>();
        obj_kemu = getKemu();
        //跳转至新页面
        //1.选择科目
        dialog_new.add(new JLabel("·选择科目:         "));
        combo_kemu2 = new JComboBox(obj_kemu);
        dialog_new.add(combo_kemu2);
        //2.试卷编号;
        dialog_new.add(new JLabel("·当前试卷编号："));
        int Pno = getNextid();
        text_Pno2=new JTextField(Pno+"");
        text_Pno2.setEditable(false);
        dialog_new.add(text_Pno2);
        //3.试题名称
        dialog_new.add(new JLabel("·试题名称:  "));
        text_Pname2 = new JTextField(12);
        dialog_new.add(text_Pname2);
        //4.生成时间
        dialog_new.add(new JLabel("·试题生成时间: "));
        java.util.Date date = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date_tmp = format.format(date); //2013-01-14
        text_Pdate = new JTextField(date_tmp);
        text_Pdate.setEditable(false);
        dialog_new.add(text_Pdate);
        dialog_new.add(new JLabel("　　　　　　　"));
        button_add1=new JButton("开始自动组卷");
        button_add1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                jButton_add1ActionPerformed(evt);
            }
        });
        dialog_new.add(button_add1);
    }

    //自动组卷页面(结束)
    private void createDialogNewFinish(String kemu)
    {
        int i,n=obj.length;
        dialog_newfinish=new JDialog(this,"自动新建试卷",true);
        dialog_newfinish.setSize((n+2)*30+200,600);
        dialog_newfinish.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        dialog_newfinish.setResizable(true);
        //题号和题目
        JPanel panel_main=new JPanel(new GridLayout(n+2,1));
        //按钮Button_new2
        JPanel panel_south=new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel=new JPanel[n+2];
        for(i=0; i<n+2; i++)
        {
            panel[i]=new JPanel(new FlowLayout(FlowLayout.LEFT));
            panel_main.add(panel[i]);
        }
        panel[0].add(new JLabel("试卷名称 "));
        text_Pname3=new JTextField(10);
        text_Pname3.setText(text_Pname2.getText());
        text_Pname3.setEditable(false);
        panel[0].add(text_Pname3);
        text_quantity=new JTextField[n];
        text_score=new JTextField[n];
        label_score=new JLabel[n];
        panel[0].add(new JLabel("总计"));
        label_total=new JLabel("0");
        panel[0].add(label_total);
        panel[0].add(new JLabel("分"));
        //选择题型和分数
        for(i=0; i<n; i++)
        {
            panel[i+1].add(new JLabel(" "+obj[i].toString()+" "));
            text_score[i]=new JTextField("0",3);
            text_score[i].setHorizontalAlignment(JTextField.CENTER);
            text_score[i].addCaretListener(this);
            text_quantity[i]=new JTextField("0",3);
            text_quantity[i].setHorizontalAlignment(JTextField.CENTER);
            text_quantity[i].addCaretListener(this);
            label_score[i]=new JLabel("0",JLabel.CENTER);
            panel[i+1].add(text_score[i]);
            panel[i+1].add(new JLabel("分 ×"));
            panel[i+1].add(text_quantity[i]);
            panel[i+1].add(new JLabel("题 ="));
            panel[i+1].add(label_score[i]);
            panel[i+1].add(new JLabel("分"));
        }
        //设置难度范围
        text_dif1=new JTextField("",3);
        text_dif1.setHorizontalAlignment(JTextField.CENTER);
        text_dif2=new JTextField("",3);
        text_dif2.setHorizontalAlignment(JTextField.CENTER);
        panel[n+1].add(new JLabel(" 难度(简单1～困难5)"));
        panel[n+1].add(text_dif1);
        panel[n+1].add(new JLabel(" ～  "));
        panel[n+1].add(text_dif2);
        //开始写知识点选择
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        jListUnselected = new JList();
        jListSelected = new JList();
        jScrollPane1 = new JScrollPane();
        jScrollPane2 = new JScrollPane();
        jButtonSelect = new JButton();
        jButtonUnselect = new JButton();
        button_addfinish1 = new JButton();
        jPanel2 = new JPanel();
        //选修课程
        jLabel1.setText("所有知识点");
        int k = combo_kemu2.getSelectedIndex();
        jListUnselected.setModel(loadListItems(obj_kemu.get(k)));
        jScrollPane1.setViewportView(jListUnselected);
        jLabel2.setText("已选知识点");
        jListSelected.setModel(loadListItems(""));
        jScrollPane2.setViewportView(jListSelected);
        jButtonSelect.setText("-->");
        jButtonSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSelectActionPerformed(evt);
            }
        });
        jButtonUnselect.setText("<--");
        jButtonUnselect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                jButtonUnselectActionPerformed(evt);
            }
        });
        button_addfinish1.setText("自动组卷完成");
        button_addfinish1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                jButton_addfinish1ActionPerformed(evt);
            }
        });
        //知识点界面GUI
        {
            javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
            jPanel2.setLayout(jPanel2Layout);
            jPanel2Layout.setHorizontalGroup(
                    jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                    .addContainerGap()
                                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                                    .addComponent(jLabel1)
                                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 52, Short.MAX_VALUE)
                                                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                            .addComponent(jButtonSelect)
                                                                            .addComponent(jButtonUnselect))
                                                                    .addGap(49, 49, 49))))
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(button_addfinish1)
                                                    .addGap(16, 16, 16)))
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel2)
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                    .addGap(20, 20, 20)))
                                    .addContainerGap())
            );
            jPanel2Layout.setVerticalGroup(
                    jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel1)
                                            .addComponent(jLabel2))
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                                                            .addComponent(jScrollPane2)))
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                    .addGap(42, 42, 42)
                                                    .addComponent(jButtonSelect)
                                                    .addGap(37, 37, 37)
                                                    .addComponent(jButtonUnselect)))
                                    .addGap(18, 18, 18)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(button_addfinish1))
                                    .addContainerGap(14, Short.MAX_VALUE))
            );
        }
        panel_south.add(jPanel2);
        dialog_newfinish.add(panel_main);
        dialog_newfinish.add(panel_south,"South");

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


    //查询试卷
    public void selectPaper(String sql)
    {
        try{
            int n;
            Object[] obj_paper={"试卷号","试卷名称","创建日期","题目内容","答案内容"};
            table_show.getSelectionModel().clearSelection();
            dtm = (DefaultTableModel)table_show.getModel();
            dtm.setRowCount(0);
            //选择题
            dtm.setColumnCount(5);
            n=con.select(sql,dtm);
            dtm.setColumnIdentifiers(obj_paper);
            int wid = this.getWidth();
            TableColumn []column = new TableColumn[table_show.getColumnCount()];
            for(int k=0;k<table_show.getColumnCount();k++)
                column[k]=table_show.getColumnModel().getColumn(k);
            column[0].setPreferredWidth(50);
            column[1].setPreferredWidth(100);
            column[2].setPreferredWidth(80);
            column[3].setPreferredWidth(Math.max(200,(wid-230)/2));
            column[4].setPreferredWidth(Math.max(200,(wid-230)/2));
            table_show.invalidate();
            //table_show.repaint();
            if(n==0)
            {
                JOptionPane.showMessageDialog(this,"没有找到符合条件的结果！","查询",JOptionPane.OK_OPTION);
            }
        }catch (Exception e){
            JOptionPane.showMessageDialog(this,"无法执行查询，请检查数据！","查询",JOptionPane.OK_OPTION);
            e.printStackTrace();
        }

    }

    //查询试卷展示出来
    public void showPaper(String pno)
    {
        String sql;
        ResultSet rs;
        try{
            sql = "SELECT * FROM Paperfinal WHERE Pno='"+pno+"';";
            rs = con.getRs(sql);
            String Pcontent="",Pans="";
            //test
            while(rs.next()) {
                Pcontent = rs.getString("Pcontent");
                Pans = rs.getString("Pans");
            }
            text_paper.setText("");
            text_paper.append("-------------------------【试题部分】-------------------------\n");
            text_paper.append(Pcontent);
            text_paper.append("\r\n");
            text_paper.append("-------------------------【答案部分】-------------------------\n");
            text_paper.append(Pans);
            text_paper.setEditable(false);
            //该所有
            rightPanel.removeAll();
            rightPanel.add(scrollpane_text, BorderLayout.CENTER);
            SwingUtilities.updateComponentTreeUI(rightPanel);
            this.validate();
            scrollpane_text.repaint();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    /**********************抽象方法实现*****************************/

    //发生动作时调用
    public void actionPerformed(ActionEvent e) {
        String sql;
        //系统管理部分
//        if (e.getSource() == menuitem_help) {
//            try {
//                Runtime.getRuntime().exec("C:\\WINDOWS\\system32\\notepad.exe 帮助.txt");
//            } catch (Exception ex) {
//                JOptionPane.showMessageDialog(this, "找不到帮助文件！", "帮助", JOptionPane.OK_OPTION);
//            }
//        }
        if (e.getSource() == menuitem_user) {
            rightPanel.removeAll();
            rightPanel.add(new UserPanel(), BorderLayout.CENTER);
            SwingUtilities.updateComponentTreeUI(rightPanel);
        }
        else if (e.getSource()== menuitem_kemu){
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
        //试卷生成部分
        //手动组卷
        else if (e.getSource()==menuitem_new1){
            rightPanel.removeAll();
            rightPanel.add(new ShoudongPanel(), BorderLayout.CENTER);
            SwingUtilities.updateComponentTreeUI(rightPanel);
        }
        //自动组卷
        else if(e.getSource()==menuitem_new2){
            this.createDialogNew();
            dialog_new.setLocation(this.getX()+this.getWidth()/2-140,this.getY()+this.getHeight()/2);
            dialog_new.setVisible(true);
        }
        //查询试卷
        else if (e.getSource()==menuitem_paper){
            this.createDialogPaper();
            dialog_paper.setLocation(this.getX()+this.getWidth()/2-140,this.getY()+(this.getHeight())/2);
            dialog_paper.setVisible(true);
        }
        //打开试卷
        else if(e.getSource()==menuitem_open){
            int []selectRows = table_show.getSelectedRows();
            dtm = (DefaultTableModel)table_show.getModel();
            for (int i=0;i<selectRows.length;i++){
                String pno = dtm.getValueAt(selectRows[i],0).toString();
                System.out.println("展示试卷的Pno"+pno);
                showPaper(pno);
            }
        }
        //删除试卷
        else if(e.getSource()==menuitem_remove) {
            int r=JOptionPane.showConfirmDialog(this,"是否删除该试卷？","删除",JOptionPane.OK_CANCEL_OPTION);
            int []selectRows=table_show.getSelectedRows();
            if(r==JOptionPane.OK_OPTION)
            {
                dtm=(DefaultTableModel)table_show.getModel();
                //test
                sql = "";
                try
                {
                    for(int i=0; i<selectRows.length; i++)
                    {
                        sql="DELETE FROM Paperfinal WHERE Pno='"+dtm.getValueAt(selectRows[i],0)+"';";
                        con.dataUpdate(sql);
                    }
                    table_show.getSelectionModel().clearSelection();
                    for(int i=0; i<selectRows.length; i++)
                        dtm.removeRow(selectRows[i]-i);
                    JOptionPane.showMessageDialog(this,"已成功删除试卷！","删除",JOptionPane.INFORMATION_MESSAGE);
                }
                catch(Exception ex){
                    JOptionPane.showMessageDialog(this,"未能删除试题","错误",JOptionPane.INFORMATION_MESSAGE);
                }
                //test
                System.out.println(sql);
            }
        }
        //保存试卷
        else if(e.getSource()==menuitem_save){
            int []selectRows = table_show.getSelectedRows();
            dtm = (DefaultTableModel)table_show.getModel();
            System.out.println("被选中的"+selectRows.length);
            int Pno = 0;
            String Pname = "";
            String quesAll="";
            String ansAll="";
            for (int i=0;i<selectRows.length;i++){
                Pno = Integer.parseInt(dtm.getValueAt(selectRows[i],0).toString());
                Pname = dtm.getValueAt(selectRows[i],1).toString();
                quesAll=dtm.getValueAt(selectRows[i],3).toString();
                ansAll=dtm.getValueAt(selectRows[i],4).toString();
            }
            FileDialog saveAs=new FileDialog(this,"保存为word",FileDialog.SAVE);
            saveAs.setFile(Pno+Pname+".doc");
            saveAs.setVisible(true);
            String fileName=saveAs.getDirectory()+saveAs.getFile();
            try
            {
                File file=new File(fileName);
                FileWriter writeOut=new FileWriter(file);
                writeOut.write("【试题部分】\n"+quesAll+"【答案部分】\n"+ansAll);
                writeOut.close();
            }
            catch(IOException ioe)
            {
                JOptionPane.showMessageDialog(this,"保存为word文件失败！","错误",JOptionPane.OK_OPTION);
            }
        }
    }



    //查找试卷 button_paper
    public void jButton_paperActionPerformed(ActionEvent evt){
        String sql  ="SELECT * FROM paperfinal";
        if(radio_paperall.isSelected())
            sql+=";";
        else if(radio_papername.isSelected())
            sql+=" WHERE Pname LIKE '%"+text_papername.getText()+"%';";
        this.selectPaper(sql);
        rightPanel.removeAll();
        scrollpane_table.setEnabled(false);
        rightPanel.add(scrollpane_table,BorderLayout.CENTER);
        SwingUtilities.updateComponentTreeUI(rightPanel);
    }

    //开始自动组卷
    public void jButton_add1ActionPerformed(ActionEvent evt) {
        //获得科目
        int j = combo_kemu2.getSelectedIndex();
        String kemu = obj_kemu.get(j);
        System.out.println("当前试卷的科目是:"+kemu);
        this.createDialogNewFinish(kemu);
        dialog_newfinish.setLocation(this.getX()+this.getWidth()/2-200,this.getY()+this.getHeight()/2-200);
        dialog_newfinish.setVisible(true);
    }

    //自动组卷完成
    public void jButton_addfinish1ActionPerformed(ActionEvent evt) {
        System.out.println("自动组卷完成被选中！！！！！");
        Object []pointSelected={};
        pointSelected = ((DefaultListModel)jListSelected.getModel()).toArray();
//        for (int i=0;i<pointSelected.length;i++){
//            System.out.println((pointSelected[i]).toString());
//        }
        String sql;
        try
        {
            //Part 1 先找出题目
            int i,j,dif1,dif2,k,p,sum=0,n=obj.length;
            DefaultTableModel tmp=new DefaultTableModel();
            int []score=new int[n];
            int []quantity=new int[n];
            String [][]str_qno=new String[n][];
            for(i=0; i<n; i++)
            {
                score[i]=Integer.parseInt(text_score[i].getText());
                quantity[i]=Integer.parseInt(text_quantity[i].getText());
                sum+=quantity[i];
            }
            dif1=Integer.parseInt(text_dif1.getText());
            dif2=Integer.parseInt(text_dif2.getText());
            if (sum==0)
                throw new Exception("没有题目！");
            if (dif1>dif2)
                throw new Exception("难度设置范围错误");
            sql="SELECT * FROM PaperInfo WHERE Pno='"+text_Pno2.getText()+"';";
            tmp.setColumnCount(4);
            tmp.setRowCount(0);
            con.select(sql,tmp);
            if(tmp.getRowCount()!=0)
                throw new Exception("试卷已存在");
            String temp_q;
            String temp_sql;
            for(i=0; i<n; i++)
            {
                tmp.setRowCount(0);
                tmp.setColumnCount(1);
                //先把符合难度要求和知识点的所有题目都找出来
                sql="SELECT Qno FROM "+table[i]+" WHERE Difficulty>="+dif1+" AND Difficulty<="+dif2;
                for (int l=0;l<pointSelected.length;l++){
                    if (l==0){
                        sql+=" AND (";
                    }
                    if (l!=pointSelected.length-1){
                        sql+="Point='"+pointSelected[l].toString()+"' OR ";
                    }else {
                        sql+="Point='"+pointSelected[l].toString()+"');";
                    }
                }
                //just test
                System.out.println(sql);
                //test
                con.select(sql, tmp);
                k=tmp.getRowCount();
                if(k<quantity[i])
                    throw new Exception("题目不足");
                //
                str_qno[i]=new String[quantity[i]];
                for(j=0; j<quantity[i]; j++)
                {
                    p=(int)(Math.random()*(k-j));
                    //随机产生题号
                    str_qno[i][j]=tmp.getValueAt(p,0).toString();
                    //更新该题号的出题times属性
                    temp_q = tmp.getValueAt(p,0).toString();
                    temp_sql="UPDATE "+table[i]+" SET Times=(Times+1) WHERE Qno="+temp_q+";";
                    con.dataUpdate(temp_sql);
                    tmp.removeRow(p);
                }
                sql="INSERT INTO PaperInfo VALUES('"+text_Pno2.getText()+"','"+Integer.toString(i+1)+"',"+score[i]+","+quantity[i]+");";
                if(quantity[i]!=0)
                    con.dataUpdate(sql);
                for(j=0; j<quantity[i]; j++)
                {
                    sql="INSERT INTO Paper VALUES('"+text_Pno2.getText()+"','"+str_qno[i][j]+"');";
                    con.dataUpdate(sql);
                }
            }


            //Part 2 把题目整合内容放入paperfinal并展示
            String quesAll="",ansAll="";
            DefaultTableModel tmp_pi;
            DefaultTableModel tmp_q,tmp_a;//存放问题和答案
            String sql1="";
            int s,q,qt;
            int pno = Integer.parseInt(text_Pno2.getText());
            sql="SELECT * FROM PaperInfo WHERE Pno='"+pno+"';";
            tmp_pi=new DefaultTableModel();
            tmp_pi.setColumnCount(4);
            tmp_pi.setRowCount(0);
            con.select(sql,tmp_pi);
            if(tmp_pi.getRowCount()==0)
                throw new Exception("试卷不存在");
            tmp_q = new DefaultTableModel();
            tmp_a = new DefaultTableModel();
            text_paper.setText("");
            //1.找出题目内容 tmp_q 2.找出题目答案tmp_a;
            for(i=0; i<tmp_pi.getRowCount(); i++)
            {
                qt=Integer.parseInt(tmp_pi.getValueAt(i,1).toString());
                s=Integer.parseInt(tmp_pi.getValueAt(i,2).toString());
                q=Integer.parseInt(tmp_pi.getValueAt(i,3).toString());
                if(qt==1)//selection
                 {
                     tmp_q.setColumnCount(5);
                     sql="SELECT Question,ItemA,ItemB,ItemC,ItemD FROM "+table[qt-1]+",Paper WHERE Pno='"+pno+"' AND "+table[qt-1]+".Qno = Paper.Qno;";
                     tmp_a.setColumnCount(1);
                     sql1="SELECT Answer FROM "+table[qt-1]+",Paper WHERE Pno='"+pno+"' AND "+table[qt-1]+".Qno = Paper.Qno;";
                 }
                else if (qt==2)//blank
                    {
                        tmp_q.setColumnCount(1);
                        sql="SELECT Question FROM "+table[qt-1]+",Paper WHERE Pno='"+pno+"' AND "+table[qt-1]+".Qno = Paper.Qno;";
                        tmp_a.setColumnCount(8);
                        sql1="SELECT Answer1,Answer2,Answer3,Answer4,Answer5,Answer6,Answer7,Answer8 FROM "+table[qt-1]+",Paper WHERE Pno='"+pno+"' AND "+table[qt-1]+".Qno = Paper.Qno;";
                    }else{
                        tmp_q.setColumnCount(1);
                        sql="SELECT Question FROM "+table[qt-1]+",Paper WHERE Pno='"+pno+"' AND "+table[qt-1]+".Qno = Paper.Qno;";
                        tmp_a.setColumnCount(1);
                        sql1="SELECT Answer FROM "+table[qt-1]+",Paper WHERE Pno='"+pno+"' AND "+table[qt-1]+".Qno = Paper.Qno;";
                    }
                    tmp_q.setRowCount(0);
                    tmp_a.setRowCount(0);
                    con.select(sql,tmp_q);
                    con.select(sql1,tmp_a);
                    //题目
                    quesAll+=(chineseNum[i]+"、"+obj[qt-1].toString()+"（"+s+"分×"+q+"题="+(s*q)+"分）\r\n");
                    for(j=0; j<tmp_q.getRowCount(); j++)
                    {
                        quesAll+=((j+1)+". "+tmp_q.getValueAt(j,0).toString()+"\r\n");
                        if(qt==1)
                            quesAll+=("A. "+tmp_q.getValueAt(j,1)+"\r\nB. "+tmp_q.getValueAt(j,2)+"\r\nC. "+tmp_q.getValueAt(j,3)+"\r\nD. "+tmp_q.getValueAt(j,4)+"\r\n");
                        quesAll+=("\r\n");
                    }
                    quesAll+=("\r\n");
                    //答案
                    ansAll+=(chineseNum[i]+"、"+obj[qt-1].toString()+"（"+s+"分×"+q+"题="+(s*q)+"分）\r\n");
                    for(j=0; j<tmp_a.getRowCount(); j++)
                    {
                        if(qt!=2){
                            ansAll+=((j+1)+". "+tmp_a.getValueAt(j,0).toString()+"\r\n");
                        }else{
                            ansAll+=((j+1)+". "+tmp_a.getValueAt(j,0).toString()+"、"+tmp_a.getValueAt(j,1).toString()+"、"+tmp_a.getValueAt(j,2).toString()+"、"
                                    +tmp_a.getValueAt(j,3).toString()+"、"+tmp_a.getValueAt(j,4).toString()+"、"+tmp_a.getValueAt(j,5).toString()+"、"
                                    +tmp_a.getValueAt(j,6).toString()+"、"+tmp_a.getValueAt(j,7).toString()+"、"+"\r\n");
                        }
                        ansAll+=("\r\n");
                    }
                }

            sql = "INSERT INTO paperfinal VALUES("+text_Pno2.getText()+",'"+text_Pname2.getText()+"','"+text_Pdate.getText()+"','"+quesAll+"','"+ansAll+"');";
            con.dataUpdate(sql);
            sql = "delete from paper where Pno="+text_Pno2.getText()+";";
            con.dataUpdate(sql);
            sql = "delete from paperinfo where Pno="+text_Pno2.getText()+";";
            con.dataUpdate(sql);
            //在面板上展示
            text_paper.append("【试题部分】------------------------------:\r\n"+quesAll);
            text_paper.append("【答案部分】------------------------------:\r\n"+ansAll);
            text_paper.setEditable(false);
            rightPanel.removeAll();
            scrollpane_table.setEnabled(false);
            rightPanel.add(scrollpane_text,BorderLayout.CENTER);
            SwingUtilities.updateComponentTreeUI(rightPanel);
            scrollpane_text.repaint();
        }
        catch(NumberFormatException nfe)
        {
            JOptionPane.showMessageDialog(this,"数据设置有误，无法生成试卷！","生成试卷",JOptionPane.OK_OPTION);
        }
        catch(Exception ex)
        {
            if(ex.getMessage().equals("题目不足"))
                JOptionPane.showMessageDialog(this,"题目数量不足，无法生成试卷！","生成试卷",JOptionPane.OK_OPTION);
            else if(ex.getMessage().equals("难度错误"))
                JOptionPane.showMessageDialog(this,"难度输入错误，无法生成试卷！","生成试卷",JOptionPane.OK_OPTION);
            else if(ex.getMessage().equals("试卷已存在"))
                JOptionPane.showMessageDialog(this,"试卷已存在！","生成试卷",JOptionPane.OK_OPTION);
            else if(ex.getMessage().equals("没有题目"))
                JOptionPane.showMessageDialog(this,"必须要有题目！","生成试卷",JOptionPane.OK_OPTION);
            else{
                JOptionPane.showMessageDialog(this,"生成试卷失败！","生成试卷",JOptionPane.OK_OPTION);
                ex.printStackTrace();
            }
        }
    }

    public void caretUpdate(CaretEvent e) {
        int s=0,q=0,i=0;
        int n = obj.length;
        int total = 0;
        try
        {
            for (i=0;i<n;i++)
            {
                if (e.getSource()==text_quantity[i]||e.getSource()==text_score[i])
                {
                    s = Integer.parseInt(text_score[i].getText());
                    q = Integer.parseInt(text_quantity[i].getText());
                    label_score[i].setText(Integer.toString(s*q));
                }
            }
            total = 0;
            for (i = 0;i<n;i++)
                total+= Integer.parseInt(label_score[i].getText());
            label_total.setText(Integer.toString(total));
        }catch (Exception ex)
        {
            label_score[i].setText("---");
            label_total.setText("---");
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

    //找出试卷编号最大的值
    public int getNextid() {
        ResultSet rs = con.getRs("select Max(Pno) from paperfinal");
        try{
            if (rs.next())
                nextid = rs.getInt(1);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return ++nextid;
    }

    // 设置背景图片
    private void updateBackImage() {
        if (backLabel != null) {
            int backw = guanliyuanUI.this.getWidth();
            int backh = this.getHeight();
            backLabel.setSize(backw, backh);
            backLabel.setText("<html><body><image width='" + backw
                    + "' height='" + (backh - 110) + "' src="
                    + guanliyuanUI.this.getClass().getResource("image/welcome.jpg")
                    + "'></img></body></html>");
        }
    }


    //"-->"
    private void jButtonSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSelectActionPerformed
        int seltIdx = jListUnselected.getSelectedIndex();
        String seltItem = (String)jListUnselected.getSelectedValue();
        ((DefaultListModel)jListSelected.getModel()).addElement(seltItem);
    }

    //"<--"
    private void jButtonUnselectActionPerformed(ActionEvent evt){
        int seltIdx = jListSelected.getSelectedIndex();
        ((DefaultListModel)jListSelected.getModel()).remove(seltIdx);
    }

    //添加导航面板的按钮
    // 1 系统帮助
    private JButton menuitem_user = new JButton("查看用户");
    //private  JButton menuitem_help = new JButton("查看帮助");
    //2 题库管理
    private JButton menuitem_ques = new JButton("题库管理");
    private JButton menuitem_kemu = new JButton("科目管理");
    private JButton menuitem_point = new JButton("添加知识点");
    private JButton menuitem_insert = new JButton("录入新题");
    private JButton menuitem_select = new JButton("查询题目");
    //3 试卷管理
    private JButton menuitem_new1 = new JButton("手动组卷");
    private JButton menuitem_new2 = new JButton("自动组卷");
    private JButton menuitem_paper = new JButton("试卷管理");
    private JButton menuitem_open = new JButton("打开试卷");
    private JButton menuitem_remove = new JButton("删除试卷");
    private JButton menuitem_save = new JButton("保存试卷");


    final JLabel backgroundLabel = new JLabel();



    //知识点选择部分;
    private javax.swing.JButton jButtonSelect;
    private javax.swing.JButton jButtonUnselect;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JList jListSelected;
    private javax.swing.JList jListUnselected;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;

}