package org.motechproject.ananya.kilkari.message.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'InboxMessage'")
@JsonIgnoreProperties(ignoreUnknown = true)
public class InboxMessage extends MotechBaseDataObject {
    @JsonProperty
    private String subscriptionId;
    @JsonProperty
    private String messageId;

    public InboxMessage(){
    }

    public InboxMessage(String subscriptionId, String messageId) {
        this.subscriptionId = subscriptionId;
        this.messageId = messageId;
    }

    @JsonIgnore
    public String getSubscriptionId() {
        return subscriptionId;
    }

    @JsonIgnore
    public String getMessageId() {
        return messageId;
    }

    @JsonIgnore
    public void update(String messageId) {
        this.messageId = messageId;
    }
}
