package org.motechproject.ananya.kilkari.obd.scheduler;

import org.motechproject.ananya.kilkari.obd.service.CampaignMessageService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class NewMessagesSenderJob extends MessagesSenderJob {

    private static final String CRON_EXPRESSION_PROPERTY = "obd.new.messages.job.cron.expression";
    public static final String SEND_NEW_MESSAGES = "obd.send.new.messages";
    private static final Logger logger = LoggerFactory.getLogger(NewMessagesSenderJob.class);

    private CampaignMessageService campaignMessageService;

    @Autowired
    public NewMessagesSenderJob(CampaignMessageService campaignMessageService, Properties obdProperties) {
        super(NewMessagesSenderJob.SEND_NEW_MESSAGES, obdProperties.getProperty(NewMessagesSenderJob.CRON_EXPRESSION_PROPERTY));
        this.campaignMessageService = campaignMessageService;
    }

    @MotechListener(subjects = {NewMessagesSenderJob.SEND_NEW_MESSAGES})
    public void sendMessages(MotechEvent motechEvent) {
        logger.info("Handling send new messages event");
        campaignMessageService.sendNewMessages();

    }
}
