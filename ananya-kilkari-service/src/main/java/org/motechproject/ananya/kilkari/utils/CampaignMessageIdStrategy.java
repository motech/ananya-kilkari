package org.motechproject.ananya.kilkari.utils;

import org.joda.time.DateTime;
import org.joda.time.Weeks;
import org.motechproject.ananya.kilkari.messagecampaign.service.MessageCampaignService;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class CampaignMessageIdStrategy {

	 private final Logger logger = LoggerFactory.getLogger(CampaignMessageIdStrategy.class);
	 
	private static final double WEEK_IN_MILIS = 7*24*60*60*1000;
	/*Added Below property for converting 1 min into week(1/10080)= 9.92063e-5; (this calculation is done for adding delta if the scheduler ran few miliseconds before 
	which resulted in wrong weeks calculation.)*/
	private static final double COMPARE_WITH_DELTA =  9.92063e-5; 


	private static final HashMap<String, String> CAMPAIGN_NAME_CODE_MAPPING = new HashMap<String, String>() {{
		put(MessageCampaignService.SIXTEEN_MONTHS_CAMPAIGN_KEY, "WEEK%s");
		put(MessageCampaignService.TWELVE_MONTHS_CAMPAIGN_KEY, "WEEK%s");
		put(MessageCampaignService.SEVEN_MONTHS_CAMPAIGN_KEY, "WEEK%s");
		put(MessageCampaignService.INFANT_DEATH_CAMPAIGN_KEY, "ID%s");
		put(MessageCampaignService.MISCARRIAGE_CAMPAIGN_KEY, "MC%s");
	}};

	public String createMessageId(String campaignName, DateTime campaignStartDate, SubscriptionPack pack) {
		logger.info("calculating messageId weeknumber for :"+campaignName+" campaignStartDate:"+campaignStartDate+" and pack:"+pack);
		int weekNumber = getWeekNumber(campaignStartDate, campaignName, pack);
		return String.format(CampaignMessageIdStrategy.CAMPAIGN_NAME_CODE_MAPPING.get(campaignName), weekNumber);
	}

	public int getWeekNumber(DateTime campaignStartDate, String campaignName, SubscriptionPack pack) {     	
		int weeksDifference = getWeeksElapsedAfterCampaignStartDate(campaignStartDate);
		return weeksDifference + getPackStartingWeek(campaignName, pack);
	}

	private int getPackStartingWeek(String campaignName, SubscriptionPack pack) {
		if (!campaignName.equals(MessageCampaignService.INFANT_DEATH_CAMPAIGN_KEY) &&
				!campaignName.equals(MessageCampaignService.MISCARRIAGE_CAMPAIGN_KEY))
			return pack.getStartWeek();
		return 1;
	}

	public int getWeeksElapsedAfterstartDate(DateTime startDate){
		return getWeeksElapsedAfterCampaignStartDate(startDate);
	}
	
	private int getWeeksElapsedAfterCampaignStartDate(DateTime campaignStartDate) {
		double exactWeekNumber = exactWeeksbetween(campaignStartDate, DateTime.now());
		double ceilValueOfExactWeekNumber = Math.ceil(exactWeekNumber);
		double diffBetween =	ceilValueOfExactWeekNumber - exactWeekNumber;

		if((diffBetween)<=(COMPARE_WITH_DELTA)){
			logger.info("scheduler has run delta time before actual start time. Applying workaround and got week number as:"+(int)( Math.ceil(exactWeekNumber)));
			return (int)( Math.ceil(exactWeekNumber));
		}else{
			logger.info("scheduler has run on time. Proceeding with normal flow.");
			return Weeks.weeksBetween(campaignStartDate, DateTime.now()).getWeeks();
		}
	}  

	private double exactWeeksbetween(DateTime start, DateTime end) {
		double exactWeekNumber = (end.getMillis()- start.getMillis())/WEEK_IN_MILIS;
		return exactWeekNumber;
	}

}
