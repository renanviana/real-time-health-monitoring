package com.renz.healthmonitoringbroker.domain.entity.cassandra;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Registry extends CassandraEntityLegacy {
    
    private String data;
    private Long timestamp;

    public Registry(String tableName, String data) {
        super(tableName);
        this.data = data;
    }

}
