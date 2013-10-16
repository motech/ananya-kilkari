package org.motechproject.ananya.kilkari.subscription.service;

import java.util.Comparator;

import org.motechproject.ananya.kilkari.subscription.domain.Subscription;

public class SubscriptionComparator implements Comparator<Subscription> {

	public int compare(Subscription e1, Subscription e2) {
		
		if(e1.getCreationDate().equals(e2)){
			return Integer.parseInt(e1.getMsisdn())-Integer.parseInt(e2.getMsisdn());
		}
		return 1;
	}

}
