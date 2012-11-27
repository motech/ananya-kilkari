package org.motechproject.ananya.kilkari.web.mapper;

import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.service.request.Location;
import org.motechproject.ananya.kilkari.subscription.service.response.SubscriptionDetailsResponse;
import org.motechproject.ananya.kilkari.web.response.AllSubscriptionDetails;
import org.motechproject.ananya.kilkari.web.response.LocationResponse;
import org.motechproject.ananya.kilkari.web.response.SubscriptionDetails;
import org.motechproject.ananya.kilkari.web.response.SubscriptionWebResponse;

import java.util.List;

public class SubscriptionDetailsMapper {
    public static SubscriptionWebResponse mapFrom(List<SubscriptionDetailsResponse> subscriptionDetailsResponses, Channel channel) {
        SubscriptionWebResponse webResponse = new SubscriptionWebResponse();
        for (SubscriptionDetailsResponse detailsResponse : subscriptionDetailsResponses) {
            if (Channel.IVR.equals(channel))
                webResponse.addSubscriptionDetail(new SubscriptionDetails(detailsResponse.getSubscriptionId(), detailsResponse.getPack().name(), detailsResponse.getStatus().name(), detailsResponse.getCampaignId()));
            else
                mapContactCenterResponse(webResponse, detailsResponse);
        }
        return webResponse;
    }

    private static void mapContactCenterResponse(SubscriptionWebResponse webResponse, SubscriptionDetailsResponse detailsResponse) {
        Location locationDetails = detailsResponse.getLocation();
        LocationResponse location = locationDetails != null ? new LocationResponse(locationDetails.getDistrict(), locationDetails.getBlock(), locationDetails.getPanchayat()) : null;
        webResponse.addSubscriptionDetail(new AllSubscriptionDetails(detailsResponse.getSubscriptionId(), detailsResponse.getPack().name(), detailsResponse.getStatus().name(), detailsResponse.getCampaignId(),
                detailsResponse.getBeneficiaryName(), detailsResponse.getBeneficiaryAge(), detailsResponse.weekNumber(), detailsResponse.getExpectedDateOfDelivery(), detailsResponse.getDateOfBirth(), location));
    }
}
