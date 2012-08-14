package org.motechproject.ananya.kilkari.request;

import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.ananya.kilkari.request.validator.WebRequestValidator;
import org.motechproject.ananya.kilkari.subscription.validators.Errors;

import java.util.List;

public class ChangeMsisdnWebRequest extends BaseWebRequest {

    @JsonProperty
    private String oldMsisdn;

    @JsonProperty
    private String newMsisdn;

    @JsonProperty
    private List<String> packs;


    public ChangeMsisdnWebRequest() {
    }

    public ChangeMsisdnWebRequest(String oldMsisdn, String newMsisdn, List<String> packs, String channel) {
        this.oldMsisdn = oldMsisdn;
        this.newMsisdn = newMsisdn;
        this.packs = packs;
        this.channel = channel;
    }

    public Errors validate() {
        WebRequestValidator webRequestValidator = new WebRequestValidator();
        webRequestValidator.validateChannel(channel);
        webRequestValidator.validateMsisdn(oldMsisdn);
        webRequestValidator.validateMsisdn(newMsisdn);
        webRequestValidator.validateSubscriptionPacksForChangeMsisdn(packs);
        return webRequestValidator.getErrors();
    }

    public String getOldMsisdn() {
        return oldMsisdn;
    }

    public String getNewMsisdn() {
        return newMsisdn;
    }

    public List<String> getPacks() {
        return packs;
    }
}
