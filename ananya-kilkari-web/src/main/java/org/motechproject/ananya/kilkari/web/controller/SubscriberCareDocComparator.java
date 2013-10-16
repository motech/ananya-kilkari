package org.motechproject.ananya.kilkari.web.controller;

import java.util.Comparator;

import org.motechproject.ananya.kilkari.subscription.domain.SubscriberCareDoc;

public class SubscriberCareDocComparator implements Comparator<SubscriberCareDoc> {

	public int compare(SubscriberCareDoc e1, SubscriberCareDoc e2) {

		if(e1.getMsisdn().equalsIgnoreCase(e2.getMsisdn())
				&&e1.getReason().equals(e2.getReason())
				&&e1.getChannel().equals(e2.getChannel())
				&&e1.getCreatedAt().getYear()==e2.getCreatedAt().getYear()
				&&e1.getCreatedAt().getDayOfYear()==e2.getCreatedAt().getDayOfYear()
				){
			return 0;
		}
		return 1;
	}
}
