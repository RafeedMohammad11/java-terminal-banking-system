package util;

import db.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class AccountNumberGenerator {

    private static final String PREFIX = "ACC-";
    private static final int    START  = 10001;

    public static String generate() {
        try {
            Connection conn = DatabaseConnection.getInstance();

            String sql = "SELECT COUNT(*) AS total FROM accounts";
            Statement stmt = conn.createStatement();
            ResultSet rs   = stmt.executeQuery(sql);

            if (rs.next()) {
                int total = rs.getInt("total");

                if (total == 0) {
                    // No accounts yet — start from 10001
                    return PREFIX + START;
                }
            }

            // Get the highest existing number
            String maxSql  = "SELECT MAX(CAST(SUBSTR(account_number, 4) AS INTEGER)) " +
                    "AS max_num FROM accounts";
            ResultSet rs2  = conn.createStatement().executeQuery(maxSql);

            if (rs2.next()) {
                int maxNum = rs2.getInt("max_num");
                return PREFIX + (maxNum + 1);
            }

        } catch (Exception e) {
            System.out.println("AccountNumberGenerator error: " + e.getMessage());
        }

        // Hard fallback — should never reach here
        return PREFIX + START;
    }
}