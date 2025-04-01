package com.renz.healthmonitoring.consumerapi.helper;

public class TableNameHelper {
    
    public static String buildTableName(String uuid) {
        return "t_" + uuid.toString().replaceAll("-", "_");
    }
}
