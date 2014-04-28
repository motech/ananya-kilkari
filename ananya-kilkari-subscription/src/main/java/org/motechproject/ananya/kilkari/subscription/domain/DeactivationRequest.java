package org.motechproject.ananya.kilkari.subscription.domain;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.obd.domain.Channel;

public class DeactivationRequest {

    private String subscriptionId;
    private Channel channel;
    private DateTime createdAt;
    private String reason;
    private String mode;

    public DeactivationRequest(String subscriptionId, Channel channel, DateTime createdAt, String reason ,String mode) {
        this.subscriptionId = subscriptionId;
        this.channel = channel;
        this.createdAt = createdAt;
        this.reason = reason;
        this.mode = mode;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public Channel getChannel() {
        return channel;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public String getReason() {
        return reason;
    }

	public String getMode() {
		return mode;
	}
    
    
}
