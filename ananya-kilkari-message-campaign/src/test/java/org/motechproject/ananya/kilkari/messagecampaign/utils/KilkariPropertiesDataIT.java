package org.motechproject.ananya.kilkari.messagecampaign.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationKilkariMessageCampaignContext.xml")
public class KilkariPropertiesDataIT {

    @Autowired
    private KilkariPropertiesData kilkariProperties;
    @Test
    public void shouldBeAbleToLoadThePropertiesFromTheKilkariPropertiesFile(){
        assertEquals(3,kilkariProperties.getBufferDaysToAllowRenewalForPackCompletion());
        assertEquals(2,kilkariProperties.getCampaignScheduleDeltaDays());
        assertEquals(30,kilkariProperties.getCampaignScheduleDeltaMinutes());

    }

}
