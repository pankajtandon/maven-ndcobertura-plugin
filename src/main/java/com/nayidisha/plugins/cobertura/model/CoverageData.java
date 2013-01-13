package com.nayidisha.plugins.cobertura.model;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

public class CoverageData implements Comparable{
	
	
	private Date pointInTime;
	
	private long numberOfCoveredLines;
	
	private long numberOfValidLines;
	
	private long numberOfCoveredBranches;
	
	private long numberOfValidBranches;
	
	private String projectName;
	
	
	public String toStringLong(){
		return "Date: " + pointInTime.toString() + ", Number of Covered Lines: " 
		+ numberOfCoveredLines + ", Number of Valid Lines: " + numberOfValidLines 
		+ ", Number of Covered Branches: " + numberOfCoveredBranches 
		+ ", Number of Valid Branches: " + numberOfValidBranches ;
	}
	
	@Override
	public int compareTo(Object o) {
		int i = 0;
		if (o instanceof CoverageData){
			CoverageData that = (CoverageData)o;
			if(this.getPointInTime().after(that.getPointInTime())){
				i = 1;
			} else {
				i = -1;
			}
		}
		return i;
	}
	
	//Will be used for legend on JFreeChart. So make sure that this is not verbose.
	public String toString(){
		return DateFormatUtils.format(this.getPointInTime(), "MMM dd, yy");// + this.toStringLong();
	}
	
	
	//---------------

	public Date getPointInTime() {
		return pointInTime;
	}

	public void setPointInTime(Date pointInTime) {
		this.pointInTime = pointInTime;
	}

	public long getNumberOfCoveredLines() {
		return numberOfCoveredLines;
	}

	public void setNumberOfCoveredLines(long numberOfCoveredLines) {
		this.numberOfCoveredLines = numberOfCoveredLines;
	}

	public long getNumberOfValidLines() {
		return numberOfValidLines;
	}

	public void setNumberOfValidLines(long numberOfValidLines) {
		this.numberOfValidLines = numberOfValidLines;
	}

	public long getNumberOfCoveredBranches() {
		return numberOfCoveredBranches;
	}

	public void setNumberOfCoveredBranches(long numberOfCoveredBranches) {
		this.numberOfCoveredBranches = numberOfCoveredBranches;
	}

	public long getNumberOfValidBranches() {
		return numberOfValidBranches;
	}

	public void setNumberOfValidBranches(long numberOfValidBranches) {
		this.numberOfValidBranches = numberOfValidBranches;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}




}
