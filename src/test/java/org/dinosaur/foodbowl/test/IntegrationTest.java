package org.dinosaur.foodbowl.test;

import org.dinosaur.foodbowl.test.persister.BlameTestPersister;
import org.dinosaur.foodbowl.test.persister.BookmarkTestPersister;
import org.dinosaur.foodbowl.test.persister.FollowTestPersister;
import org.dinosaur.foodbowl.test.persister.MemberTestPersister;
import org.dinosaur.foodbowl.test.persister.PhotoTestPersister;
import org.dinosaur.foodbowl.test.persister.ReviewTestPersister;
import org.dinosaur.foodbowl.test.persister.SchoolTestPersister;
import org.dinosaur.foodbowl.test.persister.StoreTestPersister;
import org.dinosaur.foodbowl.test.persister.ThumbnailTestPersister;
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
    protected BlameTestPersister blameTestPersister;

    @Autowired
    protected BookmarkTestPersister bookmarkTestPersister;

    @Autowired
    protected FollowTestPersister followTestPersister;

    @Autowired
    protected MemberTestPersister memberTestPersister;

    @Autowired
    protected PhotoTestPersister photoTestPersister;

    @Autowired
    protected ReviewTestPersister reviewTestPersister;

    @Autowired
    protected SchoolTestPersister schoolTestPersister;

    @Autowired
    protected StoreTestPersister storeTestPersister;

    @Autowired
    protected ThumbnailTestPersister thumbnailTestPersister;
}
