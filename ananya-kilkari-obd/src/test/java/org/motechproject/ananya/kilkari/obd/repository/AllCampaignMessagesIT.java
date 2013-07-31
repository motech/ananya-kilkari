package org.motechproject.ananya.kilkari.obd.repository;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessage;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessageStatus;
import org.motechproject.ananya.kilkari.obd.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static junit.framework.Assert.assertEquals;

public class AllCampaignMessagesIT extends SpringIntegrationTest {

    @Autowired
    private AllCampaignMessages allCampaignMessages;
    private CampaignMessage newCampaignMessage;
    private CampaignMessage newCampaignMessageWithEarlierWeekEndingDate;
    private CampaignMessage NDCampaignMessage;
    private CampaignMessage newCampaignMessageSent;
    private CampaignMessage NDCampaignMessageSent;
    private CampaignMessage NACampaignMessage;
    private CampaignMessage NACampaignMessageSent;
    private CampaignMessage NACampaignMessageWithEarlierWeekEndingDate;
    private CampaignMessage SOCampaignMessage;
    private CampaignMessage SOCampaignMessageSent;
    private DateTime weekEndingDate1;
    private DateTime weekEndingDate2;
    private DateTime weekEndingDate3;
    private DateTime weekEndingDate4;

    @Before
    public void setUp() {
        allCampaignMessages.removeAll();

        DateTime now = DateTime.now().withZone(DateTimeZone.UTC);
        weekEndingDate1 = now.plusDays(2);
        weekEndingDate2 = now.minusDays(2);
        weekEndingDate3 = now.minusDays(5);
        weekEndingDate4 = now.minusDays(7);

        newCampaignMessage = new CampaignMessage("subscriptionId1", "messageId1", now, "1234657980", "operator1", weekEndingDate1);
        newCampaignMessageWithEarlierWeekEndingDate = new CampaignMessage("subscriptionId1", "messageId1", now, "1234657980", "operator1", weekEndingDate4);

        NDCampaignMessage = new CampaignMessage("subscriptionId2", "messageId2", now, "912134567890", "operator2", weekEndingDate2);
        NDCampaignMessage.setFailureStatusCode(CampaignMessageStatus.ND);
        NDCampaignMessage.markSent();

        newCampaignMessageSent = new CampaignMessage("subscriptionId3", "messageId3", now, "3124567890", "operator3", weekEndingDate1);
        newCampaignMessageSent.markSent();

        NDCampaignMessageSent = new CampaignMessage("subscriptionId4", "messageId4", now, "004123567890", "operator4", weekEndingDate1);
        NDCampaignMessageSent.markSent();
        NDCampaignMessage.setFailureStatusCode(CampaignMessageStatus.ND);

        NDCampaignMessageSent.markSent();

        NACampaignMessage = new CampaignMessage("subscriptionId5", "messageId5", now, "5123467890", "operator5", weekEndingDate2);
        NACampaignMessage.markSent();
        NACampaignMessage.setFailureStatusCode(CampaignMessageStatus.NA);

        NACampaignMessageWithEarlierWeekEndingDate = new CampaignMessage("subscriptionId7", "messageId7", now, "5128467890", "operator7", weekEndingDate3);
        NACampaignMessageWithEarlierWeekEndingDate.setFailureStatusCode(CampaignMessageStatus.NA);

        NACampaignMessageSent = new CampaignMessage("subscriptionId6", "messageId6", now, "6123457890", "operator6", weekEndingDate1);
        NACampaignMessageSent.markSent();
        NACampaignMessageSent.setFailureStatusCode(CampaignMessageStatus.NA);
        NACampaignMessageSent.markSent();

        SOCampaignMessage = new CampaignMessage("subscriptionId7", "messageId7", now, "6123457890", "operator6", weekEndingDate1);
        SOCampaignMessage.setFailureStatusCode(CampaignMessageStatus.SO);

        SOCampaignMessageSent = new CampaignMessage("subscriptionId7", "messageId7", now, "6123457890", "operator6", weekEndingDate1);
        SOCampaignMessage.setFailureStatusCode(CampaignMessageStatus.SO);
        SOCampaignMessageSent.markSent();
    }

    @After
    public void tearDown() {
        allCampaignMessages.removeAll();
    }

    @Test
    public void shouldReturnAllTheUnsentNewMessagesInOrderOfViewKeys() {
        obdDbConnector.create(newCampaignMessage);
        obdDbConnector.create(newCampaignMessageWithEarlierWeekEndingDate);
        obdDbConnector.create(newCampaignMessageSent);
        obdDbConnector.create(NDCampaignMessage);
        obdDbConnector.create(NDCampaignMessageSent);
        obdDbConnector.create(NACampaignMessage);
        obdDbConnector.create(NACampaignMessageSent);
        obdDbConnector.create(SOCampaignMessage);
        obdDbConnector.create(SOCampaignMessageSent);

        List<CampaignMessage> allNewMessages = allCampaignMessages.getAllUnsentNewMessages();

        assertEquals(2, allNewMessages.size());
        assertEquals(newCampaignMessageWithEarlierWeekEndingDate, allNewMessages.get(0));
        assertEquals(newCampaignMessage, allNewMessages.get(1));
    }

    @Test
    public void shouldReturnAllTheUnsentNAMessagesInOrderOfViewKeys() {
        obdDbConnector.create(newCampaignMessage);
        obdDbConnector.create(newCampaignMessageSent);
        obdDbConnector.create(NDCampaignMessage);
        obdDbConnector.create(NDCampaignMessageSent);
        obdDbConnector.create(NACampaignMessage);
        obdDbConnector.create(NACampaignMessageSent);
        obdDbConnector.create(NACampaignMessageWithEarlierWeekEndingDate);

        List<CampaignMessage> allRetryMessages = allCampaignMessages.getAllUnsentNAMessages();

        assertEquals(2, allRetryMessages.size());
        assertEquals(NACampaignMessageWithEarlierWeekEndingDate, allRetryMessages.get(0));
        assertEquals(NACampaignMessage, allRetryMessages.get(1));
    }

    @Test
    public void shouldReturnAllTheUnsentNewAndNAMessagesInOrderOfViewKeysAndStatus() {
        obdDbConnector.create(newCampaignMessage);
        obdDbConnector.create(newCampaignMessageSent);
        obdDbConnector.create(newCampaignMessageWithEarlierWeekEndingDate);
        obdDbConnector.create(NDCampaignMessage);
        obdDbConnector.create(NDCampaignMessageSent);
        obdDbConnector.create(NACampaignMessage);
        obdDbConnector.create(NACampaignMessageSent);
        obdDbConnector.create(NACampaignMessageWithEarlierWeekEndingDate);
        obdDbConnector.create(SOCampaignMessage);
        obdDbConnector.create(SOCampaignMessageSent);

        List<CampaignMessage> allNewAndNAMessages = allCampaignMessages.getAllUnsentNewAndNAMessages();

        assertEquals(4, allNewAndNAMessages.size());
        assertEquals(NACampaignMessageWithEarlierWeekEndingDate, allNewAndNAMessages.get(0));
        assertEquals(NACampaignMessage, allNewAndNAMessages.get(1));
        assertEquals(newCampaignMessageWithEarlierWeekEndingDate, allNewAndNAMessages.get(2));
        assertEquals(newCampaignMessage, allNewAndNAMessages.get(3));
    }

    @Test
    public void shouldReturnAllTheUnsentMessagesInOrderOfViewKeysAndStatus() {
        obdDbConnector.create(newCampaignMessage);
        obdDbConnector.create(newCampaignMessageSent);
        obdDbConnector.create(newCampaignMessageWithEarlierWeekEndingDate);
        obdDbConnector.create(NACampaignMessage);
        obdDbConnector.create(NACampaignMessageSent);
        obdDbConnector.create(NACampaignMessageWithEarlierWeekEndingDate);
        obdDbConnector.create(NDCampaignMessage);
        obdDbConnector.create(NDCampaignMessageSent);
        obdDbConnector.create(SOCampaignMessage);
        obdDbConnector.create(SOCampaignMessageSent);

        List<CampaignMessage> allUnsentMessages = allCampaignMessages.getAllUnsentRetryMessages();

        assertEquals(4, allUnsentMessages.size());
        assertEquals(NACampaignMessageWithEarlierWeekEndingDate, allUnsentMessages.get(0));
        assertEquals(NACampaignMessage, allUnsentMessages.get(1));
        assertEquals(SOCampaignMessage, allUnsentMessages.get(2));
        assertEquals(NDCampaignMessage, allUnsentMessages.get(3));
    }
}
