/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.amitron.quoteapp.repository;

import static com.amitron.quoteapp.constants.Constants.JDBC_URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

/**
 *
 * @author Ngn
 */
public class CalculationRepository {

    public static boolean versionExists(int submitId, int version) throws Exception {
        String sql
                = "SELECT id "
                + "FROM quote_calculation "
                + "WHERE submit_id=? "
                + "AND version=?";

        try (Connection conn
                = DriverManager.getConnection(JDBC_URL); PreparedStatement ps
                = conn.prepareStatement(sql)) {

            ps.setInt(1, submitId);
            ps.setInt(2, version);

            try (ResultSet rs = ps.executeQuery()) {

                return rs.next();
            }
        }
    }

    public static void insertCalculation(
            int submitId,
            int version,
            int masterPriceVersion,
            String json) throws Exception {

        String sql
                = "INSERT INTO quote_calculation "
                + "(submit_id, version, master_price_version, calculation_data) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conn
                = DriverManager.getConnection(JDBC_URL); PreparedStatement ps
                = conn.prepareStatement(sql)) {

            ps.setInt(1, submitId);
            ps.setInt(2, version);
            ps.setInt(3, masterPriceVersion);
            ps.setString(4, json);

            ps.executeUpdate();
        }
    }

    public static void updateCalculation(
            int submitId,
            int version,
            String json) throws Exception {

        String sql
                = "UPDATE quote_calculation "
                + "SET calculation_data=? "
                + "WHERE submit_id=? "
                + "AND version=?";

        try (Connection conn
                = DriverManager.getConnection(JDBC_URL); PreparedStatement ps
                = conn.prepareStatement(sql)) {

            ps.setString(1, json);
            ps.setInt(2, submitId);
            ps.setInt(3, version);

            ps.executeUpdate();
        }
    }

    public static int getNextVersion(
            int submitId) throws Exception {

        String sql
                = "SELECT MAX(version) max_version "
                + "FROM quote_calculation "
                + "WHERE submit_id=?";

        try (Connection conn
                = DriverManager.getConnection(JDBC_URL); PreparedStatement ps
                = conn.prepareStatement(sql)) {

            ps.setInt(1, submitId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return rs.getInt("max_version") + 1;
                }
            }
        }

        return 1;
    }

    public static List<Integer> getVersions(int submitId) throws Exception {
        List<Integer> versions = new ArrayList<>();
        String sql
                = "SELECT version "
                + "FROM quote_calculation "
                + "WHERE submit_id=? "
                + "ORDER BY version";

        try (Connection conn
                = DriverManager.getConnection(JDBC_URL); PreparedStatement ps
                = conn.prepareStatement(sql)) {

            ps.setInt(1, submitId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    versions.add(
                            rs.getInt("version"));
                }
            }
        }

        return versions;
    }

    public static String getCalculationData(int submitId, int version) throws Exception {
        String sql
                = "SELECT calculation_data "
                + "FROM quote_calculation "
                + "WHERE submit_id=? "
                + "AND version=?";

        try (Connection conn
                = DriverManager.getConnection(JDBC_URL); PreparedStatement ps
                = conn.prepareStatement(sql)) {

            ps.setInt(1, submitId);
            ps.setInt(2, version);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return rs.getString(
                            "calculation_data");
                }
            }
        }

        return null;
    }
    
    public static JSONObject getLatestMasterPrice() {
        String sql  = "SELECT version, master_price "
                + "FROM master_quote_price "
                + "ORDER BY version DESC "
                + "LIMIT 1";
        try (Connection conn
                = DriverManager.getConnection(JDBC_URL); PreparedStatement ps
                = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                JSONObject root = new JSONObject();

                root.put( "version", rs.getInt("version"));

                root.put( "data", new JSONObject( rs.getString( "master_price")));

                return root;
            }

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return new JSONObject();
    }
    
    public static JSONObject getMasterPrice(int version) {
        String sql  = "SELECT version, master_price "
                + "FROM master_quote_price "
                + "WHERE version = ? ";
        
        try (Connection conn
                = DriverManager.getConnection(JDBC_URL); PreparedStatement ps
                = conn.prepareStatement(sql)) {
            
            ps.setInt(1, version);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                JSONObject root = new JSONObject();
                root.put( "version", rs.getInt("version"));
                root.put( "data", new JSONObject( rs.getString( "master_price")));

                return root;
            }
        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return new JSONObject();
    }
}
