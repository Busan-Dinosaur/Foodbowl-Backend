package org.dinosaur.foodbowl.global.infra.db;

import static org.dinosaur.foodbowl.global.infra.db.DataSourceType.Key.REPLICA_NAME;
import static org.dinosaur.foodbowl.global.infra.db.DataSourceType.Key.ROUTING_NAME;
import static org.dinosaur.foodbowl.global.infra.db.DataSourceType.Key.SOURCE_NAME;

import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

@Configuration
public class DataSourceConfig {

    @Bean
    @Qualifier(SOURCE_NAME)
    @ConfigurationProperties(prefix = "spring.datasource.source")
    public DataSource sourceDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @Qualifier(REPLICA_NAME)
    @ConfigurationProperties(prefix = "spring.datasource.replica")
    public DataSource replicaDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @Qualifier(ROUTING_NAME)
    public DataSource routingDataSource(
            @Qualifier(SOURCE_NAME) DataSource sourceDataSource,
            @Qualifier(REPLICA_NAME) DataSource replicaDataSource
    ) {
        return RoutingDataSource.from(Map.of(
                DataSourceType.SOURCE, sourceDataSource,
                DataSourceType.REPLICA, replicaDataSource
        ));
    }

    @Bean
    @Primary
    public DataSource dataSource(@Qualifier(ROUTING_NAME) DataSource routingDataSource) {
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }
}
