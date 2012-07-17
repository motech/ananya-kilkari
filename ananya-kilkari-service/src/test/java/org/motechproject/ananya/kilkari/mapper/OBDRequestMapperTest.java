package org.motechproject.ananya.kilkari.mapper;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.kilkari.request.OBDRequest;
import org.motechproject.ananya.kilkari.request.OBDRequestWrapper;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.DeactivationRequest;

import static junit.framework.Assert.assertEquals;

public class OBDRequestMapperTest {

    @Test
    public void shouldMapFromOBDRequestWrapper() {
        OBDRequestWrapper obdRequestWrapper = new OBDRequestWrapper(new OBDRequest(), "1234567890", DateTime.now(), Channel.IVR);

        DeactivationRequest deactivationRequest = OBDRequestMapper.mapFrom(obdRequestWrapper);

        assertEquals(obdRequestWrapper.getSubscriptionId(), deactivationRequest.getSubscriptionId());
        assertEquals(obdRequestWrapper.getChannel(), deactivationRequest.getChannel());
    }
}
