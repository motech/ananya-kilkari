package org.motechproject.ananya.kilkari.subscription.repository;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriberCareDoc;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriberCareReasons;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

public class AllSubscriberCareDocsIT extends SpringIntegrationTest {
    @Autowired
    private AllSubscriberCareDocs allSubscriberCareDocs;

    @Before
    public void setUp() {
        allSubscriberCareDocs.removeAll();
    }

    @Test
    public void shouldFindSubscriberCareDoc() {
        String msisdn = "9876543211";
        SubscriberCareReasons reason = SubscriberCareReasons.HELP;
        DateTime createdAt = DateTime.now();
        SubscriberCareDoc subscriberCareDoc = new SubscriberCareDoc(msisdn, reason, createdAt, Channel.IVR);
        allSubscriberCareDocs.addOrUpdate(subscriberCareDoc);
        markForDeletion(subscriberCareDoc);

        SubscriberCareDoc subscriberCareDocs = allSubscriberCareDocs.find(msisdn, reason.name());

        assertNotNull(subscriberCareDocs);
        assertEquals(msisdn, subscriberCareDocs.getMsisdn());
        assertEquals(reason, subscriberCareDocs.getReason());
        assertEquals(createdAt.withZone(DateTimeZone.UTC), subscriberCareDocs.getCreatedAt());
    }

    @Test
    public void shouldAddNewSubscriberCareDoc() {
        SubscriberCareDoc subscriberCareDoc = new SubscriberCareDoc("9876543211", SubscriberCareReasons.HELP, DateTime.now(), Channel.IVR);
        allSubscriberCareDocs.addOrUpdate(subscriberCareDoc);
        markForDeletion(subscriberCareDoc);

        List<SubscriberCareDoc> subscriberCareDocs = allSubscriberCareDocs.getAll();
        assertEquals(1, subscriberCareDocs.size());
    }

    @Test
    public void shouldUpdateAnExistingSubscriberCareDocIfMsisdnAndReasonAreSame() {
        SubscriberCareDoc subscriberCareDoc = new SubscriberCareDoc("9876543211", SubscriberCareReasons.HELP, DateTime.now(), Channel.IVR);
        allSubscriberCareDocs.addOrUpdate(subscriberCareDoc);
        markForDeletion(subscriberCareDoc);

        DateTime createdAt = DateTime.now().plusDays(1);
        SubscriberCareDoc subscriberCareDoc2 = new SubscriberCareDoc("9876543211", SubscriberCareReasons.HELP, createdAt, Channel.IVR);
        allSubscriberCareDocs.addOrUpdate(subscriberCareDoc2);

        List<SubscriberCareDoc> subscriberCareDocs = allSubscriberCareDocs.getAll();
        assertEquals(1, subscriberCareDocs.size());
        assertEquals(createdAt.withZone(DateTimeZone.UTC), subscriberCareDocs.get(0).getCreatedAt());
    }

    @Test
    public void shouldDeleteAllDocumentsForAnMsisdn() {
        String msisdn = "12345";
        SubscriberCareReasons reason = SubscriberCareReasons.HELP;
        allSubscriberCareDocs.add(new SubscriberCareDoc(msisdn, reason, DateTime.now(), Channel.IVR));

        allSubscriberCareDocs.deleteFor(msisdn);

        assertTrue(allSubscriberCareDocs.findByMsisdn(msisdn).isEmpty());
    }

    @Test
    public void shouldFindDocumentsForAnMsisdn() {
        String msisdn = "12345";
        SubscriberCareReasons reason = SubscriberCareReasons.HELP;
        SubscriberCareDoc subscriberCareDoc = new SubscriberCareDoc(msisdn, reason, DateTime.now(), Channel.IVR);
        allSubscriberCareDocs.add(subscriberCareDoc);

        List<SubscriberCareDoc> byMsisdn = allSubscriberCareDocs.findByMsisdn(msisdn);

        Assert.assertEquals(subscriberCareDoc.getChannel(), byMsisdn.get(0).getChannel());
        Assert.assertEquals(subscriberCareDoc.getReason(), byMsisdn.get(0).getReason());
        Assert.assertEquals(subscriberCareDoc.getMsisdn(), byMsisdn.get(0).getMsisdn());
    }
}
