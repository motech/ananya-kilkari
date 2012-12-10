package org.motechproject.ananya.kilkari.web.mapper;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriberCareDoc;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriberCareReasons;
import org.motechproject.ananya.kilkari.web.response.*;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class SubscriberCareDocsResponseMapperTest {
    @Test
    public void shouldMapFromSubscriberCareDocsToSubscriberCareDocsResponseList() {
        ArrayList<SubscriberCareDoc> subscriberCareDocs = new ArrayList<>();
        SubscriberCareDoc subscriberCareDoc1 = new SubscriberCareDoc("msisdn1", SubscriberCareReasons.HELP, DateTime.now(), Channel.IVR);
        SubscriberCareDoc subscriberCareDoc2 = new SubscriberCareDoc("msisdn2", SubscriberCareReasons.HELP, DateTime.now().minusHours(5), Channel.IVR);
        subscriberCareDocs.add(subscriberCareDoc2);
        subscriberCareDocs.add(subscriberCareDoc1);

        SubscriberCareDocResponseList subscriberCareDocResponseList = SubscriberCareDocsResponseMapper.mapToSubscriberDocsResponseList(subscriberCareDocs);

        assertEquals(2, subscriberCareDocResponseList.size());
        assertSubscriberCareDocsResponse(subscriberCareDoc2, subscriberCareDocResponseList.get(0));
        assertSubscriberCareDocsResponse(subscriberCareDoc1, subscriberCareDocResponseList.get(1));
    }

    private void assertSubscriberCareDocsResponse(SubscriberCareDoc expectedSubscriberCareDoc, SubscriberCareDocResponse actualSubscriberCareDoc) {
        assertEquals(expectedSubscriberCareDoc.getMsisdn(), actualSubscriberCareDoc.getMsisdn());
        assertEquals(expectedSubscriberCareDoc.getCreatedAt().toString("HH:mm:ss"), actualSubscriberCareDoc.getTime());
        assertEquals(expectedSubscriberCareDoc.getCreatedAt().toString("dd-MM-yyyy"), actualSubscriberCareDoc.getDate());
    }
}
