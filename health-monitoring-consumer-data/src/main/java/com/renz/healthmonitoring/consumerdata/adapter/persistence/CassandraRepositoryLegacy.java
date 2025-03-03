package com.renz.healthmonitoring.consumerdata.adapter.persistence;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.renz.healthmonitoring.consumerdata.domain.entity.cassandra.CassandraEntityLegacy;

public abstract class CassandraRepositoryLegacy {

    protected void setRandomUUID(CassandraEntityLegacy entityLegacy) {
        if (StringUtils.isBlank(entityLegacy.getUuid())) {
            entityLegacy.setUuid(UUID.randomUUID().toString());
        }
    }

}
