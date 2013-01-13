package com.nayidisha.plugins.cobertura;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;





import com.nayidisha.plugins.cobertura.model.CoverageData;

public class CoverageDataManager {
	
	private static Logger log = Logger.getLogger(CoverageDataManager.class.getName());
	
	public static List<CoverageData>  trimCoverageData(List<CoverageData> coverageDataList, int numberOfPoints){
		List<CoverageData> finalList = null;
		if (coverageDataList != null && coverageDataList.size() <= numberOfPoints){
			finalList = coverageDataList;
		} else {
			finalList = new ArrayList<CoverageData>();
			ArrayList<CoverageData> extraList = new ArrayList<CoverageData>();
			
			log.debug("Coverage Data List before sorting: " + coverageDataList);
			//Sort
			Collections.sort(coverageDataList);
			
			//Since the default comparable is ascending, reverse
			Collections.reverse(coverageDataList);
			
			log.debug("Coverage Data List after sorting and reverseing: " + coverageDataList);
			int i = 0;
			for (CoverageData coverageData : coverageDataList) {
				if (i < numberOfPoints){
					finalList.add(coverageData);
				} else {
					extraList.add(coverageData);
				}
				i++;
			}
			double count = 0;
			double pit = 0;
			double totalNumberOfCoveredLines = 0;
			double totalNumberOfValidLines = 0;
			double totalNumberOfCoveredBranches = 0;
			double totalNumerOfValidBranches = 0;
			log.debug("Length of extraList: " + extraList.size());
			for (CoverageData coverageData : extraList) {
				pit += coverageData.getPointInTime().getTime();
				totalNumberOfCoveredLines += coverageData.getNumberOfCoveredLines() ;
				totalNumberOfValidLines += coverageData.getNumberOfValidLines();
				totalNumberOfCoveredBranches += coverageData.getNumberOfCoveredBranches();
				totalNumerOfValidBranches += coverageData.getNumberOfValidBranches();
				count++;
			}
			
			CoverageData cd = new CoverageData();
			Date averageDate = new Date(new Double(pit/count).longValue());
			log.debug("Average Date: " + averageDate);
			cd.setPointInTime(averageDate);
			cd.setNumberOfCoveredLines(new Double(totalNumberOfCoveredLines/count).longValue());
			log.debug("Num lines: " + new Double(totalNumberOfCoveredLines/count).longValue());
			cd.setNumberOfValidLines(new Double(totalNumberOfValidLines/count).longValue());
			cd.setNumberOfCoveredBranches(new Double(totalNumberOfCoveredBranches/count).longValue());			
			cd.setNumberOfValidBranches(new Double(totalNumerOfValidBranches/count).longValue());
			log.debug("Num branches: " + new Double(totalNumberOfCoveredBranches/count).longValue());
			finalList.add(cd);
			
			//Sort again
			Collections.sort(finalList);
		}
		
		return finalList;
	}

}
