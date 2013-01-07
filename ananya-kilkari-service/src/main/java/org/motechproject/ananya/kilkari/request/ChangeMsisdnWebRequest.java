package org.motechproject.ananya.kilkari.request;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.ananya.kilkari.obd.service.validator.Errors;
import org.motechproject.ananya.kilkari.request.validator.WebRequestValidator;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

@XmlRootElement(name = "subscriber")
public class ChangeMsisdnWebRequest {

    @JsonIgnore
    @XmlTransient
    private String channel;

    @JsonProperty
    @XmlElement
    private String oldMsisdn;

    @JsonProperty
    @XmlElement
    private String newMsisdn;

    @JsonProperty
    @XmlElement
    private String reason;

    @JsonProperty
    @XmlElementWrapper(name = "packs")
    @XmlElement(name = "pack")
    private List<String> packs;


    public ChangeMsisdnWebRequest() { }

    public ChangeMsisdnWebRequest(String oldMsisdn, String newMsisdn, List<String> packs, String channel, String reason) {
        this.oldMsisdn = oldMsisdn;
        this.newMsisdn = newMsisdn;
        this.packs = packs;
        this.channel = channel;
        this.reason = reason;
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

    @XmlTransient
    public String getOldMsisdn() {
        return oldMsisdn;
    }

    @XmlTransient
    public String getNewMsisdn() {
        return newMsisdn;
    }

    @XmlTransient
    public List<String> getPacks() {
        return packs;
    }

    @XmlTransient
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

    @XmlTransient
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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
