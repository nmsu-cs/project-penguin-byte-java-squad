import java.sql.*;
public class database {

    private Connection connection;
    public String host = "dbclass.cs.nmsu.edu";
    public String port = "3306";
    public String userN = "cs371sp24";
    public String pass = "RunningBeaver120";
    public String data = "cs371sp24";

    public void setConnectionSettings(String ho, String po, String us, String pa, String da){
        host = ho;
        port = po;
        userN = us;
        pass = pa;
        data = da;
    }

    public Connection getConnection() throws SQLException{
        if (connection != null){
            return connection;
        }

        System.out.println(host);

        String url = "jdbc:mysql://"+host+":"+port+"/"+data;
        String user = userN;
        String password = pass;

        try {
            this.connection = DriverManager.getConnection(url, user, password);
        }catch(Exception e){
            System.out.println("Database connection failed, dashboard.database is probably not correctly setup.");
            System.out.println(e);
        }
        return this.connection;
    }
}
