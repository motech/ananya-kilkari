package org.motechproject.ananya.kilkari.subscription.service;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Weeks;
import org.motechproject.ananya.kilkari.message.service.CampaignMessageAlertService;
import org.motechproject.ananya.kilkari.message.service.InboxService;
import org.motechproject.ananya.kilkari.messagecampaign.domain.MessageCampaignPack;
import org.motechproject.ananya.kilkari.messagecampaign.request.MessageCampaignRequest;
import org.motechproject.ananya.kilkari.messagecampaign.service.MessageCampaignService;
import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.motechproject.ananya.kilkari.obd.service.CampaignMessageService;
import org.motechproject.ananya.kilkari.reporting.service.ReportingService;
import org.motechproject.ananya.kilkari.subscription.domain.*;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.motechproject.ananya.kilkari.subscription.repository.KilkariPropertiesData;
import org.motechproject.ananya.kilkari.subscription.repository.OnMobileSubscriptionGateway;
import org.motechproject.ananya.kilkari.subscription.request.OMSubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.service.mapper.SubscriptionDetailsResponseMapper;
import org.motechproject.ananya.kilkari.subscription.service.mapper.SubscriptionMapper;
import org.motechproject.ananya.kilkari.subscription.service.request.*;
import org.motechproject.ananya.kilkari.subscription.service.response.SubscriptionDetailsResponse;
import org.motechproject.ananya.kilkari.subscription.validators.ChangeMsisdnValidator;
import org.motechproject.ananya.kilkari.subscription.validators.SubscriptionValidator;
import org.motechproject.ananya.kilkari.subscription.validators.UnsubscriptionValidator;
import org.motechproject.ananya.kilkari.sync.service.RefdataSyncService;
import org.motechproject.ananya.reports.kilkari.contract.request.*;
import org.motechproject.ananya.reports.kilkari.contract.response.LocationResponse;
import org.motechproject.ananya.reports.kilkari.contract.response.SubscriberResponse;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.RunOnceSchedulableJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class SubscriptionService {
	private AllSubscriptions allSubscriptions;
	private OnMobileSubscriptionManagerPublisher onMobileSubscriptionManagerPublisher;
	private SubscriptionValidator subscriptionValidator;
	private ReportingService reportingService;
	private InboxService inboxService;
	private MessageCampaignService messageCampaignService;
	private OnMobileSubscriptionGateway onMobileSubscriptionGateway;
	private CampaignMessageService campaignMessageService;
	private CampaignMessageAlertService campaignMessageAlertService;
	private KilkariPropertiesData kilkariPropertiesData;
	private MotechSchedulerService motechSchedulerService;
	private ChangeMsisdnValidator changeMsisdnValidator;
	private UnsubscriptionValidator unsubscriptionValidator;
	private RefdataSyncService refdataSyncService;
	private SubscriptionDetailsResponseMapper subscriptionDetailsResponseMapper;

	private final static Logger logger = LoggerFactory.getLogger(SubscriptionService.class);

	@Autowired
	public SubscriptionService(AllSubscriptions allSubscriptions, OnMobileSubscriptionManagerPublisher onMobileSubscriptionManagerPublisher,
			SubscriptionValidator subscriptionValidator, ReportingService reportingService,
			InboxService inboxService, MessageCampaignService messageCampaignService, OnMobileSubscriptionGateway onMobileSubscriptionGateway,
			CampaignMessageService campaignMessageService, CampaignMessageAlertService campaignMessageAlertService, KilkariPropertiesData kilkariPropertiesData,
			MotechSchedulerService motechSchedulerService, ChangeMsisdnValidator changeMsisdnValidator, UnsubscriptionValidator unsubscriptionValidator,
			RefdataSyncService refdataSyncService, SubscriptionDetailsResponseMapper subscriptionDetailsResponseMapper) {
		this.allSubscriptions = allSubscriptions;
		this.onMobileSubscriptionManagerPublisher = onMobileSubscriptionManagerPublisher;
		this.subscriptionValidator = subscriptionValidator;
		this.reportingService = reportingService;
		this.inboxService = inboxService;
		this.messageCampaignService = messageCampaignService;
		this.onMobileSubscriptionGateway = onMobileSubscriptionGateway;
		this.campaignMessageService = campaignMessageService;
		this.campaignMessageAlertService = campaignMessageAlertService;
		this.kilkariPropertiesData = kilkariPropertiesData;
		this.motechSchedulerService = motechSchedulerService;
		this.changeMsisdnValidator = changeMsisdnValidator;
		this.unsubscriptionValidator = unsubscriptionValidator;
		this.refdataSyncService = refdataSyncService;
		this.subscriptionDetailsResponseMapper = subscriptionDetailsResponseMapper;
	}

	public Subscription createSubscription(SubscriptionRequest subscriptionRequest, Channel channel) {
		subscriptionValidator.validate(subscriptionRequest);

		Subscription existingActiveSubscription = allSubscriptions.findSubscriptionInProgress(subscriptionRequest.getMsisdn(), subscriptionRequest.getPack());

		if(existingActiveSubscription!=null && existingActiveSubscription.getStatus().equals(SubscriptionStatus.REFERRED_MSISDN_RECEIVED)){
			//user has received referred msisdn received request first. hence getting referred by and deleting this entry
			subscriptionRequest.setReferredByFLW(existingActiveSubscription.isReferredByFLW());
			logger.info("setting referred by to in subscription request and removing existing entry for referred msisdn received "+existingActiveSubscription.getMsisdn());
			allSubscriptions.remove(existingActiveSubscription);
		}

		Subscription subscription = new Subscription(subscriptionRequest.getMsisdn(), subscriptionRequest.getPack(),
				subscriptionRequest.getCreationDate(), subscriptionRequest.getSubscriptionStartDate(), subscriptionRequest.getSubscriber().getWeek(), subscriptionRequest.getReferredBy(), subscriptionRequest.isReferredByFLW());
		allSubscriptions.add(subscription);

		Location location = subscriptionRequest.getLocation();
		LocationResponse existingLocation = getExistingLocation(location);

		SubscriptionReportRequest reportRequest = SubscriptionMapper.createSubscriptionCreationReportRequest(
				subscription, channel, subscriptionRequest, null, null, false);
		reportingService.reportSubscriptionCreation(reportRequest);

		OMSubscriptionRequest omSubscriptionRequest = SubscriptionMapper.createOMSubscriptionRequest(subscription, channel,"");
		if (subscription.isEarlySubscription()) {
			scheduleEarlySubscription(subscriptionRequest.getSubscriptionStartDate(), omSubscriptionRequest);
		} else
			initiateActivationRequest(omSubscriptionRequest);

		if (existingLocation == null && subscriptionRequest.hasLocation()) {
			refdataSyncService.syncNewLocation(location.getState(), location.getDistrict(), location.getBlock(), location.getPanchayat());
		}

		return subscription;
	}

	private void scheduleEarlySubscription(DateTime startDate, OMSubscriptionRequest omSubscriptionRequest) {
		String subjectKey = SubscriptionEventKeys.EARLY_SUBSCRIPTION;

		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put(MotechSchedulerService.JOB_ID_KEY, omSubscriptionRequest.getSubscriptionId());
		parameters.put("0", omSubscriptionRequest);
		MotechEvent motechEvent = new MotechEvent(subjectKey, parameters);

		RunOnceSchedulableJob runOnceSchedulableJob = new RunOnceSchedulableJob(motechEvent, startDate.toDate());

		motechSchedulerService.safeScheduleRunOnceJob(runOnceSchedulableJob);
	}


	public void initiateActivationRequest(OMSubscriptionRequest omSubscriptionRequest) {
		onMobileSubscriptionManagerPublisher.sendActivationRequest(omSubscriptionRequest);
	}

	public void initiateActivationRequestForEarlySubscription(OMSubscriptionRequest omSubscriptionRequest) {
		updateToLatestMsisdn(omSubscriptionRequest);
		onMobileSubscriptionManagerPublisher.sendActivationRequest(omSubscriptionRequest);
	}

	public List<Subscription> findByMsisdn(String msisdn) {
		return allSubscriptions.findByMsisdn(msisdn);
	}

	public List<Subscription> findByMsisdnAndPack(String msisdn, SubscriptionPack pack) {
		return allSubscriptions.findByMsisdnAndPack(msisdn, pack);
	}

	public Subscription findSubscriptionInProgress(String msisdn, SubscriptionPack pack) {
		return allSubscriptions.findSubscriptionInProgress(msisdn, pack);
	}

	public Subscription subscriptionInActiveOrSuspended(String msisdn, SubscriptionPack pack) {
		return allSubscriptions.subscriptionInActiveOrSuspended(msisdn, pack);
	}

	public void activate(String subscriptionId, final DateTime activatedOn, final String operator, String mode) {
		Subscription subscription = allSubscriptions.findBySubscriptionId(subscriptionId);
		if (!subscription.canActivate()) {
			logger.warn("Cannot ACTIVATE from state : " + subscription.getStatus());
			return;
		}

		final DateTime scheduleStartDateTime = subscription.getStartDateForSubscription(activatedOn);
		scheduleCampaign(subscription, scheduleStartDateTime);
		updateStatusAndReport(subscription, activatedOn, null, operator, null,mode, new Action<Subscription>() {
			@Override
			public void perform(Subscription subscription) {
				subscription.activate(operator, getBufferedDateTime(scheduleStartDateTime), activatedOn);
			}
		});
		activateSchedule(subscription);
	}

	public void activateForReqFromSM(String msisdn, SubscriptionPack pack, final DateTime activatedOn, final String operator, String mode) {
		boolean updated = false;
		List<Subscription> subscriptions = allSubscriptions.findByMsisdnAndPack(msisdn, pack);
		logger.info("got request for msisdn:"+msisdn+" pack:"+pack+" . Activated on:"+activatedOn.toString()+" operator="+operator);

		if(!subscriptions.isEmpty()){
			//an entry already exists with msisdn and pack
			for(Subscription subscription:subscriptions){
				if(subscription.canActivate()){
					logger.info("an entry already exists with msisdn pack and status. Subscription="+subscription.toString());
					final DateTime scheduleStartDateTime = subscription.getStartDateForSubscription(activatedOn);
					logger.info("scheduleStartDateTime="+scheduleStartDateTime.toString());
					if(subscription.getStatus().equals(SubscriptionStatus.REFERRED_MSISDN_RECEIVED)){
						//A referred mdn received entry exists. we need to update the entry in couch and creat new subscription in postgres
						updateStatusAndReportForSM(subscription, activatedOn, null, operator, null,mode, new Action<Subscription>() {
							@Override
							public void perform(Subscription subscription) {
								subscription.activate(operator, getBufferedDateTime(scheduleStartDateTime), activatedOn);
							}
						});
						logger.info("going to schedule campaign");
						scheduleCampaign(subscription, scheduleStartDateTime);
						logger.info("activateSchedule");
						activateSchedule(subscription);
						updated = true;
					}else if(subscription.getStatus().equals(SubscriptionStatus.ACTIVATION_GRACE)){
						//entry present in couch and postgres for this sbscription. we need to update both couch and postgres.
						updateStatusAndReport(subscription, activatedOn, null, operator, null,mode, new Action<Subscription>() {
							@Override
							public void perform(Subscription subscription) {
								subscription.activate(operator, getBufferedDateTime(scheduleStartDateTime), activatedOn);
							}
						});
						logger.info("going to schedule campaign");
						scheduleCampaign(subscription, scheduleStartDateTime);
						logger.info("activateSchedule");
						activateSchedule(subscription);
						updated = true;
					}
				}
			}
		}
		if(!updated){
			//create new subscription
			logger.info("create new subscription");
			Subscription subscription = new Subscription(msisdn, pack, activatedOn , DateTime.now() , null, null, false);
			logger.info("Created Subscription="+subscription.toString());
			final DateTime scheduleStartDateTime = subscription.getStartDateForSubscription(activatedOn);
			logger.info("scheduleStartDateTime="+scheduleStartDateTime.toString());
			createSubscriptionAndReport(subscription, activatedOn, null, operator, null,mode, new Action<Subscription>() {
				@Override
				public void perform(Subscription subscription) {
					subscription.activate(operator, getBufferedDateTime(scheduleStartDateTime), activatedOn);
				}
			});
			List<Subscription> newSubscription = allSubscriptions.findByMsisdnPackAndStatus(msisdn, pack, SubscriptionStatus.ACTIVE);
			if(newSubscription.isEmpty()){
				logger.warn("Cannot schedule campaign as entry is missing in couchDB");
				return;
			}
			subscription = newSubscription.get(0);
			logger.info(" Subscription="+subscription.toString());
			logger.info("going to schedule campaign");
			scheduleCampaign(subscription, scheduleStartDateTime);
			logger.info("activateSchedule");
			activateSchedule(subscription);
		}
	}



	public void activationFailed(String subscriptionId, DateTime updatedOn, String reason, final String operator, String mode) {
		Subscription subscription = allSubscriptions.findBySubscriptionId(subscriptionId);
		if (!subscription.canFailActivation()) {
			logger.warn("Cannot move to ACTIVATION_FAILED state from state : " + subscription.getStatus());
			return;
		}
		updateStatusAndReport(subscription, updatedOn, reason, operator, null,mode, new Action<Subscription>() {
			@Override
			public void perform(Subscription subscription) {
				subscription.activationFailed(operator);
			}
		});
	}

	public void activationFailedForSM(String msisdn, SubscriptionPack pack,
			DateTime updatedOn, String reason,
			final String operator, String mode) {
		//SubscriptionStatus status= SubscriptionStatus.REFERRED_MSISDN_RECEIVED;
		//List<Subscription> subscriptions = allSubscriptions.findByMsisdnPackAndStatus(msisdn, pack, status);
		boolean updated = false;
		List<Subscription> subscriptions = findByMsisdnAndPack(msisdn, pack);
		if(!subscriptions.isEmpty()){
			//subscription is not empty see if any of the entries are updatable.
			for(Subscription subscription:subscriptions){
				if(subscription.canFailActivation()){
					logger.info("moving to ACT-FAILED subscription"+subscription.toString());
					if(subscription.getStatus().equals(SubscriptionStatus.REFERRED_MSISDN_RECEIVED)){
						updateStatusAndReportForSM(subscription, updatedOn, reason, operator, null,mode, new Action<Subscription>() {
							@Override
							public void perform(Subscription subscription) {
								subscription.activationFailed(operator);
							}
						});
						updated = true;
					}else if(subscription.getStatus().equals(SubscriptionStatus.ACTIVATION_GRACE)){
						updateStatusAndReport(subscription, updatedOn, reason, operator, null,mode, new Action<Subscription>() {
							@Override
							public void perform(Subscription subscription) {
								subscription.activationFailed(operator);
							}
						});
						updated = true;
					}
				}
			}
		}
		if(!updated){
			//create new subscription with status ACT_FAILED
			Subscription subscription = new Subscription(msisdn, pack, updatedOn , DateTime.now(), null, null, false);
			logger.info("created subscription"+subscription.toString());
			createSubscriptionAndReport(subscription, updatedOn, null, operator, null,mode, new Action<Subscription>() {
				@Override
				public void perform(Subscription subscription) {
					subscription.activationFailed(operator);
				}
			});
		}
	}


	public void activationGrace(String subscriptionId, DateTime updatedOn, String reason, final String operator, String mode) {
		Subscription subscription = allSubscriptions.findBySubscriptionId(subscriptionId);
		if (!subscription.canMoveToActGrace()) {
			logger.warn("Cannot move to ACTIVATION_GRACE state from state : " + subscription.getStatus());
			return;
		}
		updateStatusAndReport(subscription, updatedOn, reason, operator, null,mode, new Action<Subscription>() {
			@Override
			public void perform(Subscription subscription) {
				subscription.activationGrace(operator);
			}
		});
	}

	public void activationGraceForSM(String msisdn, SubscriptionPack pack,
			SubscriptionStatus status, DateTime updatedOn, String reason,
			final String operator, String mode) {
		List<Subscription> subscriptions = allSubscriptions.findByMsisdnPackAndStatus(msisdn, pack, status);
		if(!subscriptions.isEmpty()){
			Subscription subscription = subscriptions.get(0);
			logger.info(" subscription"+subscription.toString());
			if (!subscription.canMoveToActGrace()) {
				logger.warn("Cannot move to ACTIVATION_GRACE state from state : " + subscription.getStatus());
				return;
			}
			updateStatusAndReportForSM(subscription, updatedOn, reason, operator, null,mode, new Action<Subscription>() {
				@Override
				public void perform(Subscription subscription) {
					subscription.activationGrace(operator);
				}
			});
		}else{
			Subscription subscription = new Subscription(msisdn, pack, updatedOn , DateTime.now(), null, null, false);
			logger.info("created subscription"+subscription.toString()+" with status ACTIVATION_GRACE");
			createSubscriptionAndReport(subscription, updatedOn, null, operator, null,mode, new Action<Subscription>() {
				@Override
				public void perform(Subscription subscription) {
					subscription.activationGrace(operator);
				}
			});
		}

	}

	public void activationRequested(OMSubscriptionRequest omSubscriptionRequest) {
		Subscription subscription = allSubscriptions.findBySubscriptionId(omSubscriptionRequest.getSubscriptionId());
		if (!subscription.canSendActivationRequest()) {
			logger.warn("Cannot move to PENDING_ACTIVATION state from state : " + subscription.getStatus());
			return;
		}

		onMobileSubscriptionGateway.activateSubscription(omSubscriptionRequest);
		updateStatusWithoutReporting(subscription, new Action<Subscription>() {
			@Override
			public void perform(Subscription subscription) {
				subscription.activationRequestSent();
			}
		});
	}

	public void requestDeactivation(DeactivationRequest deactivationRequest) {
		Subscription subscription = allSubscriptions.findBySubscriptionId(deactivationRequest.getSubscriptionId());
		if (subscription.isNewEarly()) {
			deactivateAndUnschedule(subscription, deactivationRequest);
			return;
		}
		if (!subscription.canReceiveDeactivationRequest()) {
			logger.warn("Cannot move to DEACTIVATION_REQUEST_RECEIVED state from state : " + subscription.getStatus());
			return;
		}
		updateStatusAndReport(subscription, deactivationRequest.getCreatedAt(), deactivationRequest.getReason(), null, null,deactivationRequest.getMode(), new Action<Subscription>() {
			@Override
			public void perform(Subscription subscription) {
				subscription.deactivationRequestReceived();
			}
		});
		onMobileSubscriptionManagerPublisher.processDeactivation(SubscriptionMapper.createOMSubscriptionRequest(subscription, deactivationRequest.getChannel(),deactivationRequest.getMode()));
	}

	public void requestUnsubscription(DeactivationRequest deactivationRequest) {
		unsubscriptionValidator.validate(deactivationRequest.getSubscriptionId());
		requestDeactivation(deactivationRequest);
	}

	public void deactivationRequested(OMSubscriptionRequest omSubscriptionRequest) {
		Subscription subscription = allSubscriptions.findBySubscriptionId(omSubscriptionRequest.getSubscriptionId());
		if (!subscription.canMoveToPendingDeactivation()) {
			logger.warn("Cannot move to PENDING_DEACTIVATION state from state : " + subscription.getStatus());
			return;
		}

		onMobileSubscriptionGateway.deactivateSubscription(omSubscriptionRequest);
		updateStatusAndReport(subscription, DateTime.now(), null, null, null,omSubscriptionRequest.getMode(), new Action<Subscription>() {
			@Override
			public void perform(Subscription subscription) {
				subscription.deactivationRequestSent();
			}
		});
	}

	public void renewSubscription(String subscriptionId, final DateTime renewedDate, Integer graceCount, String mode) {
		Subscription subscription = allSubscriptions.findBySubscriptionId(subscriptionId);
		if (!subscription.canActivate()) {
			logger.warn("Cannot renew from state : " + subscription.getStatus());
			return;
		}
		updateStatusAndReport(subscription, renewedDate, null, null, graceCount,mode, new Action<Subscription>() {
			@Override
			public void perform(Subscription subscription) {
				subscription.activateOnRenewal();
			}
		});
		renewSchedule(subscription);
	}


	public void renewSubscriptionForSM(String msisdn, SubscriptionPack pack,
			final DateTime renewedDate, Integer graceCount, String mode, String operator) {
		List<Subscription> subscriptions = findByMsisdnAndPack(msisdn, pack);

		//Adding this functionality temporarily. should be deleted once SM is in synch with motech.
		if(subscriptions.isEmpty()){
			//A request has come from SM for renewal for which no entry exists in DB. FOr the time being going to activate these entries in such cases.
			activateForReqFromSM(msisdn, pack,  renewedDate, operator,mode);
			return;
		}else{
			boolean canBeRenewed = false;
			for (Subscription subscription : subscriptions) {
				if(subscription.getStatus().canRenew())
					canBeRenewed = true;	
			}
			if(!canBeRenewed){
				logger.debug("Redirecting the request to activation flow for msisdn: "+msisdn+" and pack: "+pack);
				activateForReqFromSM(msisdn, pack,  renewedDate, operator,mode);
				return;
			}
		}
		//workaround over

		for(Subscription subscription: subscriptions){
			if(subscription.canActivate() && !subscription.checkIfSubscriptionIsReferredStatus()){
				updateStatusAndReport(subscription, renewedDate, null, null, graceCount,mode, new Action<Subscription>() {
					@Override
					public void perform(Subscription subscription) {
						subscription.activateOnRenewal();
					}
				});
				renewSchedule(subscription);
			}else{
				logger.warn("Cannot renew from state : " + subscription.getStatus());
			}
		}
	}


	public void suspendSubscription(String subscriptionId, final DateTime renewalDate, String reason, Integer graceCount, String mode) {
		Subscription subscription = allSubscriptions.findBySubscriptionId(subscriptionId);
		if (!subscription.canSuspend()) {
			logger.warn("Cannot suspend from state : " + subscription.getStatus());
			return;
		}
		updateStatusAndReport(subscription, renewalDate, reason, null, graceCount,mode, new Action<Subscription>() {
			@Override
			public void perform(Subscription subscription) {
				subscription.suspendOnRenewal();
			}
		});
	}

	public void suspendSubscriptionForSM(String msisdn, SubscriptionPack pack, final DateTime renewalDate, String reason, Integer graceCount, String mode) {
		List<Subscription> subscriptions = findByMsisdnAndPack(msisdn, pack);
		for(Subscription subscription: subscriptions){
			if (subscription.canSuspend()) {
				updateStatusAndReport(subscription, renewalDate, reason, null, graceCount,mode, new Action<Subscription>() {
					@Override
					public void perform(Subscription subscription) {
						subscription.suspendOnRenewal();
					}
				});
			}else
				logger.warn("Cannot suspend from state : " + subscription.getStatus());

		}
	}


	public void processDeactivation(String subscriptionId, final DateTime deactivationDate, String reason, Integer graceCount, String mode, String operator) {
		Subscription subscription = findBySubscriptionId(subscriptionId);
		if (subscription.isSubscriptionCompletionRequestSent())
			deactivateSubscription(subscriptionId, deactivationDate, reason, graceCount, mode);
		else if(subscription.isSubscriptionInPendingActOrGrace()){
			logger.info("gor DCT-SUCCESS for subscription in "+subscription.getStatus()+". Redirecting to ACT_FAILED");
			activationFailed(subscriptionId, deactivationDate, reason,operator,mode);
		}else{
			SubscriptionStatus status = subscription.getStatus();
			if (status.isSuspended()) {
				reason = (StringUtils.isEmpty(reason)) ? "Deactivation due to renewal max" : reason;
				logger.info(String.format("Subscription %s is being deactivated due to low balance. Current status: %s", subscriptionId, status.getDisplayString()));
			}
			scheduleDeactivation(subscriptionId, deactivationDate, reason, graceCount, mode);
		}
	}

	public void processDeactivationForReqSM(String msisdn,
			SubscriptionPack pack, final DateTime deactivationDate , String reason,
			Integer graceCount, String mode,String operator) {
		List<Subscription> subscriptions = findByMsisdnAndPack(msisdn, pack);
		logger.info("total no. of subscriptions for msisdn and pack="+subscriptions.size());

		//workaround for idea and tata 
		if(subscriptions.isEmpty()){
			//A request has come from SM for DCT for which no entry exists in DB. FOr the time being going to move this entry to act_failed.
			activationFailedForSM(msisdn,pack, deactivationDate, reason,operator,mode);
			return;
		}else{
			boolean canBeDeactivated = false;
			for (Subscription subscription : subscriptions) {
				if(subscription.getStatus().canTransitionTo(SubscriptionStatus.DEACTIVATED))
					canBeDeactivated = true;	
			}
			if(!canBeDeactivated){
				activationFailedForSM(msisdn,pack, deactivationDate, reason,operator,mode);
				return;
			}
		}
		//workaround over

		for(Subscription subscription: subscriptions){
			/*if(subscription.isSubscriptionInPendingActOrGrace()){
				logger.info("gor DCT-SUCCESS for subscription in "+subscription.getStatus()+". Redirecting to ACT_FAILED");
				activationFailedForSM(msisdn,pack, deactivationDate, reason,operator,mode);
				break;
			}else{*/
			if(subscription.canDeactivate()){
				if (subscription.isSubscriptionCompletionRequestSent())
					deactivateSubscription(subscription.getSubscriptionId(), deactivationDate, reason, graceCount, mode);
				else {
					SubscriptionStatus status = subscription.getStatus();
					if (status.isSuspended()) {
						reason = (StringUtils.isEmpty(reason)) ? "Deactivation due to renewal max" : reason;
						logger.info(String.format("Subscription %s is being deactivated due to low balance. Current status: %s", subscription.getSubscriptionId(), status.getDisplayString()));
					}
					scheduleDeactivation(subscription.getSubscriptionId(), deactivationDate, reason, graceCount,mode);
				}
			}
			/*}*/
		}
	}

	public void deactivateSubscription(String subscriptionId, final DateTime deactivationDate, String reason, Integer graceCount, String mode) {
		Subscription subscription = allSubscriptions.findBySubscriptionId(subscriptionId);
		if (!(subscription.canDeactivate() || subscription.canComplete())) {
			logger.warn("Cannot deactivate from state : " + subscription.getStatus());
			return;
		}

		updateStatusAndReport(subscription, deactivationDate, reason, null, graceCount,mode, new Action<Subscription>() {
			@Override
			public void perform(Subscription subscription) {
				subscription.deactivate();
				inboxService.scheduleInboxDeletion(subscription.getSubscriptionId(), subscription.getCurrentWeeksMessageExpiryDate());
				unScheduleCampaign(subscription);
				campaignMessageAlertService.deleteFor(subscription.getSubscriptionId());
			}
		});
	}

	public void subscriptionComplete(OMSubscriptionRequest omSubscriptionRequest, int retrycount, boolean isFirstTry) {
		 if(retrycount>kilkariPropertiesData.getRetryCountForCompletionFlow()){
        	 logger.error(String.format("Failed to move subscriptionId: %s, msisdn: %s, pack: %s to completion. Max retrycount reached.", omSubscriptionRequest.getSubscriptionId(), omSubscriptionRequest.getMsisdn(), omSubscriptionRequest.getPack()));
        	 return;
        }
		Subscription subscription = allSubscriptions.findBySubscriptionId(omSubscriptionRequest.getSubscriptionId());
		if (!subscription.canMoveToPendingCompletion()) {
			logger.warn(String.format("Cannot unsubscribe for subscriptionid: %s  msisdn: %s as it is already in the %s state", omSubscriptionRequest.getSubscriptionId(), omSubscriptionRequest.getMsisdn(), subscription.getStatus()));
			return;
		}	
		try{
			onMobileSubscriptionGateway.deactivateSubscription(omSubscriptionRequest);
			updateStatusAndReport(subscription, DateTime.now(), "Subscription completed", null, null,omSubscriptionRequest.getMode(), new Action<Subscription>() {
				@Override
				public void perform(Subscription subscription) {
					subscription.complete();
					inboxService.scheduleInboxDeletion(subscription.getSubscriptionId(), subscription.getCurrentWeeksMessageExpiryDate());
					campaignMessageAlertService.deleteFor(subscription.getSubscriptionId());
				}
			});
		}catch(Exception e){
			logger.warn(String.format("completeion request for subscriptionid: %s  msisdn: %s returned exception %s . Scheduling completion once again.", omSubscriptionRequest.getSubscriptionId(), omSubscriptionRequest.getMsisdn(), e.getMessage()));
			if(isFirstTry){
				isFirstTry = false;
			}
			retryOMReqCompletion(subscription, DateTime.now(),++retrycount,isFirstTry);
		}

	}

	public Subscription findBySubscriptionId(String subscriptionId) {
		return allSubscriptions.findBySubscriptionId(subscriptionId);
	}

	public void changeMsisdn(ChangeMsisdnRequest changeMsisdnRequest) {
		changeMsisdnValidator.validate(changeMsisdnRequest);

		String oldMsisdn = changeMsisdnRequest.getOldMsisdn();
		List<Subscription> updatableSubscriptions = allSubscriptions.findUpdatableSubscriptions(oldMsisdn);
		for (Subscription subscription : updatableSubscriptions) {
			if (!shouldChangeMsisdn(subscription, changeMsisdnRequest)) continue;

			if (subscription.isNewEarly())
				changeMsisdnForEarlySubscription(subscription, changeMsisdnRequest);
			else
				migrateMsisdnToNewSubscription(subscription, changeMsisdnRequest);
		}
	}

	public void rescheduleCampaign(CampaignRescheduleRequest campaignRescheduleRequest) {
		String subscriptionId = campaignRescheduleRequest.getSubscriptionId();
		subscriptionValidator.validateChangeCampaign(subscriptionId, campaignRescheduleRequest.getReason());

		Subscription subscription = allSubscriptions.findBySubscriptionId(subscriptionId);
		updateMessageCampaignPackAndReport(campaignRescheduleRequest, subscription);
		DateTime nextAlertDateTime = messageCampaignService.getMessageTimings(subscriptionId, campaignRescheduleRequest.getCreatedAt(), campaignRescheduleRequest.getCreatedAt().plusMonths(1)).get(0);
		unScheduleCampaign(subscription);
		removeScheduledMessagesFromOBD(subscriptionId);
		scheduleCampaign(campaignRescheduleRequest, nextAlertDateTime);
	}

	private void updateMessageCampaignPackAndReport(CampaignRescheduleRequest campaignRescheduleRequest, Subscription subscription) {
		MessageCampaignPack messageCampaignPack = MessageCampaignPack.from(campaignRescheduleRequest.getReason().name());
		subscription.setMessageCampaignPack(messageCampaignPack);
		allSubscriptions.update(subscription);

		logger.info("Reporting change campaign for " + subscription);
		reportingService.reportCampaignChange(new CampaignChangeReportRequest(messageCampaignPack.name(), campaignRescheduleRequest.getCreatedAt()), subscription.getSubscriptionId());
	}

	public void updateSubscriberDetails(SubscriberRequest request) {
		subscriptionValidator.validateSubscriberDetails(request);

		Location location = request.getLocation();
		LocationResponse existingLocation = getExistingLocation(location);

		SubscriberLocation subscriberLocation = request.hasLocation() ? new SubscriberLocation(location.getState(), location.getDistrict(), location.getBlock(), location.getPanchayat()) : null;
		reportingService.reportSubscriberDetailsChange(request.getSubscriptionId(), new SubscriberReportRequest(request.getCreatedAt(),
				request.getBeneficiaryName(), request.getBeneficiaryAge(), subscriberLocation));

		if (existingLocation == null && request.hasLocation()) {
			refdataSyncService.syncNewLocation(location.getState(), location.getDistrict(), location.getBlock(), location.getPanchayat());
		}
	}

	public void unScheduleCampaign(Subscription subscription) {
		String activeCampaignName = messageCampaignService.getActiveCampaignName(subscription.getSubscriptionId());
		MessageCampaignRequest unEnrollRequest = new MessageCampaignRequest(subscription.getSubscriptionId(), activeCampaignName, subscription.getScheduleStartDate());
		messageCampaignService.stop(unEnrollRequest);
	}

	public List<SubscriptionDetailsResponse> getSubscriptionDetails(String msisdn, Channel channel) {
		List<Subscription> subscriptionList = findByMsisdn(msisdn);
		if (Channel.IVR.equals(channel)) {
			return subscriptionDetailsResponseMapper.map(subscriptionList, Collections.EMPTY_LIST);
		}
		List<SubscriberResponse> subscriberDetailsFromReports = reportingService.getSubscribersByMsisdn(msisdn);
		return subscriptionDetailsResponseMapper.map(subscriptionList, subscriberDetailsFromReports);
	}

	public void scheduleCompletion(Subscription subscription, DateTime completionDate) {
		String subjectKey = SubscriptionEventKeys.SUBSCRIPTION_COMPLETE;
		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put(MotechSchedulerService.JOB_ID_KEY, subscription.getSubscriptionId());
		parameters.put("0", SubscriptionMapper.createOMSubscriptionRequest(subscription, Channel.MOTECH, Channel.MOTECH.toString()));

		MotechEvent motechEvent = new MotechEvent(subjectKey, parameters);
		RunOnceSchedulableJob runOnceSchedulableJob = new RunOnceSchedulableJob(motechEvent, completionDate.toDate());

		motechSchedulerService.safeScheduleRunOnceJob(runOnceSchedulableJob);
	}
	
	public void retryOMReqCompletion(Subscription subscription, DateTime completionDate,int retryCount, boolean isFirstTry) {
		String subjectKey = SubscriptionEventKeys.RETRY_SUBSCRIPTION_COMPLETE;
		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put("0", SubscriptionMapper.createOMSubscriptionRequest(subscription, Channel.MOTECH, Channel.MOTECH.toString()));
		parameters.put("isFirstTry", isFirstTry);
		parameters.put("retryCount", retryCount);
		
		MotechEvent motechEvent = new MotechEvent(subjectKey, parameters);
		RunOnceSchedulableJob runOnceSchedulableJob = new RunOnceSchedulableJob(motechEvent, completionDate.toDate());

		motechSchedulerService.safeScheduleRunOnceJob(runOnceSchedulableJob);
	}
	
	private void updateToLatestMsisdn(OMSubscriptionRequest omSubscriptionRequest) {
		Subscription subscription = allSubscriptions.findBySubscriptionId(omSubscriptionRequest.getSubscriptionId());
		omSubscriptionRequest.setMsisdn(subscription.getMsisdn());
	}

	private void deactivateAndUnschedule(Subscription subscription, DeactivationRequest deactivationRequest) {
		motechSchedulerService.safeUnscheduleRunOnceJob(SubscriptionEventKeys.EARLY_SUBSCRIPTION, deactivationRequest.getSubscriptionId());
		updateStatusAndReport(subscription, deactivationRequest.getCreatedAt(), deactivationRequest.getReason(), null, null,deactivationRequest.getMode(), new Action<Subscription>() {
			@Override
			public void perform(Subscription subscription) {
				subscription.deactivate();
			}
		});
	}

	private void unschedule(Subscription subscription) {
		motechSchedulerService.safeUnscheduleRunOnceJob(SubscriptionEventKeys.EARLY_SUBSCRIPTION, subscription.getSubscriptionId());
	}

	public void rescheduleCampaignForChangeRequest(Subscription subscription, final ChangeSubscriptionRequest changeRequest) {		
		if (subscription.isNewEarly()) 
			unschedule(subscription);
		else{
			inboxService.scheduleInboxDeletion(subscription.getSubscriptionId(), subscription.getCurrentWeeksMessageExpiryDate());
			unScheduleCampaign(subscription);
			campaignMessageAlertService.deleteFor(subscription.getSubscriptionId());
			removeScheduledMessagesFromOBD(subscription.getSubscriptionId());
		}
		Subscriber subscriber = updateSubscriberDetailsAndReturnSubscriber(subscription, changeRequest);
		SubscriptionRequest subscriptionRequest = new SubscriptionRequest(changeRequest.getMsisdn(), changeRequest.getCreatedAt(), changeRequest.getPack(), null, subscriber, changeRequest.getReason(), subscription.getReferredBy(), subscription.isReferredByFLW(),"changeRequest-Serco");
		subscription.setStartDate(subscriptionRequest.getSubscriptionStartDate());
		//create request to set messagecampaign
		OMSubscriptionRequest omSubscriptionRequest = SubscriptionMapper.createOMSubscriptionRequest(subscription, changeRequest.getChannel(),"serco");
		subscription.setCreationDate(changeRequest.getCreatedAt());
		final DateTime scheduleStartDateTime = subscription.getStartDateForSubscription(subscriptionRequest.getCreationDate());
		subscription.setScheduleStartDate(scheduleStartDateTime);
		if(subscriptionRequest.getSubscriptionStartDate().isAfter(subscriptionRequest.getCreationDate()))
			scheduleEarlySubscription(subscriptionRequest.getSubscriptionStartDate(), omSubscriptionRequest);
		else{
			logger.info("going to reschedule subscription for subscription id"+subscription.getSubscriptionId()+" start date="+scheduleStartDateTime);
			//If an new early request for normal call wants to transition to active mode after edd/dob updation it should be handled seperately.
			//the new subscription must be activated immedietly, which will be done via omsm.
			if(subscription.isNewEarly()){
				requestDeactivation(new DeactivationRequest(subscription.getSubscriptionId(), changeRequest.getChannel(),
						subscription.getCreationDate(), changeRequest.getReason(),"changeSubscription-Serco"));	
				subscriptionRequest.setOldSubscriptionId(changeRequest.getSubscriptionId());
				createSubscription(subscriptionRequest, changeRequest.getChannel());
				return;
			}
			scheduleCampaign(subscription, scheduleStartDateTime);
			activateSchedule(subscription);
		}
		updateSubscriptionAndReport(subscription, changeRequest.getChannel());
	}


	private Subscriber updateSubscriberDetailsAndReturnSubscriber(Subscription subscription, ChangeSubscriptionRequest changeRequest){
		SubscriberResponse subscriberResponse = reportingService.getSubscriber(subscription.getSubscriptionId());
		Subscriber subscriber = new Subscriber(subscriberResponse.getBeneficiaryName(), subscriberResponse.getBeneficiaryAge(), changeRequest.getDateOfBirth(), changeRequest.getExpectedDateOfDelivery(), null);
		SubscriberChangeSubscriptionReportRequest subscriberReportRequest = new SubscriberChangeSubscriptionReportRequest(changeRequest.getCreatedAt(), subscriber.getExpectedDateOfDelivery(), subscriber.getDateOfBirth(), subscriber.getWeek());
		reportingService.reportSubscriberDetailsChangeForChangeSubscription(subscription.getSubscriptionId(), subscriberReportRequest);
		return subscriber;
	}

	private void updateSubscriptionAndReport(Subscription subscription, Channel channel){
		allSubscriptions.update(subscription);
		ChangeSubscriptionReportRequest reportRequest = new ChangeSubscriptionReportRequest(subscription.getSubscriptionId(),
				subscription.getStartDate(), DateTime.now());
		reportingService.reportChangeSubscription(reportRequest);
	}



	private LocationResponse getExistingLocation(Location location) {
		if (location == Location.NULL)
			return null;

		return reportingService.getLocation(location.getState(), location.getDistrict(), location.getBlock(), location.getPanchayat());
	}

	private void renewSchedule(Subscription subscription) {
		String subscriptionId = subscription.getSubscriptionId();
		logger.info(String.format("Processing renewal for subscriptionId: %s", subscriptionId));
		campaignMessageAlertService.scheduleCampaignMessageAlertForRenewal(subscriptionId, subscription.getMsisdn(), subscription.getOperator().name());
		rescheduleSubscriptionCompletionIfExists(subscription);
	}

	private void rescheduleSubscriptionCompletionIfExists(Subscription subscription) {
		if (!subscription.isCampaignCompleted()) return;
		scheduleCompletion(subscription, DateTime.now());
		logger.info(String.format("Rescheduled the completion of subscription %s to now", subscription.getSubscriptionId()));
	}

	private void scheduleCampaign(CampaignRescheduleRequest campaignRescheduleRequest, DateTime nextAlertDateTime) {
		String campaignName = MessageCampaignPack.from(campaignRescheduleRequest.getReason().name()).getCampaignName();
		MessageCampaignRequest enrollRequest = new MessageCampaignRequest(campaignRescheduleRequest.getSubscriptionId(),
				campaignName, nextAlertDateTime);
		messageCampaignService.start(enrollRequest, 0, kilkariPropertiesData.getCampaignScheduleDeltaMinutes());
	}

	private void scheduleDeactivation(String subscriptionId, final DateTime deactivationDate, String reason, Integer graceCount, String mode) {
		String subjectKey = SubscriptionEventKeys.DEACTIVATE_SUBSCRIPTION;
		Date startDate = DateTime.now().plusDays(kilkariPropertiesData.getBufferDaysToAllowRenewalForDeactivation()).toDate();
		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put(MotechSchedulerService.JOB_ID_KEY, subscriptionId);
		parameters.put("0", new ScheduleDeactivationRequest(subscriptionId, deactivationDate, reason, graceCount, mode));

		MotechEvent motechEvent = new MotechEvent(subjectKey, parameters);
		RunOnceSchedulableJob runOnceSchedulableJob = new RunOnceSchedulableJob(motechEvent, startDate);

		motechSchedulerService.safeScheduleRunOnceJob(runOnceSchedulableJob);
	}

	private void scheduleCampaign(Subscription subscription, DateTime activatedOn) {
		String campaignName = MessageCampaignPack.from(subscription.getPack().name()).getCampaignName();
		MessageCampaignRequest campaignRequest = new MessageCampaignRequest(
				subscription.getSubscriptionId(), campaignName, activatedOn);
		messageCampaignService.start(campaignRequest, kilkariPropertiesData.getCampaignScheduleDeltaDays(), kilkariPropertiesData.getCampaignScheduleDeltaMinutes());
	}

	private void removeScheduledMessagesFromOBD(String subscriptionId) {
		campaignMessageService.deleteCampaignMessagesFor(subscriptionId);
		campaignMessageAlertService.clearMessageId(subscriptionId);
	}

	private DateTime getBufferedDateTime(DateTime dateTime) {
		return dateTime.plusDays(kilkariPropertiesData.getCampaignScheduleDeltaDays())
				.plusMinutes(kilkariPropertiesData.getCampaignScheduleDeltaMinutes());
	}

	private void activateSchedule(Subscription subscription) {
		String subscriptionId = subscription.getSubscriptionId();
		logger.info(String.format("Processing activation for subscriptionId: %s", subscriptionId));

		String currentMessageId = campaignMessageAlertService.scheduleCampaignMessageAlertForActivation(subscriptionId, subscription.getMsisdn(), subscription.getOperator().name());

		if (currentMessageId != null)
			inboxService.newMessage(subscriptionId, currentMessageId);
	}

	private void updateStatusAndReport(Subscription subscription, DateTime updatedOn, String reason, String operator,
			Integer graceCount, String mode, Action<Subscription> action) {
		action.perform(subscription);
		logger.info("Updating Subscription and reporting change " + subscription.toString());
		allSubscriptions.update(subscription);
		reportingService.reportSubscriptionStateChange(new SubscriptionStateChangeRequest(subscription.getSubscriptionId(),
				subscription.getStatus().name(), reason, updatedOn, operator, graceCount, getSubscriptionWeekNumber(subscription, updatedOn), mode));
	}



	private void createSubscriptionAndReport(Subscription subscription, DateTime updatedOn, String reason, String operator,
			Integer graceCount, String mode, Action<Subscription> action) {
		action.perform(subscription);
		logger.info("adding Subscription and reporting it " + subscription.toString());
		allSubscriptions.add(subscription);
		SubscriptionRequest subscriptionRequest = new SubscriptionRequest(subscription.getMsisdn(), subscription.getCreationDate(), subscription.getPack(), null, null, reason, subscription.getReferredBy(),subscription.isReferredByFLW(),mode);
		SubscriptionReportRequest reportRequest = SubscriptionMapper.createSubscriptionCreationReportRequest(
				subscription, Channel.IVR, subscriptionRequest, getSubscriptionWeekNumber(subscription, updatedOn), operator, true);
		reportingService.reportSubscriptionCreation(reportRequest);
		logger.info("done");
	}

	private void updateStatusAndReportForSM(Subscription subscription, DateTime updatedOn, String reason, String operator,
			Integer graceCount, String mode, Action<Subscription> action) {
		action.perform(subscription);
		logger.info("Updating Subscription and reporting change " + subscription.toString());
		allSubscriptions.update(subscription);
		SubscriptionRequest subscriptionRequest = new SubscriptionRequest(subscription.getMsisdn(), subscription.getCreationDate(), subscription.getPack(), null, null, reason, subscription.getReferredBy(),subscription.isReferredByFLW(),mode);
		SubscriptionReportRequest reportRequest = SubscriptionMapper.createSubscriptionCreationReportRequest(
				subscription, Channel.IVR, subscriptionRequest, getSubscriptionWeekNumber(subscription, updatedOn), operator, true);
		reportingService.reportSubscriptionCreation(reportRequest);
		logger.info("done.");
	}

	private Integer getSubscriptionWeekNumber(Subscription subscription, DateTime endDate) {
		if (subscription.getScheduleStartDate() == null)
			return null;
		//not applying workaround here as the flow is not scheduler related.
		DateTimeZone ISTTimeZone = DateTimeZone.getDefault();
		Integer diffInWeeks = Weeks.weeksBetween(subscription.getScheduleStartDate().withZone(ISTTimeZone), endDate).getWeeks();
		return subscription.getPack().getStartWeek() + diffInWeeks;
	}

	private void updateStatusWithoutReporting(Subscription subscription, Action<Subscription> action) {
		action.perform(subscription);
		logger.info("Updating Subscription without reporting change " + subscription);
		allSubscriptions.update(subscription);
	}

	private boolean shouldChangeMsisdn(Subscription subscription, ChangeMsisdnRequest changeMsisdnRequest) {
		if (changeMsisdnRequest.getShouldChangeAllPacks()) return true;

		return changeMsisdnRequest.getPacks().contains(subscription.getPack());
	}

	private void migrateMsisdnToNewSubscription(Subscription subscription, ChangeMsisdnRequest changeMsisdnRequest) {
		SubscriberResponse subscriberResponse = reportingService.getSubscriber(subscription.getSubscriptionId());

		requestDeactivation(new DeactivationRequest(subscription.getSubscriptionId(), changeMsisdnRequest.getChannel(), changeMsisdnRequest.getCreatedAt(), changeMsisdnRequest.getReason(),"msisdnChangeRequest-Serco"));

		Location location = null;
		LocationResponse locationResponse = subscriberResponse.getLocationResponse();
		if (locationResponse != null) {
			location = new Location(locationResponse.getState(),
					locationResponse.getDistrict(),
					locationResponse.getBlock(), locationResponse.getPanchayat());
		}
		Subscriber subscriber = new Subscriber(subscriberResponse.getBeneficiaryName(), subscriberResponse.getBeneficiaryAge(),
				subscriberResponse.getDateOfBirth(), subscriberResponse.getExpectedDateOfDelivery(), subscription.getNextWeekNumber());

		SubscriptionRequest subscriptionRequest = new SubscriptionRequest(changeMsisdnRequest.getNewMsisdn(),
				changeMsisdnRequest.getCreatedAt(), subscription.getPack(), location, subscriber, changeMsisdnRequest.getReason(), null, false,"msisdnChangeRequest-Serco");
		subscriptionRequest.setOldSubscriptionId(subscription.getSubscriptionId());

		createSubscription(subscriptionRequest, changeMsisdnRequest.getChannel());
	}

	private void changeMsisdnForEarlySubscription(Subscription subscription, ChangeMsisdnRequest changeMsisdnRequest) {
		subscription.setMsisdn(changeMsisdnRequest.getNewMsisdn());
		allSubscriptions.update(subscription);
		reportingService.reportChangeMsisdnForEarlySubscription(new SubscriberChangeMsisdnReportRequest(subscription.getSubscriptionId(), Long.valueOf(changeMsisdnRequest.getNewMsisdn()), changeMsisdnRequest.getReason(), changeMsisdnRequest.getCreatedAt()));
	}

	public void updateReferredByMsisdn(Subscription subscription, ChangeSubscriptionRequest changeSubscriptionRequest) {  	
		subscription.setReferredByFLW(changeSubscriptionRequest.isReferredBy());
		subscription.setReferredBy(changeSubscriptionRequest.getReferredBy());
		allSubscriptions.update(subscription);
		reportingService.reportChangeReferredByFlwMsisdn(new SubscriptionChangeReferredFLWMsisdnReportRequest(subscription.getSubscriptionId(), changeSubscriptionRequest.getReferredBy(), changeSubscriptionRequest.getReason(), changeSubscriptionRequest.getCreatedAt(), changeSubscriptionRequest.isReferredBy()));	
	}

	public void updateSubscription(Subscription subscription) {
		try {
			allSubscriptions.update(subscription);
		} catch (IllegalArgumentException exception) {
			throw new IllegalArgumentException(String.format("Subscription %s does not exist in db", subscription.getSubscriptionId()));
		}
	}

	public List<Subscription>findByMsisdnPackAndStatus(String msisdn, SubscriptionPack pack, SubscriptionStatus status){
		return allSubscriptions.findByMsisdnPackAndStatus(msisdn, pack, status);
	}

	public Subscription updateSubscriptionForFlw(SubscriptionRequest subscriptionRequest, Channel channel) {
		subscriptionValidator.validate(subscriptionRequest);

		Subscription subscription = new Subscription(subscriptionRequest.getMsisdn(), subscriptionRequest.getPack(),
				subscriptionRequest.getCreationDate(), subscriptionRequest.getSubscriptionStartDate(), subscriptionRequest.getSubscriber().getWeek(), subscriptionRequest.getReferredBy(), subscriptionRequest.isReferredByFLW());
		allSubscriptions.update(subscription);
		return subscription;
	}

	public void createEntryInCouchForReferredBy(Subscription subscription) {
		allSubscriptions.add(subscription);
	}

	public List<Subscription> getAllSortedByDate(DateTime startDate, DateTime endDate) {
		List<Subscription> subscriptionList = allSubscriptions.findByActivationDate(startDate, endDate);
		Collections.sort(subscriptionList,new SubscriptionComparator());
		return subscriptionList;
	}

	public boolean isTransitionFromActiveOrSuspendedToNewEarly(Subscription subscription,ChangeSubscriptionRequest changeSubscriptionRequest) {
		Subscriber subscriber = new Subscriber(null, null, changeSubscriptionRequest.getDateOfBirth(), changeSubscriptionRequest.getExpectedDateOfDelivery(), null);
		SubscriptionRequest subscriptionRequest = new SubscriptionRequest(changeSubscriptionRequest.getMsisdn(), changeSubscriptionRequest.getCreatedAt(), changeSubscriptionRequest.getPack(), null, subscriber, changeSubscriptionRequest.getReason(), subscription.getReferredBy(), subscription.isReferredByFLW(),"subscriptionChangeRequest-Serco");
		if(subscriptionRequest.getSubscriptionStartDate().isAfter(subscriptionRequest.getCreationDate()) && !subscription.getStatus().equals(SubscriptionStatus.NEW_EARLY)){
			return true;
		}		
		return false;
	}

	public Subscription subscriptionInActiveSuspendedOrGrace(String msisdn,
			SubscriptionPack pack) {
		return allSubscriptions.subscriptionInActiveSuspendedOrGrace(msisdn, pack);
	}


}
