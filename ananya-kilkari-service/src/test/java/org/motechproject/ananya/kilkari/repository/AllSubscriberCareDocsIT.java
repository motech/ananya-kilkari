package org.motechproject.ananya.kilkari.repository;

import org.junit.Test;
import org.motechproject.ananya.kilkari.domain.SubscriberCareDoc;
import org.motechproject.ananya.kilkari.domain.SubscriberCareReasons;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static junit.framework.Assert.assertEquals;

public class AllSubscriberCareDocsIT extends SpringIntegrationTest{
    @Autowired
    private AllSubscriberCareDocs allSubscriberCareDocs;

    @Test
    public void shouldAddNewSubscriberCareDoc() {
        SubscriberCareDoc subscriberCareDoc = new SubscriberCareDoc("9876543211", SubscriberCareReasons.CHANGE_PACK.name());
        allSubscriberCareDocs.add(subscriberCareDoc);
        markForDeletion(subscriberCareDoc);

        List<SubscriberCareDoc> subscriberCareDocs = allSubscriberCareDocs.getAll();
        assertEquals(1, subscriberCareDocs.size());
    }
}
