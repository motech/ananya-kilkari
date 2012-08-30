package org.motechproject.ananya.kilkari.request;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.ananya.kilkari.obd.service.validator.Errors;
import org.motechproject.ananya.kilkari.request.validator.WebRequestValidator;

import java.util.List;

public class ChangeMsisdnWebRequest {

    @JsonIgnore
    private String channel;

    @JsonProperty
    private String oldMsisdn;

    @JsonProperty
    private String newMsisdn;

    @JsonProperty
    private List<String> packs;


    public ChangeMsisdnWebRequest() { }

    public ChangeMsisdnWebRequest(String oldMsisdn, String newMsisdn, List<String> packs, String channel) {
        this.oldMsisdn = oldMsisdn;
        this.newMsisdn = newMsisdn;
        this.packs = packs;
        this.channel = channel;
    }

    public Errors validate() {
        Errors errors;
        WebRequestValidator webRequestValidator = new WebRequestValidator();
        webRequestValidator.validateChannel(channel);
        webRequestValidator.validateMsisdn(oldMsisdn);
        webRequestValidator.validateMsisdn(newMsisdn);
        errors = webRequestValidator.getErrors();
        validateOldAndNewMsisdnsAreDifferent(errors);
        validateSubscriptionPacksForChangeMsisdn(errors);
        return errors;
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

    public String getChannel() {
        return channel;
    }

    public void setOldMsisdn(String oldMsisdn) {
        this.oldMsisdn = oldMsisdn;
    }

    public void setNewMsisdn(String newMsisdn) {
        this.newMsisdn = newMsisdn;
    }

    public void setPacks(List<String> packs) {
        this.packs = packs;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    private void validateSubscriptionPacksForChangeMsisdn(Errors errors) {
        if (packs.size() <= 0) errors.add("At least one pack should be specified");

        boolean allPackPresent = false;
        for (String pack : packs) {
            if (StringUtils.trim(pack).toUpperCase().equals("ALL")) allPackPresent = true;
        }
        if (allPackPresent && packs.size() != 1) errors.add("No other pack allowed when ALL specified");

        if (allPackPresent) return;

        WebRequestValidator requestValidator = new WebRequestValidator();
        for (String pack : packs) {
            requestValidator.validatePack(pack);
        }

        if(requestValidator.getErrors().hasErrors())
            errors.add(requestValidator.getErrors().allMessages());
    }

    private void validateOldAndNewMsisdnsAreDifferent(Errors errors) {
        if(oldMsisdn.equals(newMsisdn))
            errors.add("Old and new msisdn cannot be same");
    }

}
