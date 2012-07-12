package org.motechproject.ananya.kilkari.scheduler;

import org.motechproject.ananya.kilkari.service.CampaignMessageService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class RetryMessagesSenderJob extends MessagesSenderJob {

    private static final String CRON_EXPRESSION_PROPERTY = "obd.retry.messages.job.cron.expression";
    public static final String SEND_RETRY_MESSAGES = "obd.send.retry.messages";
    private static final Logger logger = LoggerFactory.getLogger(RetryMessagesSenderJob.class);
    private CampaignMessageService campaignMessageService;


    @Autowired
    public RetryMessagesSenderJob(CampaignMessageService campaignMessageService, Properties obdProperties) {
        super(RetryMessagesSenderJob.SEND_RETRY_MESSAGES, obdProperties.getProperty(RetryMessagesSenderJob.CRON_EXPRESSION_PROPERTY));
        this.campaignMessageService = campaignMessageService;
    }

    @MotechListener(subjects = {RetryMessagesSenderJob.SEND_RETRY_MESSAGES})
    public void sendMessages(MotechEvent motechEvent) {
        logger.info("Handling send retry messages event");
        campaignMessageService.sendRetryMessages();
    }
}
