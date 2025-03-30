package com.renz.healthmonitoring.consumerapi.domain.entity.cassandra;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Registry extends CassandraEntityLegacy {
    
    private String data;
    private Long timestamp;

    public Registry(String tableName, String uuid, String data) {
        super("t_".concat(tableName).replace("-", "_"), uuid);
        this.data = data;
    }

}
