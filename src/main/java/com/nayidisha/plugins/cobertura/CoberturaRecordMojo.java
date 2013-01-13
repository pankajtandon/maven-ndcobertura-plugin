package com.nayidisha.plugins.cobertura;

import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import net.sourceforge.cobertura.coveragedata.ClassData;
import net.sourceforge.cobertura.coveragedata.CoverageDataFileHandler;
import net.sourceforge.cobertura.coveragedata.ProjectData;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.nayidisha.plugins.cobertura.model.CoverageData;

/**
 * Goal: Checks if a class has required coverage....
 *
 * @goal recordCoverage
 * @phase validate
 */
public class CoberturaRecordMojo extends AbstractCoberturaMojo {

	/**
	 * Full path of the file that stores data.
	 * @parameter expression="${storageFileFullPath}" 
	 * @required
	 */
	protected String storageFileFullPath;
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
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
	        long coveredBranches = 0;
	        long totalBranches  = 0;

	        for (ClassData classData : collection) {
				coveredLines += classData.getNumberOfCoveredLines();
				totalLines += classData.getNumberOfValidLines();
				
				coveredBranches += classData.getNumberOfCoveredBranches();
				totalBranches += classData.getNumberOfValidBranches();
				
			}
	        this.getLog().info("");

	        double coveredLinesD = Double.parseDouble(coveredLines + "");
	        double totalLinesD = Double.parseDouble(totalLines + "");
	        double coverageRateD = (coveredLinesD/totalLinesD);
	        
	        ArrayList<CoverageData> list = readData(storageFileFullPath);
	        
	        if (list == null){
	        	list = new ArrayList<CoverageData>();
	        }
	        
	        CoverageData cd = new CoverageData();
	        cd.setNumberOfCoveredLines(coveredLines);
	        cd.setNumberOfValidLines(totalLines);
	        cd.setNumberOfCoveredBranches(coveredBranches);
	        cd.setNumberOfValidBranches(totalBranches);
	        cd.setPointInTime(new Date());
	        list.add(cd);
	        
	        storeData(list, storageFileFullPath);
	        
	        String coverageRateString = (totalLinesD != 0?( ", Total Coverage Rate: " + coverageRateD):", Total Coverage Rate not defined");
	        
	        this.getLog().debug("Coverage data recorded for " + cd);

		} catch (Exception e) {
			throw new MojoExecutionException("Error processing ", e);
		}

	}



}
