package org.motechproject.ananya.kilkari.repository;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.kilkari.domain.SubscriberCareDoc;
import org.motechproject.ananya.kilkari.domain.SubscriberCareReasons;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static junit.framework.Assert.assertEquals;

public class AllSubscriberCareDocsIT extends SpringIntegrationTest{
    @Autowired
    private AllSubscriberCareDocs allSubscriberCareDocs;

    @Before
    public void setUp() {
        allSubscriberCareDocs.removeAll();
    }

    @Test
    public void shouldAddNewSubscriberCareDoc() {
        SubscriberCareDoc subscriberCareDoc = new SubscriberCareDoc("9876543211", SubscriberCareReasons.HELP.name(), DateTime.now(), Channel.IVR);
        allSubscriberCareDocs.add(subscriberCareDoc);
        markForDeletion(subscriberCareDoc);

        List<SubscriberCareDoc> subscriberCareDocs = allSubscriberCareDocs.getAll();
        assertEquals(1, subscriberCareDocs.size());
    }
}
