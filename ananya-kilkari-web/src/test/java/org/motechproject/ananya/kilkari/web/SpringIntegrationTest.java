package org.motechproject.ananya.kilkari.web;

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
@ContextConfiguration("classpath:applicationContext.xml")
@ActiveProfiles("test")
public abstract class SpringIntegrationTest {

    @Qualifier("kilkariSubscriptionDbConnector")
    @Autowired
    protected CouchDbConnector kilkariSubscriptionDbConnector;

    protected ArrayList<BulkDeleteDocument> toDelete;

    @Before
    public void before() {
        toDelete = new ArrayList<BulkDeleteDocument>();
    }

    @After
    public void after() {
        kilkariSubscriptionDbConnector.executeBulk(toDelete);
    }

    protected void markForDeletion(Object... documents) {
        for (Object document : documents) {
            toDelete.add(BulkDeleteDocument.of(document));
        }
    }
}
