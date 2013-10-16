package org.motechproject.ananya.kilkari.web.response;

import org.motechproject.export.annotation.ExportValue;

public class SubscriptionCCReferredByFlwResponse {

	private String msisdn;
	private String subscription_id;
	private String pack;
	private String date;
	private String time;

	public SubscriptionCCReferredByFlwResponse(String msisdn, String subscription_id,String pack, String date, String time) {
		this.msisdn = msisdn;
		this.subscription_id=subscription_id;
		this.pack=pack;
		this.date = date;
		this.time = time;
	}

	@ExportValue(column = "msisdn", index = 0)
	public String getMsisdn() {
		return msisdn;
	}

	@ExportValue(column = "subscription_id", index = 1)
	public String getSubscription_id() {
		return subscription_id;
	}

	@ExportValue(column = "pack", index = 2)
	public String getPack() {
		return pack;
	}

	@ExportValue(column = "date", index = 3)
	public String getDate() {
		return date;
	}

	@ExportValue(column = "time", index = 4)
	public String getTime() {
		return time;
	}
}
