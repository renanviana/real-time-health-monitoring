package com.renz.healthmonitoring.consumerapi.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TableNameHelperTest {

    @Test
    public void shouldBuildTableNameCorrectly() {
        String uuid = "123e4567-e89b-12d3-a456-426614174000";
        String tableName = TableNameHelper.buildTableName(uuid);
        assertEquals("t_123e4567_e89b_12d3_a456_426614174000", tableName);
    }

    @Test
    public void shouldCoverClassDefinition() {
        new TableNameHelper(); // invokes the implicit constructor
    }

}
