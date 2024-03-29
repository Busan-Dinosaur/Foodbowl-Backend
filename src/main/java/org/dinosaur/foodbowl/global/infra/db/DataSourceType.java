package org.dinosaur.foodbowl.global.infra.db;

import static org.dinosaur.foodbowl.global.infra.db.DataSourceType.DataSourceConst.REPLICA_NAME;
import static org.dinosaur.foodbowl.global.infra.db.DataSourceType.DataSourceConst.SOURCE_NAME;

public enum DataSourceType {

    SOURCE(SOURCE_NAME),
    REPLICA(REPLICA_NAME);

    private final String key;

    DataSourceType(String key) {
        this.key = key;
    }

    public static class DataSourceConst {
        public static final String ROUTING_NAME = "ROUTING";
        public static final String SOURCE_NAME = "SOURCE";
        public static final String REPLICA_NAME = "REPLICA";
    }
}
