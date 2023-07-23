package org.dinosaur.foodbowl;

import org.dinosaur.foodbowl.config.JpaConfig;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Import(JpaConfig.class)
@ComponentScan(basePackages = "org.dinosaur.foodbowl.testsupport")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest
public class RepositoryTest {
}
