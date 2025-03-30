package com.renz.healthmonitoring.consumerapi.domain.entity.cassandra;

import lombok.Getter;

@Getter
public abstract class CassandraEntityLegacy {

    private String tableName;
    private String uuid;

    public CassandraEntityLegacy(String tableName, String uuid) {
        this.tableName = tableName;
        this.uuid = uuid;
    }

}
