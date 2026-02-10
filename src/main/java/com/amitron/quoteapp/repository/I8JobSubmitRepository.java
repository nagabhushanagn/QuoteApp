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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

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

        String sql = 
            "INSERT INTO I8_Submit " +
            "(i8_id, freshdesk_id, customer_name, customer_code," +
             "original_data, progress, submit_time, part_number, itar) "+
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        String submitTime = LocalDateTime.now().format(formatter);

        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

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
            if(is_itar){
                ps.setInt(9, 1);
            }else{
                ps.setInt(9, 0);
            }
            

            ps.executeUpdate();
        }
    }

    public static List<Integer> fetchActiveI8Ids() throws SQLException {

        List<Integer> ids = new ArrayList<>();

        String sql =
            "SELECT i8_id " +
            "FROM I8_Submit " +
            "WHERE progress IN ('N','A','R','S','P') " +
            "ORDER BY submit_time DESC " +
            "LIMIT 100";

        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ids.add(rs.getInt("i8_id"));
            }
        }
        return ids;
    }

    public static I8Progress getProgressFromDb(int i8Id) throws SQLException {

        String sql = "SELECT progress FROM I8_Submit WHERE i8_id = ?";

        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

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

        String sql =
                "UPDATE I8_Submit SET progress = ? WHERE i8_id = ?";

        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, progress.getDbValue());
            ps.setInt(2, i8Id);
            ps.executeUpdate();
        }
    }
    
    public static ObservableList<SubmitRow> fetchLatestSubmits()
            throws SQLException {

        ObservableList<SubmitRow> list = FXCollections.observableArrayList();

        String sql
                = "SELECT i8_id, freshdesk_id, customer_name, "
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

        try ( Connection conn = DriverManager.getConnection(JDBC_URL);  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, i8Id);
            int affected = ps.executeUpdate();

            return affected > 0; // true if something was deleted

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
