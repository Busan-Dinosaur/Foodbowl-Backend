package org.dinosaur.foodbowl;

import org.dinosaur.foodbowl.global.config.JpaConfig;
import org.dinosaur.foodbowl.persister.SchoolTestPersister;
import org.dinosaur.foodbowl.persister.StoreTestPersister;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(JpaConfig.class)
@ComponentScan(basePackages = "org.dinosaur.foodbowl.persister")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class PersistenceTest {

    @Autowired
    protected StoreTestPersister storeTestPersister;

    @Autowired
    protected SchoolTestPersister schoolTestPersister;
}
