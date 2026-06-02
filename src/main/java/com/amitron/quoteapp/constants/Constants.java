/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.amitron.quoteapp.constants;

/**
 *
 * @author Ngn
 */
public interface Constants {
    String SERVER = "http://UCAMCO01:8080";
    String USERNAME = "Remote";
    String PASSWORD = "";
    String HOTFOLDER_PATH = "I:\\Hotfolders";
    String QED_PATH = "I:\\Work";
    String QUOTE_DIR = "L:\\I8_test";//testing
    //String QUOTE_DIR = "C:\\quote";//for Raju
    //String QUOTE_DIR = "S:\\Quotes";//production
    //String QUOTE_DIR = "Q:\\Quote Amitron";//production New
    String[] QUOTE_STRUCTURE = {"01_RFQ_Input", "02_Technical_Data", "03_Estimating",
        "04_Quote_Working", "05_Final_Quote", "06_Approvals", "07_Communications"};
    //String JSON_TEMPLATE = "C:\\quote\\empty_template.json";//for raju
    //String JSON_TEMPLATE = "S:\\Software\\AmitronApplications\\Integr8torSubmit\\empty_template.json";//production
    String JSON_TEMPLATE = "Q:\\Quote Amitron\\AmitronApplications\\Integr8torSubmit\\empty_template.json";//production New
    String JDBC_URL = "jdbc:sqlite:L:\\Development\\QuotationApplication\\I8QuoteData.db";//testing
    //String JDBC_URL = "jdbc:sqlite:C:\\quote\\I8QuoteData.db";//for Raju
    //String JDBC_URL = "jdbc:sqlite:S:\\Software\\AmitronApplications\\Integr8torSubmit\\I8QuoteData.db";//production
    //String JDBC_URL = "jdbc:sqlite:Q:\\Quote Amitron\\AmitronApplications\\Integr8torSubmit\\I8QuoteData.db";//production new
}
