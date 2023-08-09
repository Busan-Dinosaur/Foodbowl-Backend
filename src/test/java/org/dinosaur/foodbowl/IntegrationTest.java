package org.dinosaur.foodbowl;

import org.dinosaur.foodbowl.test.persister.ReviewTestPersister;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Transactional
@SpringBootTest
public class IntegrationTest {

    @Autowired
    protected ReviewTestPersister reviewTestPersister;
}
