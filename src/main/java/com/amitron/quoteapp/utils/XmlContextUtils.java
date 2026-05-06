/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.amitron.quoteapp.utils;

/**
 *
 * @author Ngn
 */
import org.w3c.dom.Document;
import javax.xml.parsers.*;
import javax.xml.xpath.*;
import java.io.File;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.NodeList;

public class XmlContextUtils {

    private final Document document;
    private final XPath xpath;

    public XmlContextUtils(File xmlFile) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        this.document = factory.newDocumentBuilder().parse(xmlFile);
        this.xpath = XPathFactory.newInstance().newXPath();
    }

    public String getValue(String xpathExpr) {
        try {
            String value = xpath.evaluate(xpathExpr, document);
            return (value == null || value.trim().isEmpty()) ? null : value.trim();
        } catch (Exception e) {
            return null;
        }
    }

    public int getInt(String xpathExpr, int defaultVal) {
        try {
            String val = getValue(xpathExpr);
            return val == null ? defaultVal : Integer.parseInt(val);
        } catch (Exception e) {
            return defaultVal;
        }
    }

    public int getNodeCount(String xpathExpr) {
        try {
            NodeList nodes = (NodeList) xpath.evaluate(xpathExpr, document, XPathConstants.NODESET);
            return nodes.getLength();
        } catch (Exception e) {
            return 0;
        }
    }

    public NodeList getNodes(String xpathExpr) {
        try {
            return (NodeList) xpath.evaluate(xpathExpr, document, XPathConstants.NODESET);
        } catch (Exception e) {
            return null;
        }
    }

    public static Double findMin(XmlContextUtils xml, String xpathExpr) {

        NodeList nodes = xml.getNodes(xpathExpr);
        if (nodes == null) {
            return null;
        }

        Double min = null;

        for (int i = 0; i < nodes.getLength(); i++) {
            String text = nodes.item(i).getTextContent();
            Double val = parseDoubleSafe(text);

            if (val != null) {
                if (min == null || val < min) {
                    min = val;
                }
            }
        }
        return min;
    }

    private static Double parseDoubleSafe(String val) {
        if (val == null) {
            return null;
        }
        val = val.trim();
        if (val.equalsIgnoreCase("unknown") || val.equalsIgnoreCase("NR")) {
            return null;
        }
        try {
            return Double.parseDouble(val);
        } catch (Exception e) {
            return null;
        }
    }

    public static Object getValue(JSONObject json, String path) {

        if (json == null || path == null || path.isEmpty()) {
            return null;
        }

        String[] keys = path.split("\\.");
        Object current = json;

        for (String key : keys) {

            if (current instanceof JSONObject) {
                current = ((JSONObject) current).opt(key);
            } else if (current instanceof JSONArray) {
                try {
                    int index = Integer.parseInt(key);
                    current = ((JSONArray) current).opt(index);
                } catch (NumberFormatException e) {
                    return null; // invalid index
                }
            } else {
                return null;
            }

            if (current == null) {
                return null;
            }
        }

        return current;
    }

    public double getDouble(String xpathExpr) {
        try {
            return (Double) xpath.evaluate(
                    xpathExpr,
                    document,
                    XPathConstants.NUMBER
            );
        } catch (Exception e) {
            return 0;
        }
    }
}
