package com.renz.healthmonitoring.consumerdata.helper;

public class TableNameHelper {
    
    public static String buildTableName(String uuid) {
        return "t_" + uuid.toString().replaceAll("-", "_");
    }
}
