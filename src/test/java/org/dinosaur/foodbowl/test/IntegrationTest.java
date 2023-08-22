package org.dinosaur.foodbowl.test;

import org.dinosaur.foodbowl.test.persister.MemberTestPersister;
import org.dinosaur.foodbowl.test.persister.PhotoTestPersister;
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

    @Autowired
    protected MemberTestPersister memberTestPersister;

    @Autowired
    protected PhotoTestPersister photoTestPersister;
}
