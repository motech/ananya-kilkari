package org.motechproject.ananya.kilkari.messagecampaign.service;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.kilkari.messagecampaign.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.messagecampaign.request.KilkariMessageCampaignRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationKilkariMessageCampaignContext.xml")
public class KilkariMessageCampaignServiceIT {

    @Autowired
    private KilkariMessageCampaignService kilkariMessageCampaignService;

    @Test
    public void shouldCreateMessageCampaignForSevenMonthCampaign() {
        DateTime referenceDate = DateTime.now().plusDays(1);

        KilkariMessageCampaignRequest messageCampaignRequest = new KilkariMessageCampaignRequest(
                "my_id", SubscriptionPack.SEVEN_MONTHS.name(), referenceDate);

        kilkariMessageCampaignService.start(messageCampaignRequest);

        List<DateTime> dateTimeList = kilkariMessageCampaignService.getMessageTimings(
                messageCampaignRequest.getExternalId(),
                SubscriptionPack.SEVEN_MONTHS.name(),
                referenceDate.minusDays(2), referenceDate.plusYears(4));

        assertThat(dateTimeList.size(), is(28));
        assertEquals(referenceDate.toLocalDate(), dateTimeList.get(0).toLocalDate());
        assertEquals(new LocalTime(13,0), dateTimeList.get(0).toLocalTime());
        assertEquals(referenceDate.toLocalDate().plusWeeks(27), dateTimeList.get(27).toLocalDate());
        assertEquals(new LocalTime(13,0), dateTimeList.get(27).toLocalTime());
    }

    @Test
    public void shouldCreateMessageCampaignForTwelveMonthCampaign() {
        DateTime referenceDate = DateTime.now().plusDays(1);

        KilkariMessageCampaignRequest messageCampaignRequest = new KilkariMessageCampaignRequest(
                "my_id", SubscriptionPack.TWELVE_MONTHS.name(), referenceDate);

        kilkariMessageCampaignService.start(messageCampaignRequest);

        List<DateTime> dateTimeList = kilkariMessageCampaignService.getMessageTimings(
                messageCampaignRequest.getExternalId(),
                SubscriptionPack.TWELVE_MONTHS.name(),
                referenceDate.minusDays(2), referenceDate.plusYears(4));

        assertThat(dateTimeList.size(), is(48));
        assertEquals(referenceDate.toLocalDate(), dateTimeList.get(0).toLocalDate());
        assertEquals(new LocalTime(13,0), dateTimeList.get(0).toLocalTime());
        assertEquals(referenceDate.toLocalDate().plusWeeks(47), dateTimeList.get(47).toLocalDate());
        assertEquals(new LocalTime(13,0), dateTimeList.get(47).toLocalTime());
    }

    @Test
    public void shouldCreateMessageCampaignForFifteenMonthCampaign() {
        DateTime referenceDate = DateTime.now().plusDays(1);

        KilkariMessageCampaignRequest messageCampaignRequest = new KilkariMessageCampaignRequest(
                "my_id", SubscriptionPack.FIFTEEN_MONTHS.name(), referenceDate);

        kilkariMessageCampaignService.start(messageCampaignRequest);

        List<DateTime> dateTimeList = kilkariMessageCampaignService.getMessageTimings(
                messageCampaignRequest.getExternalId(),
                SubscriptionPack.FIFTEEN_MONTHS.name(),
                referenceDate.minusDays(2), referenceDate.plusYears(4));

        assertThat(dateTimeList.size(), is(60));
        assertEquals(referenceDate.toLocalDate(), dateTimeList.get(0).toLocalDate());
        assertEquals(new LocalTime(13,0), dateTimeList.get(0).toLocalTime());
        assertEquals(referenceDate.toLocalDate().plusWeeks(59), dateTimeList.get(59).toLocalDate());
        assertEquals(new LocalTime(13,0), dateTimeList.get(59).toLocalTime());
    }
}
