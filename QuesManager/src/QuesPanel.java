import jdk.nashorn.internal.runtime.regexp.joni.ast.QuantifierNode;

import java.awt.*;
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


public class QuesPanel extends JPanel {

    private Dao con = new Dao();

    //按题型浏览
    private MTable cardListTable;

    private Vector<String> cardListTableColumnV;

    private Vector<Vector> cardListTableValueV;

    private DefaultTableModel cardListTableModel;

    private JTree cardTree;

    private DefaultMutableTreeNode cardTreeRoot;

    private DefaultTreeModel cardTreeModel;

    private String []table={"selection","blank","judge","explanation","comprehensive","discussion"};

    //按知识点浏览
    private MTable infoListTable;

    private Vector<String> infoListTableColumnV;

    private Vector<Vector> infoListTableValueV;

    private DefaultTableModel infoListTableModel;

    private JTree infoTree;

    private DefaultMutableTreeNode infoTreeRoot;

    private DefaultTreeModel infoTreeModel;


    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setSize(new Dimension(900,600));
        QuesPanel panel = new QuesPanel();
        frame.add(panel);
        frame.setVisible(true);
    }

    /**
     * Create the panel
     */
    public QuesPanel() {
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

        //按题型修改
        final JButton updCardButton2 = new JButton("修改题目");
        updCardButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = cardListTable.getSelectedRows();// 获得名片列表中的选中行
                if (selectedRows.length == 1) {// 仅选中了一个名片
                    String Qno =  cardListTable.getValueAt(
                            selectedRows[0], 1).toString();// 获得选中名片的编号
                    String kemu="";
                    int type = Qno.charAt(0)-'1';
                    String sql = "select * from "+table[type]+" where Qno='" + Qno + "';";
                    ResultSet rs1 = con.getRs(sql);
                    try {
                        while (rs1.next()) {
                            kemu = rs1.getString("kemu");
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    //对于不同的题目类型弹出不同的修改框
                    if (type==0){
                        QSelectionDialog dialog = new QSelectionDialog("修改选择题",kemu,Qno);
                        dialog.setVisible(true);
                    }else if (type ==1){
                        QBlankDialog dialog = new QBlankDialog("修改填空题",kemu,Qno);
                        dialog.setVisible(true);
                    }else if (type ==2){
                        QJudgeDialog dialog = new QJudgeDialog("修改判断题",kemu,Qno);
                        dialog.setVisible(true);
                    }else if (type == 3){
                        QExplainationDialog dialog = new QExplainationDialog("修改名词解释题",kemu,Qno);
                        dialog.setVisible(true);
                    }else if (type == 4){
                        QCompreDialog dialog = new QCompreDialog("修改综合题",kemu,Qno);
                        dialog.setVisible(true);
                    }else if (type == 5){
                        QDiscussDialog dialog = new QDiscussDialog("修改论述题",kemu,Qno);
                        dialog.setVisible(true);
                    }
                    initCardListTable();
                } else {
                    if (selectedRows.length == 0) {// 未选中要修改的名片
                        JOptionPane.showMessageDialog(null, "请选择要修改的题目！",
                                "友情提示", JOptionPane.INFORMATION_MESSAGE);// 弹出提示信息
                    } else {// 选中了多个名片
                        JOptionPane.showMessageDialog(null, "一次只能修改一个题目！",
                                "友情提示", JOptionPane.INFORMATION_MESSAGE);// 弹出提示信息
                    }
                }
            }
        });
        cardButtonPanel.add(updCardButton2);

        final JButton delCardButton2 = new JButton("删除题目");
        delCardButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = cardListTable.getSelectedRows();// 获得题目列表中的选中行
                if (selectedRows.length == 0) {// 未选中要删除的题目
                    JOptionPane.showMessageDialog(null, "请选择要删除的题目！", "友情提示", JOptionPane.INFORMATION_MESSAGE);// 弹出提示信息
                } else {
                    String[] infos = new String[selectedRows.length + 1];// 组织提示信息
                    infos[0] = "确定要删除以下题目：";// 添加提示信息
                    for (int i = 0; i < selectedRows.length; i++) {// 遍历选中的题目
                        infos[i + 1] = "    " + cardListTable.getValueAt(selectedRows[i], 1)+"  "+cardListTable.getValueAt(selectedRows[i],2);// 获得题目编号
                    }
                    int i = JOptionPane.showConfirmDialog(null, infos, "友情提示", JOptionPane.YES_NO_OPTION);// 弹出提示信息
                    if (i == 0) {// 确定删除
                        for (int j = 0; j < selectedRows.length; j++) {// 遍历选中的名片
                            String Qno = cardListTable.getValueAt(
                                    selectedRows[j], 1).toString();// 获得名片编号
                            int type = Qno.charAt(0)-'1';
                            String []table={"selection","blank","judge","explanation","comprehensive","discussion"};
                            String sql = "delete from "+table[i]+" where Qno ='"+Qno+"';";
                            try{
                                con.dataUpdate(sql);
                            }catch (SQLException ex){
                                ex.printStackTrace();
                            }
                        }
                        initCardListTable();// 刷新题目列表
                    }
                }
            }
        });
        cardButtonPanel.add(delCardButton2);

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


        //按知识点浏览
        final JButton updCardButton = new JButton("修改题目");
        updCardButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = infoListTable.getSelectedRows();// 获得名片列表中的选中行
                if (selectedRows.length == 1) {// 仅选中了一个名片
                    String Qno =  infoListTable.getValueAt(
                            selectedRows[0], 1).toString();// 获得选中名片的编号
                    String kemu="";
                    int type = Qno.charAt(0)-'1';
                    String sql = "select * from "+table[type]+" where Qno='" + Qno + "';";
                    ResultSet rs1 = con.getRs(sql);
                    try {
                        while (rs1.next()) {
                            kemu = rs1.getString("kemu");
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    //对于不同的题目类型弹出不同的修改框
                    if (type==0){
                        QSelectionDialog dialog = new QSelectionDialog("修改选择题",kemu,Qno);
                        dialog.setVisible(true);
                    }else if (type ==1){
                        QBlankDialog dialog = new QBlankDialog("修改填空题",kemu,Qno);
                        dialog.setVisible(true);
                    }else if (type ==2){
                        QJudgeDialog dialog = new QJudgeDialog("修改判断题",kemu,Qno);
                        dialog.setVisible(true);
                    }else if (type == 3){
                        QExplainationDialog dialog = new QExplainationDialog("修改名词解释题",kemu,Qno);
                        dialog.setVisible(true);
                    }else if (type == 4){
                        QCompreDialog dialog = new QCompreDialog("修改综合题",kemu,Qno);
                        dialog.setVisible(true);
                    }else if (type == 5){
                        QDiscussDialog dialog = new QDiscussDialog("修改论述题",kemu,Qno);
                        dialog.setVisible(true);
                    }
                    initCardListTable();
                } else {
                    if (selectedRows.length == 0) {// 未选中要修改的名片
                        JOptionPane.showMessageDialog(null, "请选择要修改的题目！",
                                "友情提示", JOptionPane.INFORMATION_MESSAGE);// 弹出提示信息
                    } else {// 选中了多个名片
                        JOptionPane.showMessageDialog(null, "一次只能修改一个题目！",
                                "友情提示", JOptionPane.INFORMATION_MESSAGE);// 弹出提示信息
                    }
                }
            }
        });
        infoButtonPanel.add(updCardButton);

        final JButton delCardButton = new JButton("删除题目");
        delCardButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = infoListTable.getSelectedRows();// 获得题目列表中的选中行
                if (selectedRows.length == 0) {// 未选中要删除的题目
                    JOptionPane.showMessageDialog(null, "请选择要删除的题目！", "友情提示", JOptionPane.INFORMATION_MESSAGE);// 弹出提示信息
                } else {
                    String[] infos = new String[selectedRows.length + 1];// 组织提示信息
                    infos[0] = "确定要删除以下题目：";// 添加提示信息
                    for (int i = 0; i < selectedRows.length; i++) {// 遍历选中的题目
                        infos[i + 1] = "    " + infoListTable.getValueAt(selectedRows[i], 1)+"  "+infoListTable.getValueAt(selectedRows[i],2);// 获得题目编号
                    }
                    int i = JOptionPane.showConfirmDialog(null, infos, "友情提示", JOptionPane.YES_NO_OPTION);// 弹出提示信息
                    if (i == 0) {// 确定删除
                        for (int j = 0; j < selectedRows.length; j++) {// 遍历选中的名片
                            String Qno = infoListTable.getValueAt(
                                    selectedRows[j], 1).toString();// 获得名片编号
                            int type = Qno.charAt(0)-'1';
                            String []table={"selection","blank","judge","explanation","comprehensive","discussion"};
                            String sql = "delete from "+table[i]+" where Qno ='"+Qno+"';";
                            try{
                                con.dataUpdate(sql);
                            }catch (SQLException ex){
                                ex.printStackTrace();
                            }
                        }
                        initCardListTable();// 刷新题目列表
                    }
                }
            }
        });
        infoButtonPanel.add(delCardButton);

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
                    //System.out.println(sql);
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
