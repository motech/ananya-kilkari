package org.motechproject.ananya.kilkari.subscription.service.response;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.kilkari.subscription.service.request.Location;

import static junit.framework.Assert.assertEquals;

public class SubscriptionDetailsResponseTest {
    @Test
    public void shouldUpdateSubscriberDetailsWithLocation() {
        Integer startWeekNumber = 4;
        SubscriptionDetailsResponse subscriptionDetailsResponse = new SubscriptionDetailsResponse(null, null, null, null);
        String name = "name";
        DateTime dob = DateTime.now();
        DateTime edd = DateTime.now().plusDays(2);
        Integer age = 20;
        String district = "dist";
        String block = "block";
        String panchayat = "panch";

        Location expectedLocation = new Location(district, block, panchayat);

        subscriptionDetailsResponse.updateSubscriberDetails(name, age, dob, edd, startWeekNumber, expectedLocation);

        assertEquals(name, subscriptionDetailsResponse.getBeneficiaryName());
        assertEquals(age, subscriptionDetailsResponse.getBeneficiaryAge());
        assertEquals(startWeekNumber, subscriptionDetailsResponse.getStartWeekNumber());
        assertEquals(dob.toString(), subscriptionDetailsResponse.getDateOfBirth());
        assertEquals(edd.toString(), subscriptionDetailsResponse.getExpectedDateOfDelivery());
        assertEquals(expectedLocation, subscriptionDetailsResponse.getLocation());
    }
}
