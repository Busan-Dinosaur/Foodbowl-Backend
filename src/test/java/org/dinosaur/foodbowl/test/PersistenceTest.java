package org.dinosaur.foodbowl.test;

import org.dinosaur.foodbowl.global.config.JpaConfig;
import org.dinosaur.foodbowl.test.persister.FollowTestPersister;
import org.dinosaur.foodbowl.test.persister.MemberTestPersister;
import org.dinosaur.foodbowl.test.persister.Persister;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Import(JpaConfig.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Persister.class))
public class PersistenceTest {

    @Autowired
    protected MemberTestPersister memberTestPersister;

    @Autowired
    protected FollowTestPersister followTestPersister;
}
