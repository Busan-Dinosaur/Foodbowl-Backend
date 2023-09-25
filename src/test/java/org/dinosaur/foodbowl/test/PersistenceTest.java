package org.dinosaur.foodbowl.test;

import org.dinosaur.foodbowl.global.config.JpaConfig;
import org.dinosaur.foodbowl.test.persister.BlameTestPersister;
import org.dinosaur.foodbowl.test.persister.BookmarkTestPersister;
import org.dinosaur.foodbowl.test.persister.FollowTestPersister;
import org.dinosaur.foodbowl.test.persister.MemberTestPersister;
import org.dinosaur.foodbowl.test.persister.Persister;
import org.dinosaur.foodbowl.test.persister.PhotoTestPersister;
import org.dinosaur.foodbowl.test.persister.ReviewPhotoTestPersister;
import org.dinosaur.foodbowl.test.persister.ReviewTestPersister;
import org.dinosaur.foodbowl.test.persister.SchoolTestPersister;
import org.dinosaur.foodbowl.test.persister.StoreTestPersister;
import org.dinosaur.foodbowl.test.persister.ThumbnailTestPersister;
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
    protected ReviewPhotoTestPersister reviewPhotoTestPersister;

    @Autowired
    protected ReviewTestPersister reviewTestPersister;

    @Autowired
    protected SchoolTestPersister schoolTestPersister;

    @Autowired
    protected StoreTestPersister storeTestPersister;

    @Autowired
    protected ThumbnailTestPersister thumbnailTestPersister;
}
