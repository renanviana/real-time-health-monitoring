package com.renz.healthmonitoringbroker.domain.entity.cassandra;

import lombok.Getter;
import lombok.Setter;

@Getter
public abstract class CassandraEntityLegacy {

    private String tableName;

    @Setter
    protected String uuid;

    public CassandraEntityLegacy(String tableName) {
        this.tableName = tableName;
    }

}
