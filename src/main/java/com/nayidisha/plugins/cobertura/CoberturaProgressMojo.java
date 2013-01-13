package com.nayidisha.plugins.cobertura;

import java.beans.XMLDecoder;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.nayidisha.plugins.cobertura.model.CoverageData;


/**
 * Goal: Shows coverage data over time
 *
 * @goal showProgress
 * @phase validate
 */

public class CoberturaProgressMojo extends AbstractCoberturaMojo {

	/**
	 * Full path of the file that stores data.
	 * @parameter expression="${storageFileFullPath}" 
	 * @required
	 */
	protected String storageFileFullPath;
	
	/**
	 * Full path of the directory where progress report is stored.
	 * @parameter expression="${progressReportLocation}" default-value="${project.build.directory}/cobertura-progress-report"
	 * 
	 */
	protected String progressReportLocation;
	
	
	
	/**
	 * Name of the progress report html file
	 * @parameter expression="${progressReportName}" default-value="progressReport.html"
	 * 
	 */
	protected String progressReportName;
	
	
	/**
	 * This is the name that will appear after the verbiage: "Cobertura Coverage Report for Project: xxx" in the header of the progress report.
	 * 
	 * @parameter expression="${projectName}" default-value="${project.name}"
	 */
	protected String projectName;
	
	
	private String LINE_COVERAGE_CHART_FILE = "lineCoverageChart.png";
	private String BRANCH_COVERAGE_CHART_FILE = "branchCoverageChart.png";
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		if (skip){
			this.getLog().info("Skipping...");
			return;
		}
		
		ArrayList<CoverageData>  list = readData(storageFileFullPath);

		this.getLog().debug("Coverage Data read is : " + list.toString());

		buildCharts(list);

		buildReport();
		

	}

	private void buildCharts(ArrayList<CoverageData> list)
			throws MojoExecutionException {
		DefaultCategoryDataset lineDataset = new DefaultCategoryDataset();
		
		this.getLog().info("List before: " + list);
		
		//We want only 10 ticks/bars. Haven't made this (num of ticks) a parameter because I'm just being lazy.
		//Start with 9, resulting in 10 ticks.
		list = (ArrayList<CoverageData>) CoverageDataManager.trimCoverageData(list, 9);
		
		this.getLog().info("List after: " + list);
		
		for (CoverageData coverageData : list) {
			long covered = coverageData.getNumberOfCoveredLines() ;
			lineDataset.addValue(new Double(covered),  "covered", coverageData);
			lineDataset.addValue(new Double(coverageData.getNumberOfValidLines() - covered),  "total lines", coverageData);
			this.getLog().debug("Looping: " + coverageData + " "  + new Double(coverageData.getNumberOfCoveredLines() ));
		}
		this.getLog().info("Line Dataset: " + lineDataset);
		
		
		
		JFreeChart lineCoverageChart = ChartFactory.createStackedBarChart
		                     ("Line coverage",  // Title
		                      "Date",           // X-Axis label
		                      "Number of Lines",       // Y-Axis label
		                      lineDataset,          // Dataset
		                      PlotOrientation.VERTICAL,
		                      true,               // Show legend
		                      true, 			  // tooltips
		                      false				  // urls
		                     );
		CategoryPlot categoryPlot = lineCoverageChart.getCategoryPlot();
		CategoryAxis categoryAxis = (CategoryAxis)categoryPlot.getDomainAxis();
		categoryAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);
		
		
		DefaultCategoryDataset branchDataset = new DefaultCategoryDataset();
		for (CoverageData coverageData : list) {
			long covered = coverageData.getNumberOfCoveredBranches();
			branchDataset.addValue(new Double(covered),  "covered", coverageData);
			branchDataset.addValue(new Double(coverageData.getNumberOfValidBranches() - covered),  "total branches", coverageData);
			this.getLog().debug("Looping: " + coverageData + " "  + new Double(coverageData.getNumberOfCoveredBranches() ));
		}
		
		this.getLog().info("Branch Dataset: " + branchDataset);
		
		JFreeChart branchCoverageChart = ChartFactory.createStackedBarChart
				        ("Branch coverage",  // Title
				         "Date",           // X-Axis label
				         "Number of Branches",       // Y-Axis label
				         branchDataset,          // Dataset
				         PlotOrientation.VERTICAL,
				         true,               // Show legend
				         true, 			  // tooltips
				         false				  // urls
				        );
		
		CategoryPlot categoryPlotForBranch = branchCoverageChart.getCategoryPlot();
		CategoryAxis categoryAxisForBranch = (CategoryAxis)categoryPlotForBranch.getDomainAxis();
		categoryAxisForBranch.setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);
		
		try {
			File f1 = new File(progressReportLocation + File.separator + this.LINE_COVERAGE_CHART_FILE);
			f1.getParentFile().mkdirs();
			ChartUtilities.saveChartAsPNG(f1, lineCoverageChart, 900, 500);
			
			File f2 = new File(progressReportLocation + File.separator + this.BRANCH_COVERAGE_CHART_FILE);
			f2.getParentFile().mkdirs();
			ChartUtilities.saveChartAsPNG(f2, branchCoverageChart, 900, 500);
			
		} catch (IOException ioe) {
			throw new MojoExecutionException("Cannot save chart to file ", ioe);
		}
	}
	
	
	private void buildReport() throws MojoExecutionException{
	    try{
			File f = new File(progressReportLocation + File.separator + progressReportName);
			f.getParentFile().mkdirs();
			FileOutputStream fileOutputStream = new FileOutputStream(f);
			BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

    	    StringBuffer sb = new StringBuffer();
    	    sb.append("<html>");
    	    sb.append("<body>");
    	    sb.append("<h2> Cobertura Coverage Report for Project: " + projectName + "</h2>");
    	    sb.append("<img src=\"" + this.LINE_COVERAGE_CHART_FILE + "\" />");
    	    sb.append("</BR>");
    	    sb.append("<img src=\"" + this.BRANCH_COVERAGE_CHART_FILE + "\" />");
    	    sb.append("</body>");
    	    sb.append("</html>");
    	    
    	    bos.write(sb.toString().getBytes());
    	    bos.close();

	    }catch (Exception e){
	    	throw new MojoExecutionException("Error writing report.", e);
	    }
	}

}
