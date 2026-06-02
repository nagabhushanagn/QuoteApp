/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.amitron.quoteapp.repository;

import com.amitron.quoteapp.constants.Constants;
import com.amitron.quoteapp.constants.I8Progress;
import com.amitron.quoteapp.model.SubmitRow;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import org.json.JSONObject;

/**
 *
 * @author Ngn
 */
public class I8JobSubmitRepository implements Constants {

    public static void insertSubmitData(
            int i8Id,
            Integer freshdeskId,
            String customerName,
            String customerCode,
            String originalData,
            I8Progress progress,
            String partNum,
            boolean is_itar
    ) throws SQLException {

        String sql
                = "INSERT INTO I8_Submit "
                + "(i8_id, freshdesk_id, customer_name, customer_code,"
                + "original_data, progress, submit_time, part_number, itar) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        DateTimeFormatter formatter
                = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        String submitTime = LocalDateTime.now().format(formatter);

        try (Connection conn = DriverManager.getConnection(JDBC_URL); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, i8Id);

            if (freshdeskId != null) {
                ps.setInt(2, freshdeskId);
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }

            ps.setString(3, customerName);
            ps.setString(4, customerCode);
            ps.setString(5, originalData);
            ps.setString(6, progress.getDbValue());
            ps.setString(7, submitTime);
            ps.setString(8, partNum);
            if (is_itar) {
                ps.setInt(9, 1);
            } else {
                ps.setInt(9, 0);
            }

            ps.executeUpdate();
        }
    }

    public static List<Integer> fetchActiveI8Ids() throws SQLException {

        List<Integer> ids = new ArrayList<>();

        String sql
                = "SELECT i8_id "
                + "FROM I8_Submit "
                + "WHERE progress IN ('N','A','R') "
                + "ORDER BY submit_time DESC "
                + "LIMIT 100";

        try (Connection conn = DriverManager.getConnection(JDBC_URL); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ids.add(rs.getInt("i8_id"));
            }
        }
        return ids;
    }

    public static I8Progress getProgressFromDb(int i8Id) throws SQLException {

        String sql = "SELECT progress FROM I8_Submit WHERE i8_id = ?";

        try (Connection conn = DriverManager.getConnection(JDBC_URL); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, i8Id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return I8Progress.fromCode(rs.getString("progress"));
                }
            }
        }
        return null;
    }

    public static void updateProgressInDb(int i8Id, I8Progress progress)
            throws SQLException {

        String sql
                = "UPDATE I8_Submit SET progress = ? WHERE i8_id = ?";

        try (Connection conn = DriverManager.getConnection(JDBC_URL); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, progress.getDbValue());
            ps.setInt(2, i8Id);
            ps.executeUpdate();
        }
    }

    public static ObservableList<SubmitRow> fetchLatestSubmits()
            throws SQLException {

        ObservableList<SubmitRow> list = FXCollections.observableArrayList();

        String sql
                = "SELECT id, i8_id, freshdesk_id, customer_name, "
                + "customer_code, original_data, progress, "
                + "submit_time, part_number, itar "
                + "FROM I8_Submit ORDER BY submit_time DESC LIMIT 100";

        try (Connection conn = DriverManager.getConnection(JDBC_URL); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Integer freshdeskId = (Integer) rs.getObject("freshdesk_id");

                String progressCode = rs.getString("progress");
                I8Progress progress = I8Progress.fromCode(progressCode);

                boolean isItar = rs.getInt("itar") == 1;

                list.add(new SubmitRow(
                        rs.getInt("id"),
                        rs.getInt("i8_id"),
                        freshdeskId,
                        rs.getString("customer_name"),
                        rs.getString("customer_code"),
                        rs.getString("original_data"),
                        rs.getString("part_number"),
                        isItar,
                        progress != null ? progress.getDisplayName() : progressCode,
                        rs.getString("submit_time")
                ));
            }
        }
        return list;
    }

    public static boolean deleteRow(int i8Id) {
        String sql = "DELETE FROM I8_Submit WHERE i8_id = ?";

        try (Connection conn = DriverManager.getConnection(JDBC_URL); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, i8Id);
            int affected = ps.executeUpdate();

            return affected > 0; // true if something was deleted

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void updateQedDataToDb(int i8Id, String qedData)
            throws SQLException {

        String sql = "UPDATE I8_Submit SET qed_data = ?, qed_loaded = 1 WHERE i8_id = ?";

        try (Connection conn = DriverManager.getConnection(JDBC_URL); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, qedData);
            ps.setInt(2, i8Id);
            ps.executeUpdate();
        }
    }

    public static JSONObject getQedJsonFromDb(int i8Id) throws SQLException {

        String sql = "SELECT qed_data FROM I8_Submit WHERE i8_id = ? AND qed_loaded = 1 ORDER BY id DESC LIMIT 1";

        try (Connection conn = DriverManager.getConnection(JDBC_URL); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, i8Id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {

                    String jsonString = rs.getString("qed_data");

                    if (jsonString != null && !jsonString.isEmpty()) {
                        return new JSONObject(jsonString);
                    }
                }
            }
        }

        return null;
    }

    public static void saveOrUpdateQuoteData(int submitId,
            String optimizerData,
            String savedData) {

        try (Connection conn = DriverManager.getConnection(JDBC_URL)) {
            // Ensure table exists
            ensureQuoteTableExists(conn);
            //Step 1: Check if record exists
            String checkSql = "SELECT id FROM quote_data WHERE submit_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, submitId);

            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // UPDATE
                String updateSql = "UPDATE quote_data "
                        + "SET optimizer_data = ?, "
                        + "saved_data = ?, "
                        + "updated_on = CURRENT_TIMESTAMP "
                        + "WHERE submit_id = ?";

                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setString(1, optimizerData);
                updateStmt.setString(2, savedData);
                updateStmt.setInt(3, submitId);

                updateStmt.executeUpdate();

                System.out.println("Quote data UPDATED for submit_id: " + submitId);

            } else {
                // INSERT
                String insertSql = "INSERT INTO quote_data "
                        + "(submit_id, optimizer_data, saved_data) "
                        + "VALUES (?, ?, ?)";

                PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setInt(1, submitId);
                insertStmt.setString(2, optimizerData);
                insertStmt.setString(3, savedData);

                insertStmt.executeUpdate();

                System.out.println("Quote data INSERTED for submit_id: " + submitId);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //create table if not exist
    private static void ensureQuoteTableExists(Connection conn) throws Exception {
        String createSql = "CREATE TABLE IF NOT EXISTS quote_data ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "submit_id INTEGER NOT NULL, "
                + "optimizer_data TEXT, "
                + "saved_data TEXT, "
                + "created_on TEXT DEFAULT CURRENT_TIMESTAMP, "
                + "updated_on TEXT, "
                + "FOREIGN KEY(submit_id) REFERENCES I8_Submit(id) "
                + "ON UPDATE CASCADE ON DELETE CASCADE"
                + ")";

        conn.createStatement().execute(createSql);
    }

    public static Map<String, String> getQuoteData(int submitId) {

        Map<String, String> result = new HashMap<>();

        try (Connection conn = DriverManager.getConnection(JDBC_URL)) {

            String sql = "SELECT optimizer_data, saved_data "
                    + "FROM quote_data WHERE submit_id = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, submitId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                result.put("optimizer", rs.getString("optimizer_data"));
                result.put("saved", rs.getString("saved_data"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void saveOrUpdateOptimizerData(int submitId, String optimizerData) {

        try (Connection conn = DriverManager.getConnection(JDBC_URL)) {

            //Check if record exists
            String checkSql = "SELECT id FROM quote_data WHERE submit_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, submitId);

            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                //UPDATE only optimizer_data
                String updateSql = "UPDATE quote_data "
                        + "SET optimizer_data = ?, "
                        + "updated_on = CURRENT_TIMESTAMP "
                        + "WHERE submit_id = ?";

                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setString(1, optimizerData);
                updateStmt.setInt(2, submitId);

                updateStmt.executeUpdate();

                System.out.println("Optimizer data UPDATED");

            } else {
                //INSERT (optimizer only, saved_data null)
                String insertSql = "INSERT INTO quote_data "
                        + "(submit_id, optimizer_data) "
                        + "VALUES (?, ?)";

                PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setInt(1, submitId);
                insertStmt.setString(2, optimizerData);

                insertStmt.executeUpdate();

                System.out.println("Optimizer data INSERTED");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveOrUpdateStackupData(int submitId, String satckupData) {

        try (Connection conn = DriverManager.getConnection(JDBC_URL)) {

            //Check if record exists
            String checkSql = "SELECT id FROM quote_data WHERE submit_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, submitId);

            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                //UPDATE only optimizer_data
                String updateSql = "UPDATE quote_data "
                        + "SET stackup_data = ?, "
                        + "updated_on = CURRENT_TIMESTAMP "
                        + "WHERE submit_id = ?";

                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setString(1, satckupData);
                updateStmt.setInt(2, submitId);

                updateStmt.executeUpdate();

                System.out.println("stackup data UPDATED");

            } else {
                //INSERT (optimizer only, saved_data null)
                String insertSql = "INSERT INTO quote_data "
                        + "(submit_id, stackup_data) "
                        + "VALUES (?, ?)";

                PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setInt(1, submitId);
                insertStmt.setString(2, satckupData);

                insertStmt.executeUpdate();

                System.out.println("Stackup data INSERTED");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String loadStackupData(int rowId) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL)) {
            String sql = "SELECT stackup_data FROM quote_data WHERE submit_id = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, rowId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("stackup_data");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static Map<String, String> getCalculationSourceData(int submitId) {

        Map<String, String> data = new HashMap<>();

        String sql = "SELECT optimizer_data, "
                + "saved_data, "
                + "stackup_data "
                + "FROM quote_data "
                + "WHERE submit_id = ?";

        try (Connection conn
                = DriverManager.getConnection(JDBC_URL); PreparedStatement ps
                = conn.prepareStatement(sql)) {
            ps.setInt(1, submitId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                data.put("optimizer", rs.getString("optimizer_data"));

                data.put("saved", rs.getString("saved_data"));

                data.put("stackup", rs.getString("stackup_data"));
            }

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return data;
    }

    
}
