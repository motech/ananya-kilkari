package org.motechproject.ananya.kilkari.mapper;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallRequest;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallRequestWrapper;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.DeactivationRequest;

import static junit.framework.Assert.assertEquals;

public class OBDRequestMapperTest {

    @Test
    public void shouldMapFromOBDRequestWrapper() {
        OBDSuccessfulCallRequestWrapper successfulCallRequestWrapper = new OBDSuccessfulCallRequestWrapper(new OBDSuccessfulCallRequest(), "1234567890", DateTime.now(), Channel.IVR);

        DeactivationRequest deactivationRequest = OBDRequestMapper.mapFrom(successfulCallRequestWrapper);

        assertEquals(successfulCallRequestWrapper.getSubscriptionId(), deactivationRequest.getSubscriptionId());
        assertEquals(successfulCallRequestWrapper.getChannel(), deactivationRequest.getChannel());
    }
}
