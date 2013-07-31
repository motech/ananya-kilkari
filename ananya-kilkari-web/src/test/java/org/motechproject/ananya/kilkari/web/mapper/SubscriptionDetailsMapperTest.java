package org.motechproject.ananya.kilkari.web.mapper;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;
import org.motechproject.ananya.kilkari.subscription.service.request.Location;
import org.motechproject.ananya.kilkari.subscription.service.response.SubscriptionDetailsResponse;
import org.motechproject.ananya.kilkari.web.response.*;

import java.util.ArrayList;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SubscriptionDetailsMapperTest {

    @Test
    public void shouldMapToIVRWebResponse() {
        SubscriptionDetailsResponse detailsResponse = new SubscriptionDetailsResponse(UUID.randomUUID().toString(), SubscriptionPack.BARI_KILKARI, SubscriptionStatus.ACTIVE, "WEEK13", null);
        ArrayList<SubscriptionDetailsResponse> responseList = new ArrayList<>();
        responseList.add(detailsResponse);

        SubscriptionBaseWebResponse webResponse = SubscriptionDetailsMapper.mapFrom(responseList, Channel.IVR);

        SubscriptionIVRWebResponse ivrWebResponse = (SubscriptionIVRWebResponse) webResponse;
        assertEquals(1, ivrWebResponse.getSubscriptionDetails().size());
        SubscriptionDetails subscriptionDetails = ivrWebResponse.getSubscriptionDetails().get(0);
        assertEquals(detailsResponse.getSubscriptionId(), subscriptionDetails.getSubscriptionId());
        assertEquals(detailsResponse.getPack().name(), subscriptionDetails.getPack());
        assertEquals(detailsResponse.getStatus().getDisplayString(), subscriptionDetails.getStatus());
        assertEquals(detailsResponse.getCampaignId(), subscriptionDetails.getLastCampaignId());
    }

    @Test
    public void shouldMapToCCWebResponseWithLocation() {
        Location actualLocation = new Location("d", "b", "p");
        SubscriptionDetailsResponse detailsResponse = setupData(actualLocation);
        ArrayList<SubscriptionDetailsResponse> responseList = new ArrayList<>();
        responseList.add(detailsResponse);

        SubscriptionBaseWebResponse webResponse = SubscriptionDetailsMapper.mapFrom(responseList, Channel.CONTACT_CENTER);

        SubscriptionCCWebResponse ccWebResponse = (SubscriptionCCWebResponse) webResponse;
        assertEquals(1, ccWebResponse.getSubscriptionDetails().size());
        AllSubscriptionDetails subscriptionDetails = ccWebResponse.getSubscriptionDetails().get(0);
        assertDetails(detailsResponse, subscriptionDetails);
        assertEquals(actualLocation.getDistrict(), subscriptionDetails.getLocation().getDistrict());
        assertEquals(actualLocation.getBlock(), subscriptionDetails.getLocation().getBlock());
        assertEquals(actualLocation.getPanchayat(), subscriptionDetails.getLocation().getPanchayat());
    }

    @Test
    public void shouldMapToCCWebResponseWithoutLocation() {
        SubscriptionDetailsResponse detailsResponse = setupData(null);
        ArrayList<SubscriptionDetailsResponse> responseList = new ArrayList<>();
        responseList.add(detailsResponse);

        SubscriptionBaseWebResponse webResponse = SubscriptionDetailsMapper.mapFrom(responseList, Channel.CONTACT_CENTER);

        SubscriptionCCWebResponse ccWebResponse = (SubscriptionCCWebResponse) webResponse;
        assertEquals(1, ccWebResponse.getSubscriptionDetails().size());
        AllSubscriptionDetails subscriptionDetails = ccWebResponse.getSubscriptionDetails().get(0);
        assertDetails(detailsResponse, subscriptionDetails);
        assertNull(subscriptionDetails.getLocation());
    }

    private SubscriptionDetailsResponse setupData(Location actualLocation) {
        return new SubscriptionDetailsResponse(UUID.randomUUID().toString(), SubscriptionPack.BARI_KILKARI, SubscriptionStatus.ACTIVE, "WEEK33",
                "name", 10, DateTime.now(), DateTime.now(), 4, actualLocation, DateTime.now(), DateTime.now(), DateTime.now().minusDays(8), null);
    }

    private void assertDetails(SubscriptionDetailsResponse detailsResponse, AllSubscriptionDetails subscriptionDetails) {
        assertEquals(detailsResponse.getSubscriptionId(), subscriptionDetails.getSubscriptionId());
        assertEquals(detailsResponse.getPack().name(), subscriptionDetails.getPack());
        assertEquals(detailsResponse.getStatus().getDisplayString(), subscriptionDetails.getStatus());
        assertEquals(detailsResponse.getCampaignId(), subscriptionDetails.getLastCampaignId());
        assertEquals(detailsResponse.getBeneficiaryName(), subscriptionDetails.getBeneficiaryName());
        assertEquals(detailsResponse.getBeneficiaryAge(), subscriptionDetails.getBeneficiaryAge());
        assertEquals(detailsResponse.getStartWeekNumber(), subscriptionDetails.getWeekNumber());
        assertEquals(detailsResponse.getDateOfBirth(), subscriptionDetails.getDateOfBirth());
        assertEquals(detailsResponse.getExpectedDateOfDelivery(), subscriptionDetails.getExpectedDateOfDelivery());
        assertEquals(detailsResponse.getLastWeeklyMessageScheduledDate(), subscriptionDetails.getLastWeeklyMessageScheduledDate());
        assertEquals(detailsResponse.getLastUpdatedTimeForSubscription(), subscriptionDetails.getLastUpdatedTimeForSubscription());
        assertEquals(detailsResponse.getLastUpdatedTimeForBeneficiary(), subscriptionDetails.getLastUpdatedTimeForBeneficiary());
    }
}
