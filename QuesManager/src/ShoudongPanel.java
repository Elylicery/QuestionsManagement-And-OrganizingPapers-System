import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class ShoudongPanel extends JPanel {

    private Dao con = new Dao();

    private MTable selectedListTable;

    private Vector<String> selectedListTableColumnV;

    private Vector<Vector> selectedListTableValueV;

    private DefaultTableModel selectedListTableModel;

    public static void main() {
       JFrame frame = new JFrame();
       ShoudongPanel panel = new ShoudongPanel();
       frame.add(panel);
       frame.setVisible(true);
    }

    /**
     * Create the frame
     */
    public ShoudongPanel() {
        super();
        setLayout(new BorderLayout());
        //垂直分割面板
        final JSplitPane workaroundSplitPane = new JSplitPane();// 创建分割面板对象
        workaroundSplitPane.setDividerSize(12);// 设置分割条的宽度
        workaroundSplitPane.setOneTouchExpandable(true);// 设置为支持快速展开/折叠分割条
        workaroundSplitPane.setDividerLocation(330);// 设置面版默认的分割位置
        workaroundSplitPane.setPreferredSize(new Dimension(0, 590));// 设置分割面板的首选高度
        workaroundSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);// 设置为垂直分割
        this.add(workaroundSplitPane);

        //水平分割面板
        final JSplitPane sendSplitPane = new JSplitPane();
        sendSplitPane.setOneTouchExpandable(true);// 设置为支持快速展开/折叠分割条
        sendSplitPane.setDividerSize(12);// 设置分割条的宽度
        sendSplitPane.setDividerLocation(244);// 设置面版默认的分割位置
        sendSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);// 设置为水平分割
        workaroundSplitPane.setLeftComponent(sendSplitPane);

        //左上 已选题目列表
        final JPanel selectedListPanel = new JPanel();
        selectedListPanel.setBorder(new TitledBorder(null, "已选题目列表",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, null, null));
        selectedListPanel.setLayout(new BorderLayout());
        sendSplitPane.setLeftComponent(selectedListPanel);

        final JScrollPane scrollPane = new JScrollPane();
        selectedListPanel.add(scrollPane, BorderLayout.CENTER);

        selectedListTableColumnV = new Vector<String>();
        selectedListTableColumnV.add("序号");
        selectedListTableColumnV.add("题号");
        selectedListTableColumnV.add("题目");

        selectedListTableValueV = new Vector<Vector>();

        selectedListTableModel = new DefaultTableModel(selectedListTableValueV,
                selectedListTableColumnV);

        selectedListTable = new MTable(selectedListTableModel);
        scrollPane.setViewportView(selectedListTable);

        //左上 下部按键
        final JPanel buttonPanel = new JPanel();
        final FlowLayout flowLayout_1 = new FlowLayout();
        flowLayout_1.setVgap(0);
        flowLayout_1.setHgap(0);
        buttonPanel.setLayout(flowLayout_1);
        selectedListPanel.add(buttonPanel, BorderLayout.SOUTH);

        final JButton cancelButton = new JButton("取消选择");
        buttonPanel.add(cancelButton);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = selectedListTable.getSelectedRows();
                if (selectedRows.length == 0) {
                    JOptionPane.showMessageDialog(null, "请选择要取消选择的题目！", "友情提示",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    String[] infos = new String[selectedRows.length + 1];
                    infos[0] = "确定要取消以下题目：";
                    for (int i = 0; i < selectedRows.length; i++) {
                        infos[i + 1] = "    "
                                + selectedListTable.getValueAt(selectedRows[i], 1)
                                + "  "
                                + selectedListTable.getValueAt(selectedRows[i], 2);
                    }
                    int i = JOptionPane.showConfirmDialog(null, infos, "友情提示",
                            JOptionPane.YES_NO_OPTION);
                    if (i == 0) {
                        for (int j = selectedRows.length - 1; j >= 0; j--) {
                            selectedListTableModel.removeRow(selectedRows[j]);
                        }
                        for (int row = selectedRows[0]; row < selectedListTable.getRowCount(); row++) {
                            selectedListTable.setValueAt(row + 1, row, 0);
                        }
                    }
                }
            }
        });

        final JButton clearButton = new JButton("清空");
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int i = JOptionPane.showConfirmDialog(null, "确定要清空已选题目列表？",
                        "友情提示", JOptionPane.YES_NO_OPTION);
                if (i == 0) {
                    selectedListTableValueV.removeAllElements();
                    selectedListTableModel.setDataVector(selectedListTableValueV,
                            selectedListTableColumnV);
                }
            }
        });
        buttonPanel.add(clearButton);

        //右上：试卷信息面板
        final InfoPanel infoPanel = new InfoPanel(selectedListTable);
        sendSplitPane.setRightComponent(infoPanel);


        final JButton reviewButton = new JButton("预览试卷");
        reviewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String quesAll="";
                String ansAll="";
                String sql="";
                DefaultTableModel tmp_pi;//存放问题和答案
                tmp_pi = new DefaultTableModel();
                Object []obj={"选择题","填空题","判断题","名词解释","综合题","论述题"};
                String []table={"selection","blank","judge","explanation","comprehensive","discussion"};
                String ques[] = {"一、选择题\r\n", "二、填空题\r\n", "三、判断题\r\n", "四、名词解释题\r\n", "五、综合题\r\n", "六、论述题\r\n",};
                String ans[] = {"一、选择题答案\r\n", "二、填空题答案\r\n", "三、判断题答案\r\n", "四、名词解释题答案\r\n", "五、综合题答案\r\n", "六、论述题\r\n",};
                int RowCount =  selectedListTable.getRowCount();//找到选中题目中一共有多少行
                for (int row = 0;row<RowCount;row++) {
                    String Qno = selectedListTableModel.getValueAt(row, 1).toString();// 获得题目的编号
                    int Qtype = Qno.charAt(0) - '0';
                    try {
                        ResultSet rs;
                        int quesno[] = {0, 0, 0, 0, 0, 0};
                        if (Qtype == 1) {
                            sql = "SELECT * FROM " + table[Qtype - 1] + " WHERE Qno = '" + Qno + "';";
                            rs = con.getRs(sql);
                            while (rs.next()) {
                                quesno[Qtype - 1] += 1;
                                ques[Qtype - 1] += ((quesno[Qtype - 1]) + "." + rs.getString("Question") + "\r\n");
                                ques[Qtype - 1] += ("A." + rs.getString("ItemA") + "\r\n" + "B." + rs.getString("ItemB") + "\r\n" + "C." + rs.getString("ItemC") + "\r\n" + "D." + rs.getString("ItemD") + "\r\n");
                                ques[Qtype - 1] += ("\r\n");
                                ans[Qtype - 1] += ((quesno[Qtype - 1]) + "." + rs.getString("Answer") + "\r\n");
                            }
                        } else if (Qtype == 2) {
                            sql = "SELECT * FROM " + table[Qtype - 1] + " WHERE Qno = '" + Qno + "';";
                            rs = con.getRs(sql);
                            while (rs.next()) {
                                quesno[Qtype - 1] += 1;
                                ques[Qtype - 1] += ((quesno[Qtype - 1]) + "." + rs.getString("Question") + "\r\n");
                                ques[Qtype - 1] += ("\r\n");
                                ans[Qtype - 1] += ((quesno[Qtype - 1]) + "." + rs.getString("Answer1") + "、" + rs.getString("Answer2") + "、" + rs.getString("Answer3") + "、" + rs.getString("Answer4") + "、"
                                        + rs.getString("Answer5") + "、" + rs.getString("Answer6") + "、" + rs.getString("Answer7") + "、" + rs.getString("Answer8") + "、" + "\r\n");
                            }
                        } else {
                            sql = "SELECT * FROM " + table[Qtype - 1] + " WHERE Qno = '" + Qno + "';";
                            rs = con.getRs(sql);
                            while (rs.next()) {
                                quesno[Qtype - 1] += 1;
                                ques[Qtype - 1] += ((quesno[Qtype - 1]) + "." + rs.getString("Question") + "\r\n");
                                ques[Qtype - 1] += ("\r\n");
                                ans[Qtype - 1] += ((quesno[Qtype - 1]) + "." + rs.getString("Answer") + "\r\n");
                            }
                        }
                    }catch(Exception ex) {
                        ex.printStackTrace();
                    }
                }

                for (int i = 0; i < ques.length; i++) {
                    quesAll += ques[i];
                }
                for (int i = 0; i < ans.length; i++) {
                    ansAll += ans[i];
                }
                System.out.println(quesAll);
                System.out.println(ansAll);
                infoPanel.getInfoTextArea().setText(quesAll);
                infoPanel.getEmailTextArea().setText(ansAll);
            }
        });
        buttonPanel.add(reviewButton);


        //下部总：题源管理
        final ExplorerPanel explorerPanel = new ExplorerPanel(
                selectedListTableModel, infoPanel.getTabbedPane(), infoPanel
                .getInfoTextArea(), infoPanel.getEmailTextArea());
        workaroundSplitPane.setRightComponent(explorerPanel);

    }

//    private class HandsetButtonActionListener implements ActionListener {
//        public void actionPerformed(ActionEvent e) {
//            LetterSetDialog dialog = new LetterSetDialog();
//            dialog.setVisible(true);
//        }
//    }
//
//    private class EmailButtonActionListener implements ActionListener {
//        public void actionPerformed(ActionEvent e) {
//            MailSetDialog dialog = new MailSetDialog();
//            dialog.setVisible(true);
//        }
    }
