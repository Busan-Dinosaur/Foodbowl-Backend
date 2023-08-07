package org.dinosaur.foodbowl.test;

import org.dinosaur.foodbowl.global.config.JpaConfig;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Import(JpaConfig.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest
public class PersistenceTest {
}
