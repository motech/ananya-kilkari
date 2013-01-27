package org.motechproject.ananya.kilkari.obd.repository;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
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

        weekEndingDate1 = DateTime.now().withZone(DateTimeZone.UTC).plusDays(2);
        weekEndingDate2 = DateTime.now().withZone(DateTimeZone.UTC).minusDays(2);
        weekEndingDate3 = DateTime.now().withZone(DateTimeZone.UTC).minusDays(5);
        weekEndingDate4 = DateTime.now().withZone(DateTimeZone.UTC).minusDays(7);

        newCampaignMessage = new CampaignMessage("subscriptionId1", "messageId1", "1234657980", "operator1", weekEndingDate1);
        newCampaignMessageWithEarlierWeekEndingDate = new CampaignMessage("subscriptionId1", "messageId1", "1234657980", "operator1", weekEndingDate4);

        NDCampaignMessage = new CampaignMessage("subscriptionId2", "messageId2", "912134567890", "operator2", weekEndingDate2);
        NDCampaignMessage.setStatusCode(CampaignMessageStatus.ND);
        NDCampaignMessage.markSent();

        newCampaignMessageSent = new CampaignMessage("subscriptionId3", "messageId3", "3124567890", "operator3", weekEndingDate1);
        newCampaignMessageSent.markSent();

        NDCampaignMessageSent = new CampaignMessage("subscriptionId4", "messageId4", "004123567890", "operator4", weekEndingDate1);
        NDCampaignMessageSent.markSent();
        NDCampaignMessage.setStatusCode(CampaignMessageStatus.ND);

        NDCampaignMessageSent.markSent();

        NACampaignMessage = new CampaignMessage("subscriptionId5", "messageId5", "5123467890", "operator5", weekEndingDate2);
        NACampaignMessage.markSent();
        NACampaignMessage.setStatusCode(CampaignMessageStatus.NA);

        NACampaignMessageWithEarlierWeekEndingDate = new CampaignMessage("subscriptionId7", "messageId7", "5128467890", "operator7", weekEndingDate3);
        NACampaignMessageWithEarlierWeekEndingDate.setStatusCode(CampaignMessageStatus.NA);

        NACampaignMessageSent = new CampaignMessage("subscriptionId6", "messageId6", "6123457890", "operator6", weekEndingDate1);
        NACampaignMessageSent.markSent();
        NACampaignMessageSent.setStatusCode(CampaignMessageStatus.NA);
        NACampaignMessageSent.markSent();

        SOCampaignMessage = new CampaignMessage("subscriptionId7", "messageId7", "6123457890", "operator6", weekEndingDate1);
        SOCampaignMessage.setStatusCode(CampaignMessageStatus.SO);

        SOCampaignMessageSent = new CampaignMessage("subscriptionId7", "messageId7", "6123457890", "operator6", weekEndingDate1);
        SOCampaignMessage.setStatusCode(CampaignMessageStatus.SO);
        SOCampaignMessageSent.markSent();
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

        markForDeletion(newCampaignMessage);
        markForDeletion(newCampaignMessageWithEarlierWeekEndingDate);
        markForDeletion(newCampaignMessageSent);
        markForDeletion(NDCampaignMessage);
        markForDeletion(NDCampaignMessageSent);
        markForDeletion(NACampaignMessage);
        markForDeletion(NACampaignMessageSent);
        markForDeletion(SOCampaignMessage);
        markForDeletion(SOCampaignMessageSent);

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

        markForDeletion(newCampaignMessage);
        markForDeletion(newCampaignMessageSent);
        markForDeletion(NDCampaignMessage);
        markForDeletion(NDCampaignMessageSent);
        markForDeletion(NACampaignMessage);
        markForDeletion(NACampaignMessageSent);
        markForDeletion(NACampaignMessageWithEarlierWeekEndingDate);

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

        markForDeletion(newCampaignMessage);
        markForDeletion(newCampaignMessageSent);
        markForDeletion(newCampaignMessageWithEarlierWeekEndingDate);
        markForDeletion(NDCampaignMessage);
        markForDeletion(NDCampaignMessageSent);
        markForDeletion(NACampaignMessage);
        markForDeletion(NACampaignMessageSent);
        markForDeletion(NACampaignMessageWithEarlierWeekEndingDate);
        markForDeletion(SOCampaignMessage);
        markForDeletion(SOCampaignMessageSent);

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

        markForDeletion(newCampaignMessage);
        markForDeletion(newCampaignMessageSent);
        markForDeletion(newCampaignMessageWithEarlierWeekEndingDate);
        markForDeletion(NDCampaignMessage);
        markForDeletion(NDCampaignMessageSent);
        markForDeletion(NACampaignMessage);
        markForDeletion(NACampaignMessageSent);
        markForDeletion(NACampaignMessageWithEarlierWeekEndingDate);
        markForDeletion(SOCampaignMessage);
        markForDeletion(SOCampaignMessageSent);

        List<CampaignMessage> allUnsentMessages = allCampaignMessages.getAllUnsentMessages();

        assertEquals(6, allUnsentMessages.size());
        assertEquals(NACampaignMessageWithEarlierWeekEndingDate, allUnsentMessages.get(0));
        assertEquals(NACampaignMessage, allUnsentMessages.get(1));
        assertEquals(SOCampaignMessage, allUnsentMessages.get(2));
        assertEquals(NDCampaignMessage, allUnsentMessages.get(3));
        assertEquals(newCampaignMessageWithEarlierWeekEndingDate, allUnsentMessages.get(4));
        assertEquals(newCampaignMessage, allUnsentMessages.get(5));
    }
}
