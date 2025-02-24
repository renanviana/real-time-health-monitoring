package com.renz.healthmonitoringapi.domain.entity.cassandra;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table("devices")
@AllArgsConstructor
public class Device {
    
    @PrimaryKey
    private String id;
    private String name;

}
