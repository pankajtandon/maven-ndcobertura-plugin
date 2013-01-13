package com.nayidisha.plugins.cobertura;


import java.io.File;
import java.text.DecimalFormat;
import java.util.Collection;

import net.sourceforge.cobertura.coveragedata.ClassData;
import net.sourceforge.cobertura.coveragedata.CoverageDataFileHandler;
import net.sourceforge.cobertura.coveragedata.ProjectData;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.StringUtils;


/**
 * Goal: Checks if a class has required coverage....
 *
 * @goal showCoverage
 * @phase validate
 */
public class CoberturaCheckMojo extends AbstractCoberturaMojo {
   
    /**
     * Class to show the coverage for. If no class is specified then only statistics for the entire code base are shown.
     * @parameter expression="${classToTest}"
     */
    private String classToTest;
    

  
	public void execute() throws MojoExecutionException {
		
		if (skip){
			this.getLog().info("Skipping...");
			return;
		}
		try {

	        if ( ( fullPathToDataFile == null ) || !fullPathToDataFile.exists() ){
	            throw new MojoExecutionException( "Unable to find nonexistent dataFile [" + fullPathToDataFile + "]. Please ensure that you have run the cobertura-maven-plugin with the instrument goal and created the dataFile, BEFORE you run this plugin" );
	        }
	        
	        ProjectData projectData = CoverageDataFileHandler.loadCoverageData( fullPathToDataFile );
	        if (projectData == null){
	        	throw new MojoExecutionException("No instrumented classes found. Please ensure that you have run the instrumentation goal of the cobertura-maven-plugin before running this plugin.");
	        }
	        this.getLog().info("");
	        Collection<ClassData> collection = projectData.getClasses();
	        
	        long coveredLines = 0;
	        long totalLines = 0;
	        boolean found = false;
	        DecimalFormat format = new DecimalFormat("#.##");
	        this.getLog().debug("ClassToTest is : " + this.classToTest); 
	        for (ClassData classData : collection) {
				String c = classData.getSourceFileName();
				if ((!StringUtils.isEmpty(classToTest)) && (c.indexOf(this.classToTest) != -1)){
					Double lineCoverageRate = classData.getLineCoverageRate();
					Double branchCoverageRate = classData.getBranchCoverageRate();
					lineCoverageRate = Double.valueOf(format.format(lineCoverageRate));
					branchCoverageRate = Double.valueOf(format.format(branchCoverageRate));
					this.getLog().info(c + ": LineCoverageRate: " + lineCoverageRate + " (" + classData.getNumberOfCoveredLines() + " out of " + classData.getNumberOfValidLines() + " lines)" 
							+ " and BranchCoverageRate: " + branchCoverageRate + " (" + classData.getNumberOfCoveredBranches() + " out of " + classData.getNumberOfValidBranches() + " branches)." );
					found = true;
				}
				coveredLines += classData.getNumberOfCoveredLines();
				totalLines += classData.getNumberOfValidLines();
			}
	        this.getLog().info("");
	        if (found){
	        	this.getLog().info("Please see line-by-line coverage for these classes by running mvn ndcobertura:generateReports");
	        } else {
	        	if (!StringUtils.isEmpty(classToTest)){
	        		this.getLog().info("Specified class not found. Please check the spelling and/or case and try again.");
	        	}
	        }
	        double coveredLinesD = Double.parseDouble(coveredLines + "");
	        double totalLinesD = Double.parseDouble(totalLines + "");
	        String coverageRateString = (totalLinesD != 0?( ", Total Coverage Rate: " +(coveredLinesD/totalLinesD)):", Total Coverage Rate not defined");
	        
	        this.getLog().info(" Covered Lines: " + coveredLines + ", Total Lines: " + totalLines  + coverageRateString);

		} catch (Exception e) {
			throw new MojoExecutionException("Error processing ", e);
		}

	}
    
    
}


