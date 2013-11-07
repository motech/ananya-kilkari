package org.motechproject.ananya.kilkari.web.controller;

import java.util.Comparator;

import org.motechproject.ananya.kilkari.subscription.domain.Subscription;

public class SubscriptionListComparator implements Comparator<Subscription> {

	@Override
	public int compare(Subscription s1, Subscription s2) {
		if(s1.getCreationDate().isAfter(s2.getCreationDate())){
			return 0;
		}
		return 1;
	}
}

