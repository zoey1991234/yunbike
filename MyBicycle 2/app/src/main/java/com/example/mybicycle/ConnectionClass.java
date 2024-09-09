// ConnectionClass.java
package com.example.mybicycle;

import android.util.Log;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.Random;

public class ConnectionClass {

    protected static String db = "bicycle";
    protected static String ip = "recipeedb-2.mysql.database.azure.com";
    protected static String port = "3306";
    protected static String username = "yuntech";
    protected static String password = "recipeDB@@";

    public Connection CONN() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://" + ip + ":" + port + "/" + db, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            Log.e("ERROR", Objects.requireNonNull(e.getMessage()));
        }
        return conn;
    }


    public boolean checkInventoryId(int inventoryId) {
        boolean inventoryExists = false;
        Connection connection = CONN();

        if (connection != null) {
            try {
                String query = "SELECT * FROM inventories WHERE inventoryId = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, inventoryId);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    inventoryExists = true;
                }

                resultSet.close();
                preparedStatement.close();
                connection.close();
            } catch (SQLException e) {
                Log.e("ERROR", e.getMessage());
            }
        }

        return inventoryExists;
    }

    public boolean updateReturnTime(int inventoryId, Timestamp returnTime) {
        boolean updateSuccessful = false;
        Connection connection = CONN();

        if (connection != null) {
            try {
                String query = "UPDATE rents SET returnTime = ? WHERE inventoryId = ? ORDER BY rentTime DESC LIMIT 1";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setTimestamp(1, returnTime);
                preparedStatement.setInt(2, inventoryId);
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    updateSuccessful = true;
                }

                preparedStatement.close();
                connection.close();
            } catch (SQLException e) {
                Log.e("ERROR", e.getMessage());
            }
        }

        return updateSuccessful;
    }

    public boolean insertIssue(int accountId, Timestamp postTime, String issueType, String issue, byte[] issuePic) {
        boolean insertSuccessful = false;
        Connection connection = CONN();

        if (connection != null) {
            try {
                String query = "INSERT INTO issues (accountId, postTime, issueType, issue, issuePic) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, accountId);
                preparedStatement.setTimestamp(2, postTime);
                preparedStatement.setString(3, issueType);
                preparedStatement.setString(4, issue);
                preparedStatement.setBytes(5, issuePic);
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    insertSuccessful = true;
                }

                preparedStatement.close();
                connection.close();
            } catch (SQLException e) {
                Log.e("ERROR", e.getMessage());
            }
        }

        return insertSuccessful;
    }

    public boolean updateSiteIdToNull(int inventoryId) {
        boolean updateSuccessful = false;
        Connection connection = CONN();

        if (connection != null) {
            try {
                String query = "UPDATE inventories SET siteId = NULL WHERE inventoryId = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, inventoryId);
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    updateSuccessful = true;
                }

                preparedStatement.close();
                connection.close();
            } catch (SQLException e) {
                Log.e("ERROR", e.getMessage());
            }
        }

        return updateSuccessful;
    }

    public boolean updateSiteIdToRandom(int inventoryId) {
        boolean updateSuccessful = false;
        Connection connection = CONN();

        if (connection != null) {
            try {
                int siteId = new Random().nextInt(5) + 1; // Random number between 1 and 5
                String query = "UPDATE inventories SET siteId = ? WHERE inventoryId = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, siteId);
                preparedStatement.setInt(2, inventoryId);
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    updateSuccessful = true;
                }

                preparedStatement.close();
                connection.close();
            } catch (SQLException e) {
                Log.e("ERROR", e.getMessage());
            }
        }

        return updateSuccessful;
    }

    public boolean insertRentRecord(int accountId, int rentSite, Timestamp rentTime, int inventoryId) {
        boolean insertSuccessful = false;
        Connection connection = CONN();

        if (connection != null) {
            try {
                String query = "INSERT INTO rents (accountId, rentSite, rentTime, inventoryId) VALUES (?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, accountId);
                preparedStatement.setInt(2, rentSite);
                preparedStatement.setTimestamp(3, rentTime);
                preparedStatement.setInt(4, inventoryId);
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    insertSuccessful = true;
                }

                preparedStatement.close();
                connection.close();
            } catch (SQLException e) {
                Log.e("ERROR", e.getMessage());
            }
        }

        return insertSuccessful;
    }

    public String getParkingInfo(int siteId) {
        Connection conn = CONN();
        String result = "";
        if (conn != null) {
            try {
                String query = "SELECT s.totalParkingSpace, COUNT(i.siteId) AS inventoryCount " +
                        "FROM bicycle.site s " +
                        "LEFT JOIN bicycle.inventories i ON s.siteId = i.siteId " +
                        "WHERE s.siteId = " + siteId;
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                if (rs.next()) {
                    int totalSpaces = rs.getInt("totalParkingSpace");
                    int availableSpaces = totalSpaces - rs.getInt("inventoryCount");
                    result = "Total parking spaces: " + totalSpaces + "\n" +
                            "Currently available: " + availableSpaces;
                }
                conn.close();
            } catch (SQLException e) {
                Log.e("ERROR", Objects.requireNonNull(e.getMessage()));
            }
        }
        return result;
    }
}
