package org.motechproject.ananya.kilkari.service;

import java.util.Comparator;

import org.motechproject.ananya.kilkari.subscription.domain.Subscription;

public class SubscriptionListComparator implements Comparator<Subscription> {

	@Override
	public int compare(Subscription s1, Subscription s2) {
		if(s1.getCreationDate().isBefore(s2.getCreationDate())){
			return 1;
		}
		return 0;
	}
}

