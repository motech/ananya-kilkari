package org.motechproject.ananya.kilkari.repository;

import org.ektorp.BulkDeleteDocument;
import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.util.ArrayList;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationAnanyaKilkariSubscriptionContext.xml")
@ActiveProfiles("test")
public abstract class SubscriptionBaseIT {

    @Qualifier("kilkariDbConnector")
    @Autowired
    protected CouchDbConnector kilkariDbConnector;

    protected ArrayList<BulkDeleteDocument> toDelete;

    @Before
    public void before() {
        toDelete = new ArrayList<BulkDeleteDocument>();
    }

    @After
    public void after() {
        kilkariDbConnector.executeBulk(toDelete);
    }

    protected void markForDeletion(Object document) {
        toDelete.add(BulkDeleteDocument.of(document));
    }
}
