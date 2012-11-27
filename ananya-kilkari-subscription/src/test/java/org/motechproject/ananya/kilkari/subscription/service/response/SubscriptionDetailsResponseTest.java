package org.motechproject.ananya.kilkari.subscription.service.response;

import org.junit.Test;
import org.motechproject.ananya.kilkari.subscription.service.request.Location;
import org.motechproject.ananya.reports.kilkari.contract.response.LocationResponse;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class SubscriptionDetailsResponseTest {
    @Test
    public void shouldUpdateSubscriberDetailsWithLocation() {
        SubscriptionDetailsResponse subscriptionDetailsResponse = new SubscriptionDetailsResponse(null, null, null, null);
        String name = "name";
        String age = "23";
        Integer week = 34;
        String dob = "12-12-2000";
        String edd = "12-12-2012";
        String district = "dist";
        String block = "block";
        String panchayat = "panch";
        LocationResponse locationResponse = new LocationResponse(district, block, panchayat);
        Location expectedLocation = new Location(district, block, panchayat);

        subscriptionDetailsResponse.updateSubscriberDetails(name, age, week, dob, edd, locationResponse);

        assertEquals(name, subscriptionDetailsResponse.getBeneficiaryName());
        assertEquals(age, subscriptionDetailsResponse.getBeneficiaryAge());
        assertEquals(week, subscriptionDetailsResponse.getStartWeekNumber());
        assertEquals(dob, subscriptionDetailsResponse.getDateOfBirth());
        assertEquals(edd, subscriptionDetailsResponse.getExpectedDateOfDelivery());
        assertEquals(expectedLocation, subscriptionDetailsResponse.getLocation());
    }

    @Test
    public void shouldUpdateSubscriberDetailsWithoutLocation() {
        SubscriptionDetailsResponse subscriptionDetailsResponse = new SubscriptionDetailsResponse(null, null, null, null);

        subscriptionDetailsResponse.updateSubscriberDetails("name", "23", 34, "12-12-2000", "12-12-2012", null);

        assertNull(subscriptionDetailsResponse.getLocation());
    }
}
