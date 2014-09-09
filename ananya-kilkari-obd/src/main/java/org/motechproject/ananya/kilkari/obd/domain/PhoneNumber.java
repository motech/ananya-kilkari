package org.motechproject.ananya.kilkari.obd.domain;

import org.apache.commons.lang.StringUtils;

public class PhoneNumber {

	public static boolean isValid(String phoneNumber) {
		return validate(phoneNumber);
	}

	public static boolean isNotValid(String phoneNumber) {
		return !validate(phoneNumber);
	}

	private static boolean validate(String phoneNumber) {
		return StringUtils.isNotBlank(phoneNumber) && StringUtils.isNumeric(phoneNumber) && ((phoneNumber.length() == 10)||(phoneNumber.length()==11 && phoneNumber.startsWith("0")));
	}

	public static  String trimPhoneNumber(String phoneNumber){
		if(StringUtils.isNotBlank(phoneNumber) && StringUtils.isNumeric(phoneNumber)){
			if(phoneNumber.startsWith("+91")){
				return getLastTenDigit(phoneNumber.substring(3));
			}else if(phoneNumber.startsWith("91") && (phoneNumber.length()== 12)){
				return getLastTenDigit(phoneNumber.substring(2));
			}else if(phoneNumber.startsWith("0")){
				return getLastTenDigit(phoneNumber.substring(1));
			}else{
				return getLastTenDigit(phoneNumber);
			}}
		else{
			return phoneNumber;
		}
	}

	private static String getLastTenDigit(String phoneNumber) {
		if(phoneNumber.length()<=10){
			return phoneNumber;
		}else {
			return phoneNumber.substring(phoneNumber.length()-10);
		}
	}
}
