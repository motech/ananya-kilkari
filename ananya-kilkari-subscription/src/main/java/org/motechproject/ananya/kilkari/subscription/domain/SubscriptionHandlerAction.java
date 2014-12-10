package org.motechproject.ananya.kilkari.subscription.domain;


public enum SubscriptionHandlerAction {
	
    ACTIVATION("ACT","SUCCESS"), ACTIVATION_FAIL("ACT","BAL-LOW"), RENEWAL("REN","SUCCESS"), RENEWAL_SUSPENSION("REN","BAL-LOW"), DEACTIVATION("DCT","SUCCESS"), ACTIVATION_GRACE("ACT","GRACE") ;

    private String action;
    private String status;
    
	private SubscriptionHandlerAction(String action, String status) {
		this.action = action;
		this.status = status;
	}

	public String getAction() {
		return action;
	}

	public String getStatus() {
		return status;
	}


}
