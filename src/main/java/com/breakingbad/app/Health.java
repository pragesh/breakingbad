package com.breakingbad.app;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


public class Health {

	private int positiveCount;
	
	private int totalCount;
	
	private List<String> positiveUsers = new ArrayList<>();
	
	private List<String> negativeUsers = new ArrayList<>();
	
	private List<String> inProgressUsers = new ArrayList<>();

	public int getPositiveCount() {
		return positiveCount;
	}

	public void setPositiveCount(int positiveCount) {
		this.positiveCount = positiveCount;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	
	public void updateCount(boolean isPositive, String user) {
		if(isPositive) {
			positiveCount++;
			addPostiveUser(user);
		} else {
			addNegativeUser(user);
		}
		totalCount++;
	}
	
	public double getPercentage() {
		if(totalCount == 0) {
			return Double.NaN;
		}
		return positiveCount/ (1.0 * totalCount) * 100;
	}
	
	public List<String> getPositiveUsers() {
		return positiveUsers;
	}

	public void setPositiveUsers(List<String> positiveUsers) {
		this.positiveUsers = positiveUsers;
	}

	public List<String> getNegativeUsers() {
		return negativeUsers;
	}

	public void setNegativeUsers(List<String> negativeUsers) {
		this.negativeUsers = negativeUsers;
	}
	
	public List<String> getInProgressUsers() {
		return inProgressUsers;
	}

	public void setInProgressUsers(List<String> inProgressUsers) {
		this.inProgressUsers = inProgressUsers;
	}

	public void addPostiveUser(String user) {
		positiveUsers.add(user);
	}
	
	public void addNegativeUser(String user) {
		negativeUsers.add(user);
	}
	
	public void addInProgressUser(String user) {
		inProgressUsers.add(user);
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this,
				ToStringStyle.MULTI_LINE_STYLE);
	}
}
