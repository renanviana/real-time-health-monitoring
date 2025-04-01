package com.renz.healthmonitoring.consumerdata.adapter.persistence;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.metadata.Metadata;
import com.datastax.oss.driver.api.core.metadata.schema.KeyspaceMetadata;
import com.datastax.oss.driver.api.core.metadata.schema.TableMetadata;
import com.renz.healthmonitoring.consumerdata.domain.entity.cassandra.Registry;

@ExtendWith(MockitoExtension.class)
public class CassandraRegistryRepositoryLegacyTest {

    @Mock(lenient = true)
    private CqlSession cqlSession;

    @Mock(lenient = true)
    private Metadata metadata;

    @Mock(lenient = true)
    private KeyspaceMetadata keyspaceMetadata;

    @InjectMocks
    private CassandraRegistryRepositoryLegacy repository;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(repository, "keyspace", "test_keyspace");
        lenient().when(cqlSession.getMetadata()).thenReturn(metadata);
    }

    @Test
    public void shouldExecuteInsertStatement() {
        Registry registry = new Registry("table", "uuid", "data");
        repository.save(registry);
        verify(cqlSession).execute(anyString(), eq("uuid"), eq("data"), anyLong());
    }

    @Test
    public void shouldCreateTableWhenNotExists() {
        String tableName = "t_test_topic";
        when(metadata.getKeyspace("test_keyspace")).thenReturn(Optional.of(keyspaceMetadata));
        when(keyspaceMetadata.getTable(tableName)).thenReturn(Optional.empty());
        repository.createTable("test-topic");
        verify(cqlSession).execute(contains("CREATE TABLE " + tableName));
        verify(cqlSession).execute(contains("CREATE INDEX IF NOT EXISTS timestamp_idx"));
    }

    @Test
    public void shouldNotCreateTableWhenExists() {
        String tableName = "t_existing";
        when(metadata.getKeyspace("test_keyspace")).thenReturn(Optional.of(keyspaceMetadata));
        when(keyspaceMetadata.getTable(tableName)).thenReturn(Optional.of(mock(TableMetadata.class)));
        repository.createTable("existing");
        verify(cqlSession, never()).execute(contains("CREATE TABLE"));
    }

}
