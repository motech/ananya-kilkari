package org.motechproject.ananya.kilkari.subscription.service.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.kilkari.message.service.InboxService;
import org.motechproject.ananya.kilkari.subscription.builder.SubscriptionBuilder;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.service.request.Location;
import org.motechproject.ananya.kilkari.subscription.service.response.SubscriptionDetailsResponse;
import org.motechproject.ananya.reports.kilkari.contract.response.LocationResponse;
import org.motechproject.ananya.reports.kilkari.contract.response.SubscriptionResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SubscriptionDetailsResponseMapperTest {
    @Mock
    private InboxService inboxService;
    private SubscriptionDetailsResponseMapper subscriptionDetailsResponseMapper;

    @Before
    public void setup() {
        subscriptionDetailsResponseMapper = new SubscriptionDetailsResponseMapper(inboxService);
    }

    @Test
    public void shouldMapSubscriptionToSubscriptionDetailResponseWhenReportsResponseIsEmpty() {
        Subscription subscription = new SubscriptionBuilder().withDefaults().build();
        ArrayList<Subscription> subscriptionList = new ArrayList<>();
        subscriptionList.add(subscription);
        String messageId = "week 13";
        when(inboxService.getMessageFor(subscription.getSubscriptionId())).thenReturn(messageId);

        List<SubscriptionDetailsResponse> responseList = subscriptionDetailsResponseMapper.map(subscriptionList, Collections.EMPTY_LIST);

        verify(inboxService).getMessageFor(subscription.getSubscriptionId());
        assertEquals(1, responseList.size());
        assertEquals(subscription.getSubscriptionId(), responseList.get(0).getSubscriptionId());
        assertEquals(subscription.getPack(), responseList.get(0).getPack());
        assertEquals(subscription.getStatus(), responseList.get(0).getStatus());
        assertEquals(messageId, responseList.get(0).getCampaignId());
        assertNull(responseList.get(0).getBeneficiaryName());
        assertNull(responseList.get(0).getBeneficiaryAge());
        assertNull(responseList.get(0).getStartWeekNumber());
        assertNull(responseList.get(0).getDateOfBirth());
        assertNull(responseList.get(0).getExpectedDateOfDelivery());
        assertNull(responseList.get(0).getLocation());
    }

    @Test
    public void shouldMapToAppropriateResponseWhenBothTransactionalAndReportsResponseIsPresent() {
        final String msisdn = "1234567890";
        String messageId1 = "messageId 1";
        String messageId2 = "messageId 2";
        LocationResponse location = new LocationResponse("d", "b", "p");
        final Subscription subscription1 = new SubscriptionBuilder().withDefaults().withMsisdn(msisdn).withPack(SubscriptionPack.BARI_KILKARI).build();
        final Subscription subscription2 = new SubscriptionBuilder().withDefaults().withMsisdn(msisdn).withPack(SubscriptionPack.NANHI_KILKARI).build();
        List<Subscription> subscriptionList = new ArrayList<Subscription>() {{
            add(subscription1);
            add(subscription2);
        }};
        final SubscriptionResponse subscriptionResponse1 = new SubscriptionResponse(Long.valueOf(msisdn), subscription1.getSubscriptionId(), subscription1.getPack().name(), "name 1", "ACTIVE", "23", "22", null, null, location, null);
        final SubscriptionResponse subscriptionResponse2 = new SubscriptionResponse(Long.valueOf(msisdn), subscription2.getSubscriptionId(), subscription2.getPack().name(), "name 2", "NEW", "34", "33", null, null, location, null);
        List<SubscriptionResponse> subscriberDetailsList = new ArrayList<SubscriptionResponse>() {{
            add(subscriptionResponse2);
            add(subscriptionResponse1);
        }};
        when(inboxService.getMessageFor(subscription1.getSubscriptionId())).thenReturn(messageId1);
        when(inboxService.getMessageFor(subscription2.getSubscriptionId())).thenReturn(messageId2);

        List<SubscriptionDetailsResponse> responseList = subscriptionDetailsResponseMapper.map(subscriptionList, subscriberDetailsList);

        verify(inboxService).getMessageFor(subscription1.getSubscriptionId());
        verify(inboxService).getMessageFor(subscription2.getSubscriptionId());
        assertEquals(2, responseList.size());
        assertResponse(messageId1, subscription1, subscriptionResponse1, responseList.get(0));
        assertResponse(messageId2, subscription2, subscriptionResponse2, responseList.get(1));
    }

    private void assertResponse(String messageId, Subscription subscription, SubscriptionResponse subscriptionResponse, SubscriptionDetailsResponse response) {
        LocationResponse actualLocation = subscriptionResponse.getLocation();
        Location expectedLocation = new Location(actualLocation.getDistrict(), actualLocation.getBlock(), actualLocation.getPanchayat());

        assertEquals(subscription.getSubscriptionId(), response.getSubscriptionId());
        assertEquals(subscription.getPack(), response.getPack());
        assertEquals(subscription.getStatus(), response.getStatus());
        assertEquals(messageId, response.getCampaignId());
        assertEquals(subscriptionResponse.getBeneficiaryName(), response.getBeneficiaryName());
        assertEquals(subscriptionResponse.getBeneficiaryAge(), response.getBeneficiaryAge());
        assertEquals(subscriptionResponse.getStartWeekNumber(), response.getStartWeekNumber());
        assertEquals(subscriptionResponse.getDateOfBirth(), response.getDateOfBirth());
        assertEquals(subscriptionResponse.getExpectedDateOfDelivery(), response.getExpectedDateOfDelivery());
        assertEquals(expectedLocation, response.getLocation());
    }

    @Test
    public void shouldReturnAnEmptyListIfBothResponsesAreEmpty()
    {
        List<SubscriptionDetailsResponse> responseList = subscriptionDetailsResponseMapper.map(Collections.EMPTY_LIST, Collections.EMPTY_LIST);

        assertNotNull(responseList);
        assertTrue(responseList.isEmpty());
    }



}
