package com.renz.healthmonitoring.consumerdata.domain.entity.cassandra;

import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table("devices")
@AllArgsConstructor
public class Device {
    
    @Id
    private String id;
    private String name;

}
