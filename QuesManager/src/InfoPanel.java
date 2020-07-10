import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class InfoPanel extends JPanel {

    private int nextid = 0;
    private int Pno;

    private JTabbedPane tabbedPane;

    private JTextArea emailTextArea;

    private JTextArea infoTextArea;

    private MTable seletedListTable;

    private Dao con = new Dao();


    private String []table={"selection","blank","judge","explanation","comprehensive","discussion"};



    public InfoPanel(MTable seletedListTable) {
        super();
        this.seletedListTable = seletedListTable;
        setBorder(new TitledBorder("试卷信息"));
        setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();
        add(tabbedPane);

        //试卷信息部分
        //左部分：面板切换，查看题目和答案
        //试题部分内容
        final JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BorderLayout());
        tabbedPane.addTab("题目内容", null, infoPanel, null);

        final JScrollPane infoScrollPane = new JScrollPane();
        infoPanel.add(infoScrollPane, BorderLayout.CENTER);

        infoTextArea = new JTextArea();
        infoTextArea.setLineWrap(true);
        infoScrollPane.setViewportView(infoTextArea);

        //答案部分内容
        final JPanel emailPanel = new JPanel();
        emailPanel.setLayout(new BorderLayout());
        tabbedPane.addTab("答案内容", null, emailPanel, null);

        final JScrollPane emailScrollPane = new JScrollPane();
        emailPanel.add(emailScrollPane, BorderLayout.CENTER);
        emailTextArea = new JTextArea();
        emailTextArea.setLineWrap(true);
        emailScrollPane.setViewportView(emailTextArea);


        //右边：设置试卷信息
        final JPanel ButtonPanel = new JPanel();
        ButtonPanel.setLayout(new BoxLayout(ButtonPanel, BoxLayout.Y_AXIS));
        infoPanel.add(ButtonPanel, BorderLayout.EAST);

        ButtonPanel.add(new JLabel("当前试卷编号："));
        Pno = getNextid();
        JTextField text_Pno=new JTextField(Pno+"");
        text_Pno.setEditable(false);
        ButtonPanel.add(text_Pno);
        //1.输入试题名称
        ButtonPanel.add(new JLabel("试题名称:  "));
        JTextField text_Pname = new JTextField(12);
        ButtonPanel.add(text_Pname);
        //2.选择时间
        ButtonPanel.add(new JLabel("试题生成时间: "));
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date_tmp = format.format(date); //2013-01-14
        JTextField text_Pdate = new JTextField(date_tmp);
        text_Pdate.setEditable(false);
        ButtonPanel.add(text_Pdate);

        final JButton addfinishButton = new JButton("组卷完成");
        addfinishButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //更新题目次数
                int n = seletedListTable.getRowCount();
                for (int i=0;i<n;i++){
                    String tempQno = seletedListTable.getValueAt(i,1).toString();
                    int type = tempQno.charAt(0)-'1';
                    String sql = "UPDATE "+table[type]+" SET Times=(Times+1) WHERE Qno="+tempQno+";";
                    try{
                        con.dataUpdate(sql);
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
                //将试卷信息入库
                String quesAll = infoTextArea.getText();
                String ansAll = emailTextArea.getText();
                String sql;
                try {
                    //插入到paperfinal中
                    sql = "INSERT INTO paperfinal VALUES("+Pno+",'"+text_Pname.getText()+"','"+text_Pdate.getText()+"','"+quesAll+"','"+ansAll+"');";
                    con.dataUpdate(sql);
                    JOptionPane.showMessageDialog(null,"添加新试卷成功！","提示",JOptionPane.INFORMATION_MESSAGE);
                }catch(Exception ex)
                {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null,"添加新试卷失败！","提示",JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        ButtonPanel.add(addfinishButton);

        final JButton addfinishButton2 = new JButton("组卷完成并另存为");
        addfinishButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String quesAll = infoTextArea.getText();
                String ansAll = emailTextArea.getText();
                String sql;
                //更新题目次数
                int n = seletedListTable.getRowCount();
                for (int i=0;i<n;i++){
                    String tempQno = seletedListTable.getValueAt(i,1).toString();
                    int type = tempQno.charAt(0)-'1';
                    sql = "UPDATE "+table[type]+" SET Times=(Times+1) WHERE Qno="+tempQno+";";
                    try{
                        con.dataUpdate(sql);
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
                try {
                    //插入到paperfinal中
                    sql = "INSERT INTO paperfinal VALUES("+Pno+",'"+text_Pname.getText()+"','"+text_Pdate.getText()+"','"+quesAll+"','"+ansAll+"');";
                    con.dataUpdate(sql);
                    FileDialog saveAs=new FileDialog(new Frame(),"保存为word",FileDialog.SAVE);
                    saveAs.setFile(text_Pname.getText()+".doc");
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
                        JOptionPane.showMessageDialog(null,"保存为word文件失败！","错误",JOptionPane.ERROR_MESSAGE);
                    }
                }catch(Exception ex)
                {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null,"添加新试卷失败!","失败！",JOptionPane.OK_OPTION);
                }
            }
        });
        ButtonPanel.add(addfinishButton2);


        add(ButtonPanel,BorderLayout.EAST);

    }

    public JTextArea getEmailTextArea() {
        return emailTextArea;
    }

    public JTextArea getInfoTextArea() {
        return infoTextArea;
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
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


}
