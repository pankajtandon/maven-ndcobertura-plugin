package com.nayidisha.plugins.cobertura;


import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.SystemUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;


/**
 * Goal: Generates reports from Cobertura data file.
 *
 * @goal generateReports
 * @phase validate
 */
public class CoberturaReportMojo  extends AbstractCoberturaMojo{
   
    /**
     * Maximum memory allocated to this process
     * @parameter expression="${maxmem}" default-value="64m"
     */
    private String maxmem;
    
    
    /**
     * <p>
     * Path to where the report will be generated. A directory will be created under the reportPath with the name ${project.artifactId}-cobertura-reports which will contain the reports.
     * @parameter expression="${reportPath}" default-value="${project.build.directory}"
     */
    protected String reportPath;
    

    /**
     * The name of the directory under reportPath where the reports will be generated.
     * 
     * @parameter  expression="${reportDirectoryName}" default-value="${project.artifactId}-cobertura-reports"
     */
    protected String reportDirectoryName;
    
    /**
     * The directory where the source lives. This is needed to show line coverage in the cobertura reports.
     * 
     * @parameter expression="${sourceDir}" default-value="${basedir}/src/main/java"
     */
    protected String sourceDir;
    

    /**
     * <i>Maven Internal</i>: List of artifacts for the plugin.
     * 
     * @parameter expression="${plugin.artifacts}"
     * @required
     * @readonly
     */
	protected List pluginClasspathList;
	
	
  
	public void execute() throws MojoExecutionException {
		
		
		if (skip){
			this.getLog().info("Skipping...");
			return;
		}
		
		boolean boo = false;
		if (fullPathToDataFile != null) {
			boo = fullPathToDataFile.exists();
		}
		
		if (!boo){
			throw new MojoExecutionException("Cannot find Cobertura data file (typically target/cobertura/cobertura.ser). Please instrument the code before running this goal.");
		}
		
		try {
	        
	        Commandline cl = new Commandline();
	        File java = new File( SystemUtils.getJavaHome(), "bin/java" );
	        cl.setExecutable( java.getAbsolutePath() );
	        cl.createArg().setValue( "-cp" );
	        cl.createArg().setValue( createClasspath() );
	        cl.createArg().setValue( "-Xmx" + maxmem );
	        cl.createArg().setValue( "net.sourceforge.cobertura.reporting.Main" );
	        cl.createArg().setValue("--datafile");
	        cl.createArg().setValue(fullPathToDataFile.getAbsolutePath());
	        cl.createArg().setValue("--destination");
	        String dest = reportPath + File.separator + reportDirectoryName;
	        cl.createArg().setValue(dest);

	        File fi = FileUtils.getFile(sourceDir);
	        if (!fi.exists()){
	        	this.getLog().warn("Source Directory " + sourceDir + " not found. Sources will not be referenced in reports!");
	        }
	        cl.createArg().setValue(sourceDir);
	        
	        
	        CommandLineUtils.StringStreamConsumer stdout = new CommandLineUtils.StringStreamConsumer();

	        CommandLineUtils.StringStreamConsumer stderr = new CommandLineUtils.StringStreamConsumer();

	        getLog().debug( "Executing command line:" );
	        getLog().debug( cl.toString() );
	        this.getLog().info("");
	        this.getLog().info("Starting report generation in " + dest + "...");
	        
	        int exitCode;
	        try
	        {
	            exitCode = CommandLineUtils.executeCommandLine( cl, stdout, stderr );
	        }
	        catch ( CommandLineException e )
	        {
	            throw new MojoExecutionException( "Unable to execute Cobertura.", e );
	        }

	        getLog().debug( "exit code: " + exitCode );

	        String output = stdout.getOutput();

	        if ( output.trim().length() > 0 )
	        {
	            getLog().debug( "--------------------" );
	            getLog().debug( " Standard output from the Cobertura task of generating reports:" );
	            getLog().debug( "--------------------" );
	            getLog().info( output );
	            getLog().debug( "--------------------" );
	        }

	        String stream = stderr.getOutput();

	        if ( stream.trim().length() > 0 )
	        {
	            getLog().debug( "--------------------" );
	            getLog().debug( " Standard error from the Cobertura task of generating reports:" );
	            getLog().debug( "--------------------" );
	            getLog().error( stderr.getOutput() );
	            getLog().debug( "--------------------" );
	        }


	       this.getLog().info("...Done. Please see reports by clicking on index.html in " + dest);

		} catch (Exception e) {
			throw new MojoExecutionException("Error processing ", e);
		}

	}
	
	private String createClasspath() throws MojoExecutionException{
		
        StringBuffer cpBuffer = new StringBuffer();

        for ( Iterator it = pluginClasspathList.iterator(); it.hasNext(); ){
            Artifact artifact = (Artifact) it.next();

            try {
                cpBuffer.append( File.pathSeparator ).append( artifact.getFile().getCanonicalPath() );
            } catch ( IOException e ) {
                throw new MojoExecutionException( "Error while creating the canonical path for '" + artifact.getFile() + "'.", e );
            }
        }

        return cpBuffer.toString();

	}
    


    
}


