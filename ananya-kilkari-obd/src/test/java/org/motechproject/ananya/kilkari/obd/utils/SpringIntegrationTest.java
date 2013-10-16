package org.motechproject.ananya.kilkari.obd.utils;

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
@ContextConfiguration("classpath:applicationKilkariOBDContext.xml")
@ActiveProfiles("test")
public abstract class SpringIntegrationTest {

    @Qualifier("obdDbConnector")
    @Autowired
    protected CouchDbConnector obdDbConnector;

    protected ArrayList<BulkDeleteDocument> toDelete;

    @Before
    public void before() {
        toDelete = new ArrayList<>();
    }

    @After
    public void after() {
        obdDbConnector.executeBulk(toDelete);
    }

    protected void markForDeletion(Object document) {
        toDelete.add(BulkDeleteDocument.of(document));
    }
}
