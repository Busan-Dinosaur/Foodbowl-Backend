package org.dinosaur.foodbowl;

import org.dinosaur.foodbowl.config.JpaConfig;
import org.dinosaur.foodbowl.testsupport.BlameTestSupport;
import org.dinosaur.foodbowl.testsupport.BookmarkTestSupport;
import org.dinosaur.foodbowl.testsupport.FollowTestSupport;
import org.dinosaur.foodbowl.testsupport.MemberTestSupport;
import org.dinosaur.foodbowl.testsupport.PhotoTestSupport;
import org.dinosaur.foodbowl.testsupport.PostTestSupport;
import org.dinosaur.foodbowl.testsupport.StoreTestSupport;
import org.dinosaur.foodbowl.testsupport.ThumbnailTestSupport;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    protected MemberTestSupport memberTestSupport;
    @Autowired
    protected FollowTestSupport followTestSupport;
    @Autowired
    protected PostTestSupport postTestSupport;
    @Autowired
    protected StoreTestSupport storeTestSupport;
    @Autowired
    protected PhotoTestSupport photoTestSupport;
    @Autowired
    protected ThumbnailTestSupport thumbnailTestSupport;
    @Autowired
    protected BookmarkTestSupport bookmarkTestSupport;
    @Autowired
    protected BlameTestSupport blameTestSupport;
}
