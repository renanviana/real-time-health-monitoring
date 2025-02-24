package com.renz.healthmonitoringbroker.adapter.persistence;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.renz.healthmonitoringbroker.domain.entity.cassandra.CassandraEntityLegacy;

public abstract class CassandraRepositoryLegacy {
    
    protected void setRandomUUID(CassandraEntityLegacy entityLegacy) {
        if (StringUtils.isBlank(entityLegacy.getUuid())) {
            entityLegacy.setUuid(UUID.randomUUID().toString());
        }
    }

}
