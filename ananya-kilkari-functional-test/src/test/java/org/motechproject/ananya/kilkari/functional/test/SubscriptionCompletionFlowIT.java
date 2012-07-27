package org.motechproject.ananya.kilkari.functional.test;

import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationKilkariFunctionalTestContext.xml")
@Ignore
public class SubscriptionCompletionFlowIT {
    @Autowired
    @Qualifier(value = "flowSystemBeanImpl")
    private FlowSystem flowSystem;

    @Test
    public void shouldSubscribeAndProgressAndCompleteSubscriptionSuccessfully() throws Exception {
        flowSystem.subscribe(new SubscriptionData("fifteen_months","ivr","9876543298")).activate().moveToFutureTime(DateTime.now().plusDays(2).plusHours(1));

    }


}
