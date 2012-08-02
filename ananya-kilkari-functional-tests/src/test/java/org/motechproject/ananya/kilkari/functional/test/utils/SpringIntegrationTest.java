package org.motechproject.ananya.kilkari.functional.test.utils;

import org.ektorp.BulkDeleteDocument;
import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.motechproject.ananya.kilkari.functional.test.domain.Ivr;
import org.motechproject.ananya.kilkari.functional.test.domain.SubscriptionManager;
import org.motechproject.ananya.kilkari.functional.test.domain.Time;
import org.motechproject.ananya.kilkari.functional.test.verifiers.CampaignMessageVerifier;
import org.motechproject.ananya.kilkari.functional.test.verifiers.SubscriptionVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationKilkariFunctionalTestContext.xml")
@ActiveProfiles("test")
public abstract class SpringIntegrationTest {

    @Qualifier("kilkariDbConnector")
    @Autowired
    protected CouchDbConnector kilkariDbConnector;

    protected ArrayList<BulkDeleteDocument> toDelete;

    @Autowired
    protected Ivr ivr;
    @Autowired
    protected SubscriptionManager subscriptionManager;
    @Autowired
    protected Time time;
    @Autowired
    protected CampaignMessageVerifier campaignMessageVerifier;
    @Autowired
    protected SubscriptionVerifier subscriptionVerifier;

    @Before
    public void before() {
        toDelete = new ArrayList<>();
    }

    @After
    public void after() {
        kilkariDbConnector.executeBulk(toDelete);
    }

    protected void markForDeletion(Object document) {
        toDelete.add(BulkDeleteDocument.of(document));
    }
}
