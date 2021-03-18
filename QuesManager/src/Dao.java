
import java.sql.*;
import java.util.*;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.io.File;

//写一个类，用来与数据库建立连接，并且查询数据
class Dao {
    // 设定用户名和密码
    static String username= null;
    static String pwd = null;

    //关于数据库操作
    static PreparedStatement ps = null;
    static ResultSet rs = null;
    static Statement stmt = null;
    static String second = null;

    //mySQL连接
    private static final String DRIVERCLASS= "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/project?serverTimezone=GMT&&characterEncoding=utf8";

    static Connection conn = null;//数据库连接

    static {
        try {
            if (conn == null) {
                //mysql
                Class.forName(DRIVERCLASS);
                conn = DriverManager.getConnection(URL, "root", "123456");
             }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    //获取查询结果集
    public ResultSet getRs(String sql){
        if (sql.toLowerCase().indexOf("select")!=-1){
            try{
                stmt = conn.createStatement();
                rs = stmt.executeQuery(sql);
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        return rs;

    }

    //关闭数据库资源
    public void finalize() throws SQLException
    {
        conn.close();
        rs.close();
        ps.close();
    }

    //改
    public int dataUpdate(String sql) throws SQLException
    {
        Statement statement=conn.createStatement();
        int result=statement.executeUpdate(sql);
        statement.close();
        return result;
    }

    //查
    public int select(String sql, DefaultTableModel dtm)throws SQLException
    {
        Statement statement=conn.createStatement();
        ResultSet resultset=statement.executeQuery(sql);
        ResultSetMetaData rsmd=resultset.getMetaData();
        int c=rsmd.getColumnCount();
        int r=0;
        String []data=new String[c];
        while(resultset.next())
        {
            for(int i=0;i<c;i++)
            {
                data[i]=resultset.getString(i+1);
                if(data[i]!=null)
                    data[i]=data[i].trim();
            }
            dtm.addRow(data);
            r++;
        }
        return r;
    }


    // 向user表查询数据
    public static void queryUser(String role, String username) {
        try {
            ps = conn.prepareStatement("select * from user where role=? and userName=? ");
            //ps = conn.prepareStatement("select * from user_info where role=? and userName=? ");
            ps.setString(1, role);
            ps.setString(2, username);
            rs = ps.executeQuery();
            // 循环取出
            if (rs.next()) {
                pwd = rs.getString("userPwd");
                System.out.println("成功获取到密码和用户名from数据库");
                System.out.println("用户：" + username + "\t 密码：" + pwd + "\t");
            } else {
                JOptionPane.showMessageDialog(null, "没有此用户，请重新输入！", "提示消息", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

    }

    // 查询多个记录
    protected Vector selectSomeNote(String sql) {
        Vector vector = new Vector();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            int columnCount = rs.getMetaData().getColumnCount();
            int row = 1;
            while (rs.next()) {
                Vector rowV = new Vector();
                rowV.add(new Integer(row++));
                for (int column = 1; column <= columnCount; column++) {
                    rowV.add(rs.getObject(column));
                }
                vector.add(rowV);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vector;
    }
}



