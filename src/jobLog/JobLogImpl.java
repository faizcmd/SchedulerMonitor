package jobLog;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JobLogImpl implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
    	
    	 Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
    	   
        System.out.println("[JobLogHealthCheckJob] Running check at " + new Date());
        try {
            // 🔹 SQL to check last 1 hour logs
            String sql = "SELECT COUNT(*) FROM joblogs " +
                         "WHERE STR_TO_DATE(startat, '%Y-%m-%d %H:%i:%s') >= NOW() - INTERVAL 1 HOUR";

            // 🔹 3 DBs to check (replace with actual)
            String[] dbUrls = {
                    "jdbc:mysql://localhost:3306/swa",
                    "jdbc:mysql://localhost:3306/eb2bmw",
                    "jdbc:mysql://localhost:3306/npcts"
            };
            String user = "root";
            String pass = "root";

            for (String url : dbUrls) {
                checkDatabase(url, user, pass, sql);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void checkDatabase(String url, String user, String pass, String sql) {
        try (Connection con = DriverManager.getConnection(url, user, pass);
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                int count = rs.getInt(1);
                if (count == 0) {
                    // 🔹 No logs in last 1 hour → send alert
                    String subject = "⚠️ No job logs in last hour for DB: " + url;
                    String body = "Dear Team,<br><br>No job logs found in the last 1 hour for database:<br>" +
                                  url + "<br><br>Please check scheduler / processing.<br><br>Time: " +
                                  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                    EmailUtil.sendEmail(subject, body,
                            "shaikh.faizan@asterixsolution.com,naved.ansari@asterixsolution.com,faizan.ppc@gmail.com");

                    System.out.println("[JobLogHealthCheckJob] ALERT sent for DB: " + url);
                } else {
                    System.out.println("[JobLogHealthCheckJob] Logs found (" + count + ") for DB: " + url);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[JobLogHealthCheckJob] Failed checking DB: " + url);
        }
    }
}
	