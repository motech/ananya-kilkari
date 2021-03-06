package org.motechproject.ananya.kilkari.subscription.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.joda.time.Weeks;
import org.motechproject.ananya.kilkari.messagecampaign.domain.MessageCampaignPack;
import org.motechproject.model.MotechBaseDataObject;

import java.util.UUID;

@TypeDiscriminator("doc.type === 'Subscription'")
public class Subscription extends MotechBaseDataObject {
	private static final double WEEK_IN_MILIS = 7*24*60*60*1000;
	/*Added Below property for converting 1 min into week(1/10080)= 9.92063e-5; (this calculation is done for adding delta if the scheduler ran few miliseconds before 
	which resulted in wrong weeks calculation.)*/
	private static final double COMPARE_WITH_DELTA =  9.92063e-5; 


	@JsonProperty
	private String msisdn;

	@JsonProperty
	private Operator operator;

	@JsonProperty
	private String subscriptionId;

	@JsonProperty
	private SubscriptionStatus status;

	@JsonProperty
	private SubscriptionPack pack;

	@JsonProperty
	private DateTime creationDate;

	@JsonProperty
	private DateTime startDate;

	@JsonProperty
	private DateTime activationDate;

	@JsonProperty
	private DateTime scheduleStartDate;

	@JsonProperty
	private Integer startWeekNumber;

	@JsonProperty
	private boolean campaignCompleted;

	@JsonProperty
	private MessageCampaignPack messageCampaignPack;

	@JsonProperty
	private String referredBy;

	@JsonProperty
	private boolean referredByFLW;

	Subscription() {
		//for serialization do not make it public
	}

	public Subscription(String msisdn, SubscriptionPack pack, DateTime createdAt, DateTime startDate, Integer startWeekNumber, String referredBy, boolean referredByFLW) {
		this.pack = pack;
		this.msisdn = msisdn;
		this.startWeekNumber = startWeekNumber;
		this.creationDate = floorToExactMinutes(createdAt);
		this.startDate = floorToExactMinutes(startDate);
		this.subscriptionId = UUID.randomUUID().toString();
		this.status = isEarlySubscription() ? SubscriptionStatus.NEW_EARLY : SubscriptionStatus.NEW;
		this.messageCampaignPack = MessageCampaignPack.from(pack.name());
		this.referredBy = referredBy;
		this.referredByFLW = referredByFLW;
	}

	public boolean isReferredByFLW() {
		return referredByFLW;
	}

	public void setReferredByFLW(boolean referredByFLW) {
		this.referredByFLW = referredByFLW;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public String getSubscriptionId() {
		return subscriptionId;
	}

	public DateTime getCreationDate() {
		return creationDate;
	}

	public SubscriptionStatus getStatus() {
		return status;
	}


	public SubscriptionPack getPack() {
		return pack;
	}

	public void setStatus(SubscriptionStatus status) {
		this.status = status;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public DateTime getStartDate() {
		return startDate;
	}

	public DateTime getActivationDate() {
		return activationDate;
	}

	public DateTime getScheduleStartDate() {
		return scheduleStartDate;
	}

	public Integer getStartWeekNumber() {
		return startWeekNumber;
	}

	public MessageCampaignPack getMessageCampaignPack() {
		return messageCampaignPack;
	}

	public void setMessageCampaignPack(MessageCampaignPack messageCampaignPack) {
		this.messageCampaignPack = messageCampaignPack;
	}
	public String getReferredBy() {
		return referredBy;
	}

	public void setReferredBy(String referredBy) {
		this.referredBy = referredBy;
	}

	public void setStartWeekNumber(Integer startWeekNumber) {
		this.startWeekNumber = startWeekNumber;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Subscription)) return false;

		Subscription that = (Subscription) o;

		return new EqualsBuilder().append(this.msisdn, that.msisdn)
				.append(this.pack, that.pack)
				.append(this.subscriptionId, that.subscriptionId)
				.append(this.operator, that.operator)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
		.append(this.msisdn)
		.append(this.subscriptionId)
		.append(this.pack)
		.hashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
		.append(msisdn)
		.append(operator)
		.append(subscriptionId)
		.append(pack)
		.append(status)
		.append(creationDate)
		.append(startDate)
		.append(activationDate)
		.append(scheduleStartDate)
		.append(startWeekNumber)
		.append(referredByFLW)
		.append(referredBy)
		.append(messageCampaignPack)
		.toString();
	}



	@JsonIgnore
	public boolean isInProgress() {
		return getStatus().isInProgress();
	}

	
	@JsonIgnore
	public boolean isNewEarly() {
		return getStatus().isNewEarly();
	}

	@JsonIgnore
	public boolean isActiveOrSuspended() {
		return getStatus().isActive() || getStatus().isSuspended();
	}
	
	
	@JsonIgnore
	public boolean isActiveSuspendedOrGrace() {
		return getStatus().isActive() || getStatus().isSuspended() ||getStatus().isGrace();
	}

	@JsonIgnore
	public boolean isInUpdatableState() {
		return (this.isNewEarly() || this.isActiveOrSuspended()) && !this.isInDeactivatedState();
	}

	public void activateOnRenewal() {
		setStatus(SubscriptionStatus.ACTIVE);
	}

	public void suspendOnRenewal() {
		setStatus(SubscriptionStatus.SUSPENDED);
	}

	public void activate(String operator, DateTime scheduleStartDate, DateTime activationDate) {
		setStatus(SubscriptionStatus.ACTIVE);
		setOperator(Operator.getFor(operator));
		this.scheduleStartDate = floorToExactMinutes(scheduleStartDate);
		this.activationDate = floorToExactMinutes(activationDate);
	}

	public void activationFailed(String operator) {
		setStatus(SubscriptionStatus.ACTIVATION_FAILED);
		setOperator(Operator.getFor(operator));
	}

	public void activationGrace(String operator) {
		setStatus(SubscriptionStatus.ACTIVATION_GRACE);
		setOperator(Operator.getFor(operator));
	}
	
	public void activationRequestSent() {
		setStatus(SubscriptionStatus.PENDING_ACTIVATION);
	}

	public void deactivationRequestSent() {
		setStatus(SubscriptionStatus.PENDING_DEACTIVATION);
	}

	public void deactivationRequestReceived() {
		setStatus(SubscriptionStatus.DEACTIVATION_REQUEST_RECEIVED);
	}

	public void deactivate() {
		if (isSubscriptionCompletionRequestSent())
			setStatus(SubscriptionStatus.COMPLETED);
		else
			setStatus(SubscriptionStatus.DEACTIVATED);
	}

	@JsonIgnore
	public boolean isSubscriptionCompletionRequestSent() {
		return SubscriptionStatus.PENDING_COMPLETION.equals(getStatus());
	}

	@JsonIgnore
	public boolean isSubscriptionInPendingActOrGrace() {
		return SubscriptionStatus.PENDING_ACTIVATION.equals(getStatus())||SubscriptionStatus.ACTIVATION_GRACE.equals(getStatus())||SubscriptionStatus.NEW.equals(getStatus())||SubscriptionStatus.NEW_EARLY.equals(getStatus());
	}
	
	@JsonIgnore
	public boolean isStatusUpdatableForReferredBy() {
		return SubscriptionStatus.ACTIVATION_FAILED.equals(getStatus())||isSubscriptionInPendingActOrGrace();
	}

	public void complete() {
		setStatus(SubscriptionStatus.PENDING_COMPLETION);
	}

	public DateTime endDate() {
		return getScheduleStartDate().plusWeeks(getPack().getTotalWeeks());
	}

	@JsonIgnore
	public boolean isInDeactivatedState() {
		return getStatus().isInDeactivatedState();
	}

	@JsonIgnore
	public DateTime getCurrentWeeksMessageExpiryDate() {
		return scheduleStartDate != null ? scheduleStartDate.plusWeeks(getWeeksElapsedAfterScheduleStartDate() + 1) : null;
	}

	@JsonIgnore
	private int getWeeksElapsedAfterScheduleStartDate() {
		/**commenting this line to apply workaround for base not happening*/
		//return Weeks.weeksBetween(scheduleStartDate, DateTime.now()).getWeeks();
				return getWeeksElapsedAfterStartDate();
	}

	private int getWeeksElapsedAfterStartDate() {
		double exactWeekNumber = exactWeeksbetween(scheduleStartDate, DateTime.now());
		double ceilValueOfExactWeekNumber = Math.ceil(exactWeekNumber);
		double diffBetween =	ceilValueOfExactWeekNumber - exactWeekNumber;

		if((diffBetween)<=(COMPARE_WITH_DELTA)){
			System.out.println("Calculation of weeknumber within subscription record. going to adjust delta minutes.");
			return (int)( Math.ceil(exactWeekNumber));
		}else{
			return Weeks.weeksBetween(scheduleStartDate, DateTime.now()).getWeeks();
		}
	}  

	private double exactWeeksbetween(DateTime start, DateTime end) {
		double exactWeekNumber = (end.getMillis()- start.getMillis())/WEEK_IN_MILIS;
		return exactWeekNumber;
	}


	/*
	 * Returns the next week number for this subscription devoid in absolute terms, ie. it is
	 * devoid of pack start week initial count.
	 */
	@JsonIgnore
	public int getNextWeekNumber() {
		DateTime now = DateTime.now();
		if (scheduleStartDate == null || scheduleStartDate.isAfter(now)) {
			return 1;
		}

		//return Weeks.weeksBetween(scheduleStartDate, now).getWeeks()  // Weeks elapsed increment
		return getWeeksElapsedAfterStartDate()
				+ 1 // Current Week increment
				+ 1; // Next week increment
	}

	public boolean hasBeenActivated() {
		return getStatus().hasBeenActivated();
	}

	public boolean hasSchedulableStatus() {
		return getStatus().canScheduleMessage();
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	@JsonIgnore
	public boolean isLateSubscription() {
		return startDate.isBefore(creationDate);
	}

	@JsonIgnore
	public DateTime getStartDateForSubscription(DateTime activatedOn) {
		activatedOn = floorToExactMinutes(activatedOn);
		if (isLateSubscription())
			return startDate.plus(activatedOn.getMillis() - creationDate.getMillis());

		return activatedOn;
	}

	@JsonIgnore
	public boolean isEarlySubscription() {
		return startDate.isAfter(creationDate);
	}

	public boolean canActivate() {
		return status.canTransitionTo(SubscriptionStatus.ACTIVE);
	}

	public boolean canDeactivate() {
		return status.canTransitionTo(SubscriptionStatus.DEACTIVATED);
	}

	public boolean canReceiveDeactivationRequest() {
		return status.canTransitionTo(SubscriptionStatus.DEACTIVATION_REQUEST_RECEIVED);
	}

	public boolean canSuspend() {
		return status.canTransitionTo(SubscriptionStatus.SUSPENDED);
	}

	public boolean canCreateNewSubscription() {
		return status.canTransitionTo(SubscriptionStatus.NEW);
	}

	public boolean canCreateANewEarlySubscription() {
		return status.canTransitionTo(SubscriptionStatus.NEW_EARLY);
	}

	public boolean canFailActivation() {
		return status.canTransitionTo(SubscriptionStatus.ACTIVATION_FAILED);
	}

	public boolean canMoveToActGrace() {
		return status.canTransitionTo(SubscriptionStatus.ACTIVATION_GRACE);
	}
	
	public boolean canSendActivationRequest() {
		return status.canTransitionTo(SubscriptionStatus.PENDING_ACTIVATION);
	}

	public boolean canMoveToPendingDeactivation() {
		return status.canTransitionTo(SubscriptionStatus.PENDING_DEACTIVATION);
	}

	public boolean canMoveToPendingCompletion() {
		return status.canTransitionTo(SubscriptionStatus.PENDING_COMPLETION);
	}

	public boolean canComplete() {
		return status.canTransitionTo(SubscriptionStatus.COMPLETED);
	}


	public boolean checkIfSubscriptionIsReferredStatus(){
		return getStatus().equals(SubscriptionStatus.REFERRED_MSISDN_RECEIVED);
	}
	
	private DateTime floorToExactMinutes(DateTime dateTime) {
		return dateTime != null ? dateTime.withSecondOfMinute(0).withMillisOfSecond(0) : null;
	}

	public boolean isCampaignCompleted() {
		return campaignCompleted;
	}

	public void campaignCompleted() {
		campaignCompleted = true;
	}

	public void setCreationDate(DateTime creationDate) {
		this.creationDate = creationDate;
	}

	public void setStartDate(DateTime startDate) {
		this.startDate = startDate;
	}

	public void setPack(SubscriptionPack pack) {
		this.pack = pack;
	}

	public void setScheduleStartDate(DateTime scheduleStartDate) {
		this.scheduleStartDate = scheduleStartDate;
	}
	
}
