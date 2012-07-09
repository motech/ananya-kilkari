package org.motechproject.ananya.kilkari.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'CampaignMessage'")
public class CampaignMessage extends MotechBaseDataObject {

        @JsonProperty
        private String subscriptionId;

        @JsonProperty
        private String messageId;

        @JsonProperty
        private boolean renewed;

        public CampaignMessage() {

        }

        public CampaignMessage(String subscriptionId, String messageId) {
            this.subscriptionId = subscriptionId;
            this.messageId = messageId;
        }

        public CampaignMessage(String subscriptionId, String messageId, boolean renewed) {
            this.subscriptionId = subscriptionId;
            this.messageId = messageId;
            this.renewed = renewed;
        }

        public String getSubscriptionId() {
            return subscriptionId;
        }

        public String getMessageId() {
            return messageId;
        }

        public boolean isRenewed() {
            return renewed;
        }

        public void setMessageId(String messageId) {
            this.messageId = messageId;
        }
}