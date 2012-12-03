package org.motechproject.ananya.kilkari.web.mapper;

import org.junit.Test;
import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;
import org.motechproject.ananya.kilkari.subscription.service.response.SubscriptionDetailsResponse;
import org.motechproject.ananya.kilkari.web.response.*;
import org.motechproject.ananya.reports.kilkari.contract.response.LocationResponse;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SubscriptionDetailsMapperTest {

    @Test
    public void shouldMapToIVRWebResponse() {
        SubscriptionDetailsResponse detailsResponse = new SubscriptionDetailsResponse(UUID.randomUUID().toString(), SubscriptionPack.BARI_KILKARI, SubscriptionStatus.ACTIVE, "WEEK13");
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
        LocationResponse actualLocation = new LocationResponse("d", "b", "p");
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

    private SubscriptionDetailsResponse setupData(LocationResponse actualLocation) {
        String subscriptionId = UUID.randomUUID().toString();
        SubscriptionPack pack = SubscriptionPack.BARI_KILKARI;
        SubscriptionStatus status = SubscriptionStatus.ACTIVE;
        String campaignId = "WEEK33";
        String name = "name";
        String age = "23";
        String week = "32";
        String dob = "dob";
        String edd = "edd";
        SubscriptionDetailsResponse detailsResponse = new SubscriptionDetailsResponse(subscriptionId, pack, status, campaignId);
        detailsResponse.updateSubscriberDetails(name, age, Integer.parseInt(week), dob, edd, actualLocation);
        return detailsResponse;
    }

    private void assertDetails(SubscriptionDetailsResponse detailsResponse, AllSubscriptionDetails subscriptionDetails) {
        assertEquals(detailsResponse.getSubscriptionId(), subscriptionDetails.getSubscriptionId());
        assertEquals(detailsResponse.getPack().name(), subscriptionDetails.getPack());
        assertEquals(detailsResponse.getStatus().getDisplayString(), subscriptionDetails.getStatus());
        assertEquals(detailsResponse.getCampaignId(), subscriptionDetails.getLastCampaignId());
        assertEquals(detailsResponse.getBeneficiaryName(), subscriptionDetails.getBeneficiaryName());
        assertEquals(detailsResponse.getBeneficiaryAge(), subscriptionDetails.getBeneficiaryAge());
        assertEquals(detailsResponse.getStartWeekNumber().toString(), subscriptionDetails.getWeekNumber());
        assertEquals(detailsResponse.getDateOfBirth(), subscriptionDetails.getDateOfBirth());
        assertEquals(detailsResponse.getExpectedDateOfDelivery(), subscriptionDetails.getExpectedDateOfDelivery());
    }
}
