package org.motechproject.ananya.kilkari.subscription.repository;


import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionHandlerAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

@Component
public class KilkariPropertiesData {
	
	private Properties kilkariProperties;
	/*::to store valid actions and status like ACT_SUCCESS, DCT_BAL-LOW , SUS_BAL-LOW , SUS_SUCCESS etc*/
	private ArrayList<String> validActionStatusList = new ArrayList<>();
	/*::to store which handler to invoke for each action and status "ACT_SUCCESS":"ACTIVATE" */
	private HashMap<String, SubscriptionHandlerAction> handlerMap = new HashMap<>();
	/*::to store actions like ACT, DCT , REN , SUS etc*/
	private ArrayList<String> actionList =  new ArrayList<>();

	@Autowired
	public KilkariPropertiesData(@Qualifier("kilkariProperties") Properties kilkariProperties) {
		this.kilkariProperties = kilkariProperties;
		populateStatusAction();
	}

	private void populateStatusAction() {
		populateAvailableActions();
		populateAvailableCallbackStatus();
		populateHandlerActionForCallBack();
	}


	private void populateAvailableActions() {
		String propertyName= "kilkari.available.subscription.actions";
		String propertyValue = kilkariProperties.getProperty(propertyName);
		if (propertyValue == null) {
			throw new RuntimeException(String.format("%s property should be available.", propertyName));
		}

		String[] subscriptionActions = propertyValue.split(",");
		for (String subscriptionAction : subscriptionActions) {
			subscriptionAction = StringUtils.trim(subscriptionAction);
			if (StringUtils.isEmpty(subscriptionAction)) continue;
			actionList.add(subscriptionAction.toUpperCase());
		}
	}

	private void populateAvailableCallbackStatus() {
		for(String action:actionList){
			String propertyName= String.format("valid.callbackstatus.%s", action.toUpperCase());
			String propertyValue = kilkariProperties.getProperty(propertyName);
			if (propertyValue == null) {
				throw new RuntimeException(String.format("%s property should be available.", propertyName));
			}

			String[] callBackStatuses = propertyValue.split(",");
			for (String callBackStatus : callBackStatuses) {
				callBackStatus = StringUtils.trim(callBackStatus);
				if (StringUtils.isEmpty(callBackStatus)) continue;
				validActionStatusList.add(action.toUpperCase()+"_"+callBackStatus.toUpperCase());
			}
		}
	}
	
	private void populateHandlerActionForCallBack() {
		for(SubscriptionHandlerAction handlerAction: SubscriptionHandlerAction.values()){
			String propertyName= String.format("subscription.callback.handler.%s",handlerAction.name());
			String propertyValue = kilkariProperties.getProperty(propertyName);
			if (propertyValue == null) {
				throw new RuntimeException(String.format("%s property should be available.", propertyName));
			}
			String[] statusActions = propertyValue.split(",");
			for (String action_status : statusActions) {
				action_status = StringUtils.trim(action_status).toUpperCase();
				if (StringUtils.isEmpty(action_status)) continue;
				handlerMap.put(action_status.toUpperCase(),handlerAction);
			}
		}
	}

	


	public int getCampaignScheduleDeltaDays() {
		return Integer.parseInt(kilkariProperties.getProperty("kilkari.campaign.schedule.delta.days"));
	}

	public int getCampaignScheduleDeltaMinutes() {
		return Integer.parseInt(kilkariProperties.getProperty("kilkari.campaign.schedule.delta.minutes"));

	}

	public int getBufferDaysToAllowRenewalForDeactivation() {
		return Integer.parseInt(kilkariProperties.getProperty("buffer.days.to.allow.renewal.for.pack.deactivation"));
	}
	
	public int getRetryCountForCompletionFlow() {
		return Integer.parseInt(kilkariProperties.getProperty("retry.count.for.completion.flow"));
	}

	public String getDefaultState() {
		return kilkariProperties.getProperty("location.default.state");
	}
	
	public List<String> getValidActionStatus(){
		return validActionStatusList;
	}
	
	public HashMap<String, SubscriptionHandlerAction> getHandlerMap() {
		return handlerMap;
	}
	
	//format for param is action_status. eg- ACT_SUCCESS, ACT_BAL-LOW
	public SubscriptionHandlerAction getHandlerAction(String action, String status){
		return handlerMap.get(action.toUpperCase()+"_"+status.toUpperCase());
	}
}
