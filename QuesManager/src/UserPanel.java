import com.sun.javaws.util.JfxHelper;
import netscape.security.UserDialogHelper;

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


public class UserPanel extends JPanel {

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

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        UserPanel user = new UserPanel();
        frame.add(user);
        frame.setSize(900,600);
        frame.setVisible(true);
    }
    //private final Dao dao = Dao.getInstance();

    /**
     * Create the panel
     */
    public UserPanel() {
        super();
        setLayout(new BorderLayout());

        final JTabbedPane tabbedPane = new JTabbedPane();
        add(tabbedPane, BorderLayout.CENTER);

        final JSplitPane cardSplitPane = new JSplitPane();
        cardSplitPane.setOneTouchExpandable(true);
        cardSplitPane.setDividerSize(12);
        cardSplitPane.setDividerLocation(244);
        tabbedPane.addTab("所有用户", null, cardSplitPane, null);


        //1.按题型浏览
        //左侧题源
        final JPanel cardTreePanel = new JPanel();
        cardSplitPane.setLeftComponent(cardTreePanel);
        cardTreePanel.setLayout(new BorderLayout());
        final JScrollPane cardTreeScrollPane = new JScrollPane();// 创建显示题
        // 题型树的滚动面板
        cardTreePanel.add(cardTreeScrollPane);// 添加到上级面板中

        cardTreeRoot = new DefaultMutableTreeNode("root");// 创建题型树的根节点

        initTree(cardTreeRoot);// 初始化名片夹树

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
        String cardListTableColumns[] = { "序号","用户名", "密码", "身份"};
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

        final JButton addUserButton = new JButton("添加用户");
        addUserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                UserNewDialog dialog = new UserNewDialog("添加新用户", "");// 创建添加名片的对话框对象
                    dialog.setVisible(true);// 设置添加名片的对话框为可见
                    initCardListTable();// 刷新名片列表
                }
        });
        cardButtonPanel.add(addUserButton);

        final JButton updCardButton = new JButton("修改信息");
        updCardButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = cardListTable.getSelectedRows();// 获得名片列表中的选中行
                if (selectedRows.length == 1) {// 仅选中了一个名片
                    String name =  cardListTable.getValueAt(
                            selectedRows[0], 1).toString();// 获得选中名片的编号
                    //System.out.println("当前修改的用户名为:"+ name);
                    UserNewDialog dialog = new UserNewDialog("添加名片", name);// 创建修改名片的对话框对象
                    dialog.setVisible(true);// 设置修改名片的对话框为可见
                    initCardListTable();
                } else {
                    if (selectedRows.length == 0) {// 未选中要修改的名片
                        JOptionPane.showMessageDialog(null, "请选择要修改的人员！",
                                "友情提示", JOptionPane.INFORMATION_MESSAGE);// 弹出提示信息
                    } else {// 选中了多个名片
                        JOptionPane.showMessageDialog(null, "一次只能修改一个人员！",
                                "友情提示", JOptionPane.INFORMATION_MESSAGE);// 弹出提示信息
                    }
                }
            }
        });
        cardButtonPanel.add(updCardButton);

        final JButton delCardButton = new JButton("删除用户");
        delCardButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = cardListTable.getSelectedRows();// 获得名片列表中的选中行
                if (selectedRows.length == 0) {// 未选中要删除的名片
                    JOptionPane.showMessageDialog(null, "请选择要删除的人员！", "友情提示",
                            JOptionPane.INFORMATION_MESSAGE);// 弹出提示信息
                } else {
                    String[] infos = new String[selectedRows.length + 1];// 组织提示信息
                    infos[0] = "确定要删除以下人员：";// 添加提示信息
                    for (int i = 0; i < selectedRows.length; i++) {// 遍历选中的名片
                        infos[i + 1] = "    " + cardListTable.getValueAt(selectedRows[i], 1);// 获得名片编号
                    }
                    int i = JOptionPane.showConfirmDialog(null, infos, "友情提示",
                            JOptionPane.YES_NO_OPTION);// 弹出提示信息
                    if (i == 0) {// 确定删除
                        for (int j = 0; j < selectedRows.length; j++) {// 遍历选中的名片
                            String name = cardListTable.getValueAt(
                                    selectedRows[j], 1).toString();// 获得名片编号
                            String sql = "delete from User where UserName = '"+name+"';";
                            try{
                                con.dataUpdate(sql);
                            }catch (SQLException ex){
                                ex.printStackTrace();
                            }
                        }
                        initCardListTable();// 刷新名片列表
                    }
                }
            }
        });
        cardButtonPanel.add(delCardButton);


    }

    private void initTree(DefaultMutableTreeNode treeRoot) {// 初始化树的方法
        DefaultMutableTreeNode guanliNode = new DefaultMutableTreeNode("超级管理员");
        treeRoot.add(guanliNode);
        DefaultMutableTreeNode tikuNode = new DefaultMutableTreeNode("题库管理员");
        treeRoot.add(tikuNode);
        DefaultMutableTreeNode zujuanNode = new DefaultMutableTreeNode("组卷管理员");
        treeRoot.add(zujuanNode);
    }

    //按身份浏览时，获取表格
    private void initCardListTable() {

        cardListTableValueV.removeAllElements();// 清空名片列表
        DefaultMutableTreeNode cardTreeNode = (DefaultMutableTreeNode) cardTree.getLastSelectedPathComponent();// 获得名片夹树的选中节点对象
        if (cardTreeNode != null) {// 判断是否存在选中的节点
            String cardName = convertToEnglish(cardTreeNode.getUserObject().toString());// 获得选中名片夹的名称
            String sql = "select userName,userPwd,role from User where role='"+cardName+"';";
            cardListTableValueV.addAll(con.selectSomeNote(sql));// 检索名片夹包含的名片
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
        String []typeChinese = {"超级管理员","题库管理员","组卷管理员"};
        String []typeEnglish = {"guanliyuan","tikuyuan","zujuanyuan"};
        switch (typeName){
            case "超级管理员":
                tmp = "guanliyuan";
                break;
            case "题库管理员":
                tmp = "tikuyuan";
                break;
            case "组卷管理员":
                tmp = "zujuanyuan";
                break;
            default:
                break;
        }
        return tmp;
    }

}
