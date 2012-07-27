package org.motechproject.ananya.kilkari.messagecampaign.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.kilkari.messagecampaign.request.MessageCampaignRequest;
import org.motechproject.ananya.kilkari.messagecampaign.domain.MessageCampaignPack;
import org.motechproject.server.messagecampaign.dao.AllCampaignEnrollments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationKilkariMessageCampaignContext.xml")
public class MessageCampaignServiceIT {

    public static final int CONFIGURED_DELTA_MINUTES = 30;
    public static final int CONFIGURED_DELTA_DAYS = 2;

    @Autowired
    private AllCampaignEnrollments allCampaignEnrollments;

    @Autowired
    private MessageCampaignService messageCampaignService;

    @Before
    @After
    public void setUp() {
        allCampaignEnrollments.removeAll();
    }

    @Test
    public void shouldCreateMessageCampaignForSevenMonthCampaign() {
        DateTime referenceDate = DateTime.now().plusDays(1);

        MessageCampaignRequest messageCampaignRequest = new MessageCampaignRequest(
                "my_id1", MessageCampaignPack.SEVEN_MONTHS.name(), referenceDate);

        messageCampaignService.start(messageCampaignRequest);

        List<DateTime> dateTimeList = messageCampaignService.getMessageTimings(
                messageCampaignRequest.getExternalId(),
                referenceDate.minusDays(2), referenceDate.plusYears(4));

        DateTime referenceDateWithDelta = referenceDate.plusDays(CONFIGURED_DELTA_DAYS);
        assertThat(dateTimeList.size(), is(28));
        assertEquals(referenceDateWithDelta.toLocalDate(), dateTimeList.get(0).toLocalDate());
        LocalTime deliverTime = new LocalTime(referenceDate.plusMinutes(CONFIGURED_DELTA_MINUTES).getHourOfDay(), referenceDate.plusMinutes(CONFIGURED_DELTA_MINUTES).getMinuteOfHour());
        assertEquals(deliverTime, dateTimeList.get(0).toLocalTime());
        assertEquals(referenceDateWithDelta.plusWeeks(27).toLocalDate(), dateTimeList.get(27).toLocalDate());
        assertEquals(deliverTime, dateTimeList.get(27).toLocalTime());
    }

    @Test
    public void shouldCreateMessageCampaignForTwelveMonthCampaign() {
        DateTime referenceDate = DateTime.now().plusDays(1);

        MessageCampaignRequest messageCampaignRequest = new MessageCampaignRequest(
                "my_id2", MessageCampaignPack.TWELVE_MONTHS.name(), referenceDate);

        messageCampaignService.start(messageCampaignRequest);

        List<DateTime> dateTimeList = messageCampaignService.getMessageTimings(
                messageCampaignRequest.getExternalId(),
                referenceDate.minusDays(2), referenceDate.plusYears(4));

        LocalDate referenceDateWithDelta = referenceDate.toLocalDate().plusDays(CONFIGURED_DELTA_DAYS);
        LocalTime deliverTime = new LocalTime(referenceDate.plusMinutes(CONFIGURED_DELTA_MINUTES).getHourOfDay(), referenceDate.plusMinutes(CONFIGURED_DELTA_MINUTES).getMinuteOfHour());
        assertThat(dateTimeList.size(), is(48));
        assertEquals(referenceDateWithDelta, dateTimeList.get(0).toLocalDate());
        assertEquals(deliverTime, dateTimeList.get(0).toLocalTime());

        assertEquals(referenceDateWithDelta.plusWeeks(47), dateTimeList.get(47).toLocalDate());
        assertEquals(deliverTime, dateTimeList.get(47).toLocalTime());
    }

    @Test
    public void shouldCreateMessageCampaignForFifteenMonthCampaign() {
        DateTime referenceDate = DateTime.now().plusDays(1);

        MessageCampaignRequest messageCampaignRequest = new MessageCampaignRequest(
                "my_id3", MessageCampaignPack.FIFTEEN_MONTHS.name(), referenceDate);

        messageCampaignService.start(messageCampaignRequest);

        List<DateTime> dateTimeList = messageCampaignService.getMessageTimings(
                messageCampaignRequest.getExternalId(),
                referenceDate.minusDays(2), referenceDate.plusYears(4));

        LocalDate referenceDateWithDelta = referenceDate.toLocalDate().plusDays(CONFIGURED_DELTA_DAYS);
        LocalTime deliverTime = new LocalTime(referenceDate.plusMinutes(CONFIGURED_DELTA_MINUTES).getHourOfDay(), referenceDate.plusMinutes(CONFIGURED_DELTA_MINUTES).getMinuteOfHour());
        assertThat(dateTimeList.size(), is(60));

        assertEquals(referenceDateWithDelta, dateTimeList.get(0).toLocalDate());
        assertEquals(deliverTime, dateTimeList.get(0).toLocalTime());

        assertEquals(referenceDateWithDelta.plusWeeks(59), dateTimeList.get(59).toLocalDate());
        assertEquals(deliverTime, dateTimeList.get(59).toLocalTime());
    }
}
