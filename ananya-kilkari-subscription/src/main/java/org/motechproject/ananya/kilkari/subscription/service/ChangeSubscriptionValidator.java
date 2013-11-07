package org.motechproject.ananya.kilkari.subscription.service;
import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.subscription.domain.ChangeSubscriptionType;
import org.motechproject.ananya.kilkari.subscription.domain.Operator;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.subscription.request.OMSubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.service.mapper.SubscriptionMapper;
import org.motechproject.ananya.kilkari.subscription.service.request.ChangeSubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.service.request.Subscriber;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriptionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


import java.util.Arrays;
import java.util.List;
import java.util.Properties;


@Component
public class ChangeSubscriptionValidator {

	SubscriptionService subscriptionService;
	private Properties kilkariProperties;

	@Autowired
	public ChangeSubscriptionValidator(SubscriptionService subscriptionService, @Qualifier("kilkariProperties") Properties kilkariProperties) {
		this.subscriptionService = subscriptionService;
		this.kilkariProperties = kilkariProperties;
	}

	public void validate(ChangeSubscriptionRequest changeSubscriptionRequest) {
		Subscription subscription = validateAndReturnIfSubscriptionExists(changeSubscriptionRequest.getSubscriptionId());
		validateSubscriptionStatus(subscription);
		ChangeSubscriptionType changeType = changeSubscriptionRequest.getChangeType();
		if (ChangeSubscriptionType.isChangePack(changeType)){
			validateIfChangePackRequestIsBlockedForOperator(subscription.getOperator());
			validateIfSubscriptionAlreadyExistsFor(subscription.getMsisdn(), changeSubscriptionRequest.getPack());
		}else{
			validateRequestedPackIsSameAsExistingPack(subscription, changeSubscriptionRequest.getPack());
			if(ChangeSubscriptionType.isChangeSchedule(changeType)){
				validateIfEarlySubscriptonIsBlockedForOperator(subscription, changeSubscriptionRequest);	
			}
		}
	}



	private void validateIfEarlySubscriptonIsBlockedForOperator(Subscription subscription,ChangeSubscriptionRequest changeSubscriptionRequest) {
		String blockedOperators = kilkariProperties.getProperty("blocked.operators.for.early.suscriptions");
		if(subscriptionService.isTransitionFromActiveOrSuspendedToNewEarly(subscription, changeSubscriptionRequest) && isOperatorBlocked(subscription.getOperator(), blockedOperators))		
			throw new ValidationException(String.format("Transition to Early Subscription from %s state Is Blocked for Operator %s", subscription.getStatus().toString(), subscription.getOperator().toString()));

	}


	private boolean isOperatorBlocked(Operator operator,
			String blockedOperators) {
		if(blockedOperators !=null && !blockedOperators.isEmpty()){
			List<String> items = Arrays.asList(blockedOperators.toLowerCase().split("\\s*,\\s*"));
			if(items.contains(operator.toString().toLowerCase()))
				return true;
		}	
		return false;
	}

	private void validateIfChangePackRequestIsBlockedForOperator(
			Operator operator) {
		String blockedOperators = kilkariProperties.getProperty("blocked.operators.for.changed.pack");
		if(operator == null)
			throw new ValidationException(String.format("Operator Missing For Given User"));
		if(isOperatorBlocked(operator, blockedOperators))
			throw new ValidationException(String.format("Change Pack Request Is Blocked for Operator %s", operator.toString()));
	}

	private void validateIfSubscriptionAlreadyExistsFor(String msisdn, SubscriptionPack pack) {
		List<Subscription> subscriptionList = subscriptionService.findByMsisdnAndPack(msisdn, pack);
		for (Subscription subscription : subscriptionList) {
			if (subscription.isInProgress())
				throw new ValidationException(String.format("Active subscription already exists for %s and %s", msisdn, pack));
		}
	}

	public Subscription validateAndReturnIfSubscriptionExists(String subscriptionId) {
		Subscription subscription = subscriptionService.findBySubscriptionId(subscriptionId);
		if (subscription == null) {
			throw new ValidationException(String.format("Subscription does not exist for subscriptionId %s", subscriptionId));
		}
		return subscription;
	}

	private void validateRequestedPackIsSameAsExistingPack(Subscription existingSubscription, SubscriptionPack requestedPack) {
		if (existingSubscription.getPack() != requestedPack)
			throw new ValidationException(String.format("Subscription %s is not subscribed to requested pack", existingSubscription.getSubscriptionId()));
	}

	private void validateSubscriptionStatus(Subscription subscription) {
		if (!subscription.getStatus().canChangeSubscription())
			throw new ValidationException("Subscription is not active for subscriptionId " + subscription.getSubscriptionId());
	}
}
