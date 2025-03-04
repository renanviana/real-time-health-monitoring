-- CREATE KEYTSPACE
CREATE KEYSPACE IF NOT EXISTS health_monitoring WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

-- ACTIVE KEYTSPACE TO USE
USE health_monitoring;

-- CREATE DEVICES TABLE
CREATE TABLE IF NOT EXISTS devices (id TEXT PRIMARY KEY, type TEXT);

-- CREATE INDEXES
CREATE INDEX IF NOT EXISTS ON devices (type);

