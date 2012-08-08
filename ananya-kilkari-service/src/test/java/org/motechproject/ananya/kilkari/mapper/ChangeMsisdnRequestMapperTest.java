package org.motechproject.ananya.kilkari.mapper;

import org.junit.Test;
import org.motechproject.ananya.kilkari.request.ChangeMsisdnWebRequest;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.service.request.ChangeMsisdnRequest;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class ChangeMsisdnRequestMapperTest {

    @Test
    public void shouldMapRequestWithALLAsPack() {
        String oldMsisdn = "9876543210";
        String newMsisdn = "9876543211";
        Channel channel = Channel.CALL_CENTER;

        ChangeMsisdnRequest mappedRequest = ChangeMsisdnRequestMapper.mapFrom(new ChangeMsisdnWebRequest(oldMsisdn, newMsisdn, Arrays.asList("alL"), channel.toString()));

        assertEquals(oldMsisdn, mappedRequest.getOldMsisdn());
        assertEquals(newMsisdn, mappedRequest.getNewMsisdn());
        assertEquals(channel, mappedRequest.getChannel());
        assertEquals(true, mappedRequest.getShouldChangeAllPacks());
    }

    @Test
    public void shouldMapRequestWithPacks() {
        String oldMsisdn = "9876543210";
        String newMsisdn = "9876543211";
        Channel channel = Channel.CALL_CENTER;

        ChangeMsisdnRequest mappedRequest = ChangeMsisdnRequestMapper.mapFrom(new ChangeMsisdnWebRequest(oldMsisdn, newMsisdn, Arrays.asList("NANHI_KILKARI", "choti_kilkari"), channel.toString()));

        assertEquals(oldMsisdn, mappedRequest.getOldMsisdn());
        assertEquals(newMsisdn, mappedRequest.getNewMsisdn());
        assertEquals(channel, mappedRequest.getChannel());
        assertEquals(false, mappedRequest.getShouldChangeAllPacks());
        assertTrue(mappedRequest.getPacks().contains(SubscriptionPack.NANHI_KILKARI));
        assertTrue(mappedRequest.getPacks().contains(SubscriptionPack.CHOTI_KILKARI));
    }

}
