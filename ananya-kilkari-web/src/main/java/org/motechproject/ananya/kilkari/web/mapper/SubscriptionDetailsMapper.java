package org.motechproject.ananya.kilkari.web.mapper;

import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.service.request.Location;
import org.motechproject.ananya.kilkari.subscription.service.response.SubscriptionDetailsResponse;
import org.motechproject.ananya.kilkari.web.response.*;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionDetailsMapper {
    public static SubscriptionBaseWebResponse mapFrom(List<SubscriptionDetailsResponse> subscriptionDetailsResponses, Channel channel) {
        if (Channel.IVR.equals(channel))
            return mapToIvrResponse(subscriptionDetailsResponses);

        return mapToContactCenterResponse(subscriptionDetailsResponses);
    }

    private static SubscriptionBaseWebResponse mapToIvrResponse(List<SubscriptionDetailsResponse> subscriptionDetailsResponses) {
        SubscriptionIVRWebResponse ivrWebResponse = new SubscriptionIVRWebResponse();
        for (SubscriptionDetailsResponse detailsResponse : subscriptionDetailsResponses) {
            ivrWebResponse.addSubscriptionDetail(new SubscriptionDetails(detailsResponse.getSubscriptionId(), detailsResponse.getPack().name(), detailsResponse.getStatus().getDisplayString(), detailsResponse.getCampaignId()));
        }
        return ivrWebResponse;
    }

    private static SubscriptionBaseWebResponse mapToContactCenterResponse(List<SubscriptionDetailsResponse> subscriptionDetailsResponses) {
        SubscriptionCCWebResponse ccWebResponse = new SubscriptionCCWebResponse();
        for (SubscriptionDetailsResponse detailsResponse : subscriptionDetailsResponses) {
            Location locationDetails = detailsResponse.getLocation();
            LocationResponse location = locationDetails != null ? new LocationResponse(locationDetails.getState(), locationDetails.getDistrict(), locationDetails.getBlock(), locationDetails.getPanchayat()) : null;
            ccWebResponse.addSubscriptionDetail(new AllSubscriptionDetails(detailsResponse.getSubscriptionId(),
                    detailsResponse.getPack().name(),
                    detailsResponse.getStatus().getDisplayString(),
                    detailsResponse.getCampaignId(),
                    detailsResponse.getBeneficiaryName(),
                    detailsResponse.getBeneficiaryAge(),
                    detailsResponse.getStartWeekNumber(),
                    detailsResponse.getReferredBy(),
                    detailsResponse.getExpectedDateOfDelivery(),
                    detailsResponse.getDateOfBirth(),
                    location,
                    detailsResponse.getLastWeeklyMessageScheduledDate(),
                    detailsResponse.getLastUpdatedTimeForSubscription(),
                    detailsResponse.getLastUpdatedTimeForBeneficiary()));
        }
        return ccWebResponse;
    }
    
    public static SubscriptionResponseList mapToSubscriptionResponseList(List<Subscription> subscriptionList) {
        ArrayList<SubscriptionCCReferredByFlwResponse> subscriptionResponses = new ArrayList<>();
        for (Subscription subscription : subscriptionList) {
            
            subscriptionResponses.add(new 
            		SubscriptionCCReferredByFlwResponse(subscription.getMsisdn(),
            				subscription.getSubscriptionId(), 
            				subscription.getPack().toString(),
            				subscription.getCreationDate().toDate().toString(),
            				subscription.getCreationDate().toTimeOfDay().toString())
            );
        }
        return new SubscriptionResponseList(subscriptionResponses);
    }
}
