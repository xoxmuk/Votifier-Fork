package ru.xoxmuk.votifierfork.model;

import lombok.Getter;
import lombok.Setter;

public class Vote {

	private String serviceName;

	private String username;

	private String address;

	private String timeStamp;

	@Getter
	@Setter

	private String sourceAddress;

	public Vote(String serviceName, String username, String address, String timeStamp) {
		this.serviceName = serviceName;
		this.username = username;
		this.address = address;
		this.timeStamp = timeStamp;
	}

	public Vote() {

	}

	@Override
	public String toString() {
		return "Vote (from:" + serviceName + " username:" + username + " address:" + address + " timeStamp:" + timeStamp
				+ ", sourceAddress:" + sourceAddress + ")";
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setUsername(String username) {
		this.username = username.length() <= 16 ? username : username.substring(0, 16);
	}

	public String getUsername() {
		return username;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress() {
		return address;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

}
