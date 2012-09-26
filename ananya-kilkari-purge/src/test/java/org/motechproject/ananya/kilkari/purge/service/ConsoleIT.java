package org.motechproject.ananya.kilkari.purge.service;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.kilkari.message.domain.CampaignMessageAlert;
import org.motechproject.ananya.kilkari.message.domain.InboxMessage;
import org.motechproject.ananya.kilkari.message.repository.AllCampaignMessageAlerts;
import org.motechproject.ananya.kilkari.message.repository.AllInboxMessages;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessage;
import org.motechproject.ananya.kilkari.obd.domain.InvalidCallRecord;
import org.motechproject.ananya.kilkari.obd.repository.AllCampaignMessages;
import org.motechproject.ananya.kilkari.obd.repository.AllInvalidCallRecords;
import org.motechproject.ananya.kilkari.purge.console.SubscriptionPurger;
import org.motechproject.ananya.kilkari.purge.exception.WrongNumberArgsException;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationKilkariPurgeContext.xml")
@ActiveProfiles("test")
public class ConsoleIT {

    @Autowired
    private AllSubscriptions allSubscriptions;
    @Autowired
    private AllCampaignMessages allCampaignMessages;
    @Autowired
    private AllInboxMessages allInboxMessages;
    @Autowired
    private AllCampaignMessageAlerts allCampaignMessageAlerts;
    @Autowired
    private AllInvalidCallRecords allInvalidCallRecords;

    private String msisdn;
    private String subscriptionId;
    private File file;

    @Before
    public void setUp() throws IOException {
        msisdn = "9" + RandomStringUtils.randomNumeric(9);
        insertDataIntoDb();
        file = new File("msisdn.txt");
        IOUtils.write(msisdn, new FileOutputStream(file));
    }

    @Test
    public void shouldPurgeAllDataForAnMsisdn() throws IOException, WrongNumberArgsException, InterruptedException {
        SubscriptionPurger.main(new String[]{"msisdn.txt"});
        assertTrue(allSubscriptions.findByMsisdn(msisdn).isEmpty());
        assertTrue(allCampaignMessages.findBySubscriptionId(subscriptionId).isEmpty());
        assertTrue(allInvalidCallRecords.findBySubscriptionId(subscriptionId).isEmpty());
        assertNull(allInboxMessages.findBySubscriptionId(subscriptionId));
        assertNull(allCampaignMessageAlerts.findBySubscriptionId(subscriptionId));
    }

    @After
    public void tearDown(){
        FileUtils.deleteQuietly(file);
    }

    private void insertDataIntoDb() {
        Subscription subscription = new Subscription(msisdn, SubscriptionPack.BARI_KILKARI, DateTime.now(), DateTime.now());
        String messageId = "WEEK3";
        subscriptionId = subscription.getSubscriptionId();
        allSubscriptions.add(subscription);
        allCampaignMessages.add(new CampaignMessage(subscriptionId, messageId, msisdn, messageId, DateTime.now()));
        allInboxMessages.add(new InboxMessage(subscriptionId, messageId));
        allCampaignMessageAlerts.add(new CampaignMessageAlert(subscriptionId, messageId,false,DateTime.now().plusDays(2)));
        allInvalidCallRecords.add(new InvalidCallRecord(msisdn,subscriptionId,"","",""));
    }
}
