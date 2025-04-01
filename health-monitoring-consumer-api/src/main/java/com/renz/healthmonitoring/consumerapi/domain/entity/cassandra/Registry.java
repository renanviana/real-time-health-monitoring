package com.renz.healthmonitoring.consumerapi.domain.entity.cassandra;

import com.renz.healthmonitoring.consumerapi.helper.TableNameHelper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Registry extends CassandraEntityLegacy {
    
    private String data;
    private Long timestamp;

    public Registry(String tableName, String uuid, String data) {
        super(TableNameHelper.buildTableName(tableName), uuid);
        this.data = data;
    }

}
