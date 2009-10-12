/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package fr.imag.adele.cadse.builder.pde.ant;


import java.io.File;

import org.apache.felix.ipojo.manipulator.Pojoization;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

/**
 * iPOJO Ant Task. This Ant task manipulates an input bundle.
 * @author <a href="mailto:dev@felix.apache.org">Felix Project Team</a>
 */
public class IPojoPDETask extends Task {
    
    /** Input bundle. */
    private File m_directory, m_target;

    /** Flag describing if we need to ignore annotation of not. */
    private boolean m_ignoreAnnotations = false;
    
    /**
     * Flag describing if we need or not use local XSD files
     * (i.e. use the {@link SchemaResolver} or not).
     * If <code>true</code> the local XSD are not used.
     */
    private boolean m_ignoreLocalXSD = false;

	  
    
    /**
     * Set the input bundle.
     * @param in : the input bundle
     */
    public void setDirectory(File in) {
    	m_directory = in;
    }
    
    /**
     * Set the input bundle.
     * @param in : the input bundle
     */
    public void setTarget(File in) {
    	m_target = in;
    }
   
    
    /**
     * Set if we need to ignore annotations or not.
     * @param flag : true if we need to ignore annotations.
     */
    public void setIgnoreAnnotations(boolean flag) {
        m_ignoreAnnotations = flag;
    }
    
    /**
     * Set if we need to use embedded XSD files or not.
     * @param flag : true if we need to ignore embedded XSD files.
     */
    public void setIgnoreEmbeddedSchemas(boolean flag) {
        m_ignoreLocalXSD = flag;
    }
    
    /**
     * Execute the Ant Task.
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() {
    	if (m_directory == null) {
            throw new BuildException("No project directory specified");
        }
        if (!m_directory.exists()) {
            throw new BuildException("The project directory " + m_directory.getAbsolutePath() + " does not exist");
        }
        
        if (m_target == null) {
            throw new BuildException("No target specified");
        }
        if (!m_target.exists()) {
            throw new BuildException("The target " + m_target.getAbsolutePath() + " does not exist");
        }
    	File manifestFile = new File(m_directory, "META-INF"+File.separator+"MANIFEST.MF");
		if (!manifestFile.exists()) {
			throw new BuildException("No manifest file found");
		}
		File metadataFile = new File(m_directory, "metadata.xml");
		if (!metadataFile.exists()) {
			metadataFile = new File(m_directory, 
					"src"+File.separator+"main"+File.separator+"resources"+File.separator+"metadata.xml");
			if (!metadataFile.exists()) {
				metadataFile = new File(m_directory, 
						"sources"+File.separator+"main"+File.separator+"resources"+File.separator+"metadata.xml");
			}
			if (!metadataFile.exists()) {
				 log("No metadata file found & annotations ignored : nothing to do");
                 return;
			}
		}
		
        log("Input bundle file : " + m_target.getAbsolutePath());
        log("Start bundle manipulation");
        
        
        Pojoization pojo = new Pojoization();
        if (! m_ignoreAnnotations) {
            pojo.setAnnotationProcessing();
        }
        if (! m_ignoreLocalXSD) {
            pojo.setUseLocalXSD();
        }
        pojo.directoryPojoization(m_target, metadataFile, manifestFile);
        for (int i = 0; i < pojo.getWarnings().size(); i++) {
            log((String) pojo.getWarnings().get(i), Project.MSG_WARN);
        }
        if (pojo.getErrors().size() > 0) { throw new BuildException((String) pojo.getErrors().get(0)); }
        
        
        
        log("Bundle manipulation - SUCCESS");
        
    }
    
    

}

