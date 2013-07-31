package org.motechproject.ananya.kilkari.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.motechproject.ananya.kilkari.request.HelpWebRequest;
import org.motechproject.ananya.kilkari.service.validator.SubscriberCareRequestValidator;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriberCareDoc;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriberCareReasons;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.subscription.service.SubscriberCareService;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriberCareRequest;
import org.motechproject.ananya.kilkari.subscription.validators.DateUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class KilkariSubscriberCareServiceTest {
    @Mock
    private SubscriptionPublisher subscriptionPublisher;
    @Mock
    private SubscriberCareService subscriberCareService;

    private SubscriberCareRequestValidator careRequestValidator;
    private KilkariSubscriberCareService kilkariSubscriberCareService;

    @Before
    public void setUp() {
        initMocks(this);
        careRequestValidator = new SubscriberCareRequestValidator();
        kilkariSubscriberCareService = new KilkariSubscriberCareService(subscriberCareService, careRequestValidator, subscriptionPublisher);
    }

    @Test(expected = ValidationException.class)
    public void shouldThrowExceptionForInvalidMsisdnInSubscriberCareRequest() {
        SubscriberCareRequest subscriberCareRequest = new SubscriberCareRequest("12345", SubscriberCareReasons.HELP.name(), "ivr", DateTime.now());

        kilkariSubscriberCareService.createSubscriberCareRequest(subscriberCareRequest);
    }

    @Test
    public void shouldSaveValidSubscriberCareRequest() {
        String msisdn = "1234567890";
        SubscriberCareReasons reason = SubscriberCareReasons.HELP;
        SubscriberCareRequest subscriberCareRequest = new SubscriberCareRequest(msisdn, reason.name(), "ivr", DateTime.now());

        kilkariSubscriberCareService.createSubscriberCareRequest(subscriberCareRequest);

        ArgumentCaptor<SubscriberCareRequest> subscriberCareDocArgumentCaptor = ArgumentCaptor.forClass(SubscriberCareRequest.class);
        verify(subscriberCareService).create(subscriberCareDocArgumentCaptor.capture());
        SubscriberCareRequest actualSubscriberCareRequest = subscriberCareDocArgumentCaptor.getValue();

        assertEquals(msisdn, actualSubscriberCareRequest.getMsisdn());
        assertEquals(reason.toString(), actualSubscriberCareRequest.getReason());
    }

    @Test
    public void shouldFetchSubscriberCareDocsInTheGivenDateRange() {
        List<SubscriberCareDoc> expectedSubscriberCareDocs = new ArrayList<>();
        expectedSubscriberCareDocs.add(new SubscriberCareDoc("msisdn1", SubscriberCareReasons.HELP, DateTime.now(), Channel.IVR));
        String fromDate = "01-01-2012 01:01:01";
        String toDate = "02-02-2012 02:02:02";
        DateTime startDate = DateUtils.parseDateTimeForCC(fromDate);
        DateTime endDate = DateUtils.parseDateTimeForCC(toDate);
        when(subscriberCareService.getAllSortedByDate(startDate, endDate)).thenReturn(expectedSubscriberCareDocs);

        List<SubscriberCareDoc> actualSubscriberCareDocs = kilkariSubscriberCareService.fetchSubscriberCareDocs(new HelpWebRequest(fromDate, toDate, Channel.CONTACT_CENTER.name()));

        assertEquals(expectedSubscriberCareDocs, actualSubscriberCareDocs);
    }
}
