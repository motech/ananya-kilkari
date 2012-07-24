package org.motechproject.ananya.kilkari.subscription.domain;

import org.joda.time.DateTime;

public class Subscriber {

    private String beneficiaryName;
    private int beneficiaryAge;
    private DateTime dateOfBirth;
    private DateTime expectedDateOfDelivery;

    public static final Subscriber NULL = new Subscriber();

    private Subscriber() {
    }

    public Subscriber(String beneficiaryName, int beneficiaryAge, DateTime dateOfBirth, DateTime expectedDateOfDelivery) {
        this.beneficiaryName = beneficiaryName;
        this.beneficiaryAge = beneficiaryAge;
        this.dateOfBirth = dateOfBirth;
        this.expectedDateOfDelivery = expectedDateOfDelivery;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public int getBeneficiaryAge() {
        return beneficiaryAge;
    }

    public DateTime getDateOfBirth() {
        return dateOfBirth;
    }

    public DateTime getExpectedDateOfDelivery() {
        return expectedDateOfDelivery;
    }
}
