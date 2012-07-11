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
public class FreshMessagesSenderJob extends MessagesSenderJob {

    private static final String CRON_EXPRESSION_PROPERTY = "obd.fresh.messages.job.cron.expression";
    public static final String SEND_FRESH_MESSAGES = "obd.send.fresh.messages";
    private static final Logger logger = LoggerFactory.getLogger(FreshMessagesSenderJob.class);

    private CampaignMessageService campaignMessageService;

    @Autowired
    public FreshMessagesSenderJob(CampaignMessageService campaignMessageService, Properties obdProperties) {
        super(FreshMessagesSenderJob.SEND_FRESH_MESSAGES, obdProperties.getProperty(FreshMessagesSenderJob.CRON_EXPRESSION_PROPERTY));
        this.campaignMessageService = campaignMessageService;
    }

    @MotechListener(subjects = {FreshMessagesSenderJob.SEND_FRESH_MESSAGES})
    public void sendMessages(MotechEvent motechEvent) {
        logger.info("Handing send fresh messages event");
        campaignMessageService.sendFreshMessages();

    }
}
