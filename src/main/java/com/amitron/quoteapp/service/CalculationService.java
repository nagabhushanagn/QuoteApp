/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.amitron.quoteapp.service;

import com.amitron.quoteapp.repository.CalculationRepository;
import java.util.List;
import org.json.JSONObject;

/**
 *
 * @author Ngn
 */
public class CalculationService {

    public static void saveCalculation(int submitId, int version, int masterPriceVersion, JSONObject json, boolean newVersion) throws Exception {
        if (newVersion) {
            CalculationRepository.insertCalculation(submitId, version, masterPriceVersion,json.toString());

        } else {
            boolean exists = CalculationRepository.versionExists(submitId, version);
            if (exists) {
                CalculationRepository.updateCalculation(submitId, version,json.toString());
            } else {
                CalculationRepository.insertCalculation(submitId, version, masterPriceVersion, json.toString());
            }
        }
    }

    public static int saveAsNewVersion(int submitId, JSONObject json, int masterPriceVersion) throws Exception {
        int nextVersion = CalculationRepository.getNextVersion(submitId);
        json.put("version", nextVersion);
        CalculationRepository.insertCalculation(submitId, nextVersion, masterPriceVersion,json.toString());

        return nextVersion;
    }

    public static List<Integer> getVersions(int submitId) throws Exception {
        return CalculationRepository.getVersions(submitId);
    }

    public static JSONObject loadCalculation(int submitId, int version) throws Exception {
        String json = CalculationRepository.getCalculationData(submitId, version);
        if (json == null || json.isEmpty()) {
            return null;
        }

        return new JSONObject(json);
    }
    
    public static JSONObject getLatestMasterPrice() {
        return CalculationRepository.getLatestMasterPrice();
    }
    
    public static JSONObject getMasterPrice(int version) {
        return CalculationRepository.getMasterPrice(version);
    }
}
