import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;


public class ExplorerPanel extends JPanel {

    private Dao con = new Dao();

    //按题型浏览
    private MTable cardListTable;

    private Vector<String> cardListTableColumnV;

    private Vector<Vector> cardListTableValueV;

    private DefaultTableModel cardListTableModel;

    private JTree cardTree;

    private DefaultMutableTreeNode cardTreeRoot;

    private DefaultTreeModel cardTreeModel;

    //按知识点浏览
    private MTable infoListTable;

    private Vector<String> infoListTableColumnV;

    private Vector<Vector> infoListTableValueV;

    private DefaultTableModel infoListTableModel;

    private JTree infoTree;

    private DefaultMutableTreeNode infoTreeRoot;

    private DefaultTreeModel infoTreeModel;


    //private final Dao dao = Dao.getInstance();

    /**
     * Create the panel
     */
    public ExplorerPanel(final DefaultTableModel selectedListTableModel,
                         final JTabbedPane infoTabbedPane, final JTextArea infoTextArea,
                         final JTextArea emailTextArea) {
        super();
        setLayout(new BorderLayout());

        final JTabbedPane tabbedPane = new JTabbedPane();
        add(tabbedPane, BorderLayout.CENTER);

        final JSplitPane cardSplitPane = new JSplitPane();
        cardSplitPane.setOneTouchExpandable(true);
        cardSplitPane.setDividerSize(12);
        cardSplitPane.setDividerLocation(244);
        tabbedPane.addTab("按题型浏览", null, cardSplitPane, null);


        //1.按题型浏览
        //左侧题源
        final JPanel cardTreePanel = new JPanel();
        cardSplitPane.setLeftComponent(cardTreePanel);
        cardTreePanel.setLayout(new BorderLayout());
        final JScrollPane cardTreeScrollPane = new JScrollPane();// 创建显示题
        // 题型树的滚动面板
        cardTreePanel.add(cardTreeScrollPane);// 添加到上级面板中

        cardTreeRoot = new DefaultMutableTreeNode("root");// 创建题型树的根节点

        initTree(cardTreeRoot, "card");// 初始化名片夹树

        cardTreeModel = new DefaultTreeModel(cardTreeRoot);// 创建题型树模型

        cardTree = new JTree(cardTreeModel);// 创建题型树树
        cardTree.setRootVisible(false);// 设置题型树的根节点不可见
        System.out.println(cardTree.getSelectionModel().getSelectionMode());
        cardTree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);// 设置题型树的选择模式为单选
        if (cardTreeRoot.getChildCount() > 0)
            cardTree.setSelectionRow(0);// 如果题型树存在子节点，则设置选中第一个子节点
        cardTree.addTreeSelectionListener(new TreeSelectionListener() {// 为题型树添加接点选中事件监听器
            public void valueChanged(TreeSelectionEvent e) {
                initCardListTable();// 初始化题型列表
            }
        });
        cardTreeScrollPane.setViewportView(cardTree);// 将题型树添加到滚动面板中

        //右侧题目信息
        final JPanel cardListPanel = new JPanel();
        cardSplitPane.setRightComponent(cardListPanel);
        cardListPanel.setLayout(new BorderLayout());

        final JScrollPane cardListScrollPane = new JScrollPane();
        cardListPanel.add(cardListScrollPane);

        cardListTableColumnV = new Vector<String>();
        String cardListTableColumns[] = { "序号","题号", "题目", "知识点","难度", "出题次数"};
        for (int i = 0; i < cardListTableColumns.length; i++) {
            cardListTableColumnV.add(cardListTableColumns[i]);
        }

        cardListTableValueV = new Vector<Vector>();

        cardListTableModel = new DefaultTableModel(cardListTableValueV, cardListTableColumnV);

        cardListTable = new MTable(cardListTableModel);
        initCardListTable();
        cardListScrollPane.setViewportView(cardListTable);

        //右侧按键面板
        final JPanel cardButtonPanel = new JPanel();
        cardButtonPanel.setLayout(new BoxLayout(cardButtonPanel,
                BoxLayout.Y_AXIS));
        cardListPanel.add(cardButtonPanel, BorderLayout.EAST);

        final JButton selAllButton = new JButton("全选");
        selAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardListTable.selectAll();// 选中表格中的所有行
            }
        });
        cardButtonPanel.add(selAllButton);

        final JButton addToSendListButton = new JButton("加入列表");
        addToSendListButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int rowCount = selectedListTableModel.getRowCount();// 获得试题列表的试题个数
                int[] selectedRows = cardListTable.getSelectedRows();// 获得题型列表中的选中行
                int index = rowCount + 1;// 初始化选中试题列表的序号
                for (int selectedRow = 0; selectedRow < selectedRows.length; selectedRow++) {// 遍历选中行
                    int newNum = (Integer) cardListTable.getValueAt(
                            selectedRows[selectedRow], 1);// 获得题目编号
                    boolean had = false;// 默认为未加入题目列表
                    for (int row = 0; row < rowCount; row++) {// 遍历选中题目列表
                        int nowNum = (Integer) selectedListTableModel.getValueAt(row, 1);// 获得题目的编号
                        if (newNum == nowNum) {// 判断题目编号和已选中试题编号是否相同
                            had = true;// 已经加入收信人列表
                            break;// 跳出循环
                        }
                    }
                    if (!had) {// 未加入已选中试题列表
                        Vector rowV = new Vector();// 创建一个代表已选中试题列表的向量
                        rowV.add(index++);// 添加序号
                        rowV.add(newNum);// 添加编号
                        rowV.add(cardListTable.getValueAt(
                                selectedRows[selectedRow], 2));// 添加题目内容
                        selectedListTableModel.addRow(rowV);// 加入已选中试题列表
                    }
                }
                cardListTable.clearSelection();// 取消名片列表中的选中行
            }
        });
        cardButtonPanel.add(addToSendListButton);

        //2.按知识点浏览
        final JSplitPane infoSplitPane = new JSplitPane();
        infoSplitPane.setOneTouchExpandable(true);
        infoSplitPane.setDividerSize(12);
        infoSplitPane.setDividerLocation(244);
        tabbedPane.addTab("按知识点浏览", null, infoSplitPane, null);

        final JPanel infoTreePanel = new JPanel();
        infoSplitPane.setLeftComponent(infoTreePanel);
        infoTreePanel.setLayout(new BorderLayout());

        final JScrollPane infoTreeScrollPane = new JScrollPane();
        infoTreePanel.add(infoTreeScrollPane);

        infoTreeRoot = new DefaultMutableTreeNode("root");
        initTree(infoTreeRoot, "info");

        infoTreeModel = new DefaultTreeModel(infoTreeRoot);

        infoTree = new JTree(infoTreeModel);
        infoTree.setRootVisible(false);
        if (infoTreeRoot.getChildCount() > 0)
            infoTree.setSelectionRow(0);
        infoTree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                initInfoListTable();
            }
        });
        infoTreeScrollPane.setViewportView(infoTree);

       //右侧题目内容(按知识点浏览)
        final JPanel infoListPanel = new JPanel();
        infoSplitPane.setRightComponent(infoListPanel);
        infoListPanel.setLayout(new BorderLayout());

        final JScrollPane infoListScrollPane = new JScrollPane();
        infoListPanel.add(infoListScrollPane);

        infoListTableColumnV = new Vector<String>();
        String infoListtableColumns[]={ "序号","题号","题目","难度", "出题次数"};
        for (int i = 0; i < infoListtableColumns.length; i++) {
            infoListTableColumnV.add(infoListtableColumns[i]);
        }

        infoListTableValueV = new Vector<Vector>();

        infoListTableModel = new DefaultTableModel(infoListTableValueV, infoListTableColumnV);

        infoListTable = new MTable(infoListTableModel);
        initInfoListTable();
        infoListScrollPane.setViewportView(infoListTable);


        //右侧按钮面板
        final JPanel infoButtonPanel = new JPanel();
        infoButtonPanel.setLayout(new BoxLayout(infoButtonPanel,
                BoxLayout.Y_AXIS));
        infoListPanel.add(infoButtonPanel, BorderLayout.EAST);


        final JButton selAllButton2 = new JButton("全选");
        selAllButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                infoListTable.selectAll();// 选中表格中的所有行
            }
        });
        infoButtonPanel.add(selAllButton2);

        final JButton addToSendListButton2 = new JButton("加入列表");
        addToSendListButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int rowCount = selectedListTableModel.getRowCount();// 获得试题列表的试题个数
                int[] selectedRows =infoListTable.getSelectedRows();// 获得题型列表中的选中行
                int index = rowCount + 1;// 初始化选中试题列表的序号
                for (int selectedRow = 0; selectedRow < selectedRows.length; selectedRow++) {// 遍历选中行
                    int newNum = (Integer) infoListTable.getValueAt(selectedRows[selectedRow], 1);// 获得题目编号
                    boolean had = false;// 默认为未加入题目列表
                    for (int row = 0; row < rowCount; row++) {// 遍历选中题目列表
                        int nowNum = (Integer)selectedListTableModel.getValueAt(row, 1);// 获得题目的编号
                        if (newNum == nowNum) {// 判断题目编号和已选中试题编号是否相同
                            had = true;// 已经加入已选择试题列表
                            break;// 跳出循环
                        }
                    }
                    if (!had) {// 未加入已选中试题列表
                        Vector rowV = new Vector();// 创建一个代表已选中试题列表的向量
                        rowV.add(index++);// 添加序号
                        rowV.add(newNum);// 添加编号
                        rowV.add(infoListTable.getValueAt(
                                selectedRows[selectedRow], 2));// 添加题目内容
                        selectedListTableModel.addRow(rowV);// 加入已选中试题列表
                    }
                }
                infoListTable.clearSelection();// 取消名片列表中的选中行
            }
        });
        infoButtonPanel.add(addToSendListButton2);

    }

    private void initTree(DefaultMutableTreeNode treeRoot, String used) {// 初始化树的方法
        if (used.equals("info")){
            //当按照知识点浏览试题的时候
            DefaultMutableTreeNode kemuNode,pointNode;
            String sql = "select * from Kemu order by Kno";
            ResultSet rs1= con.getRs(sql);
            try {
                while (rs1.next()) {
                    String Kname = rs1.getString("Kname");
                    kemuNode = new DefaultMutableTreeNode("[科目]:"+Kname);
                    treeRoot.add(kemuNode);
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
        }else {
            //当按照题型浏览试题的时候
            DefaultMutableTreeNode kemuNode,typeNode;
            String sql = "select * from Kemu order by Kno";
            ResultSet rs= con.getRs(sql);
            try {
                while (rs.next()) {
                    String Kname = rs.getString("Kname");
                    kemuNode = new DefaultMutableTreeNode("[科目]:"+Kname);
                    treeRoot.add(kemuNode);
                    String []typeNodeContent = {"选择题","填空题","判断题","名词解释","综合题","论述题"};
                    for (int i=0;i<typeNodeContent.length;i++){
                        kemuNode.add(new DefaultMutableTreeNode(typeNodeContent[i]));
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(zhishidianUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    //按题型浏览时，获取表格
    private void initCardListTable() {
        cardListTableValueV.removeAllElements();// 清空列表
        DefaultMutableTreeNode cardTreeNode = (DefaultMutableTreeNode) cardTree.getLastSelectedPathComponent();// 获得题型夹树的选中节点对象
        if (cardTreeNode != null) {// 判断是否存在选中的节点
            String kemuName = cardTreeNode.getParent().toString().replace("[科目]:","");//获得选中的科目
            String typeName = convertToEnglish(cardTreeNode.getUserObject().toString());//获得选中的题型
            if (kemuName!="root" && typeName!=""){
                String sql = "select Qno,Question,Point,Difficulty,Times from "+typeName+" where Kemu ='"+kemuName+"';";
                System.out.println(sql);
                cardListTableValueV.addAll(con.selectSomeNote(sql));//检索在所在科目和题型下的题目
            }
        }
        cardListTableModel.setDataVector(cardListTableValueV,
                cardListTableColumnV);// 刷新名片列表表格模型
    }

    //按知识点浏览时，获取知识点
    private String []typeTmp={"selection","blank","judge","explanation","comprehensive","discussion"};
    private void initInfoListTable() {
        infoListTableValueV.removeAllElements();
        DefaultMutableTreeNode infoTreeNode = (DefaultMutableTreeNode) infoTree.getLastSelectedPathComponent();
        if (infoTreeNode != null) {//判断是否存在选中的节点
            String kemuName = infoTreeNode.getParent().toString().replace("[科目]:","");//获得选中的科目
            String pointName = infoTreeNode.getUserObject().toString();//获得选中的知识点
            if (kemuName!="root" && pointName!=""){
                for (int i=0;i<typeTmp.length;i++){
                    String sql = "select Qno,Question,Difficulty,Times from "+typeTmp[i]+" where Kemu ='"+kemuName+"'AND Point ='"+pointName+"';";
                    System.out.println(sql);
                    infoListTableValueV.addAll(con.selectSomeNote(sql));//检索在所在科目和题型下的题目
                }
            }
        }
        infoListTableModel.setDataVector(infoListTableValueV, infoListTableColumnV);
    }


    String convertToEnglish(String typeName){
        String tmp="";
        String []typeChinese = {"选择题","填空题","判断题","名词解释","综合题","论述题"};
        String []typeEnglish = {"selection","blank","judge","explanation","comprehensive","discussion"};
        switch (typeName){
            case "选择题":
                tmp = "selection";
                break;
            case "填空题":
                tmp = "blank";
                break;
            case "判断题":
                tmp = "judge";
                break;
            case "名词解释":
                tmp = "explanation";
                break;
            case "综合题":
                tmp = "comprehensive";
                break;
            case "论述题":
                tmp = "discussion";
                break;
            default:
                break;

        }
        return tmp;
    }

}
