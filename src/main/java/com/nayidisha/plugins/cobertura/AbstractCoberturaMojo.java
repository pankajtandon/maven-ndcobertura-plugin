package com.nayidisha.plugins.cobertura;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import com.nayidisha.plugins.cobertura.model.CoverageData;

public abstract class AbstractCoberturaMojo extends AbstractMojo{
	   
    /** 
     * Should this goal be skipped
     * @parameter expression="${cobertura.showCoverage.skip}" default-value="false"
     * */
    protected boolean skip;
    
     
    /**
     * <p>
     * The Datafile Location. The datafile is created as a result of the instrumentation of the code.
     * </p>
     * 
     * @parameter expression="${cobertura.dataFile}" default-value="${project.build.directory}/cobertura/cobertura.ser"
     * @required
     * @readonly
     */
    protected File fullPathToDataFile;

    protected void storeData(ArrayList<CoverageData> list, String storageFileFullPath) throws FileNotFoundException {
		//Write to output location
		File f = new File(storageFileFullPath);
		f.getParentFile().mkdirs();
		FileOutputStream fileOutputStream = new FileOutputStream(f);
		XMLEncoder encoder = new XMLEncoder(fileOutputStream); 
		encoder.writeObject(list);
		encoder.close();
	}
	
    
    protected ArrayList<CoverageData> readData(String storageFileFullPath) throws MojoExecutionException{
    	ArrayList<CoverageData> list = null;
		try {
			//read in the stored object
			File f = new File(storageFileFullPath);
			boolean boo = f.exists();
			if (boo){
				XMLDecoder decoder = new XMLDecoder(new FileInputStream(storageFileFullPath));
				list = (ArrayList<CoverageData>)decoder.readObject();
			}
		} catch (FileNotFoundException fnfe){
			throw new MojoExecutionException("Could not read recorded data from " + storageFileFullPath, fnfe);
		}
		return list;
    }
}
