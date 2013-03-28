package org.neuroml.export.brian;

import java.io.File;
import java.io.IOException;

import org.lemsml.jlems.core.expression.ParseError;
import org.lemsml.jlems.core.sim.ContentError;
import org.lemsml.jlems.core.sim.ParseException;
import org.lemsml.jlems.core.type.BuildException;
import org.lemsml.jlems.core.type.Lems;
import org.lemsml.jlems.core.xml.XMLException;
import org.lemsml.jlems.io.util.FileUtil;
import org.neuroml.export.AppTest;
import org.neuroml.export.Utils;

import junit.framework.TestCase;

public class BrianWriterTest extends TestCase {

	public void testGetMainScript() throws ContentError, ParseError, ParseException, BuildException, XMLException, IOException {

    	String exampleFilename = "LEMS_NML2_Ex9_FN.xml";
    	
        File exampleFile = new File(AppTest.getLemsExamplesDir(), exampleFilename);

        //exampleFile = new File("/home/padraig/neuroConstruct/osb/invertebrate/barnacle/MorrisLecarModel/NeuroML2/Run_MorrisLecar.xml");
        //exampleFile = new File("/home/padraig/org.neuroml.import/src/test/resources/sbmlTestSuite/cases/semantic/00001/00001-sbml-l3v1_SBML.xml");
        
		Lems lems = Utils.loadLemsFile(exampleFile);
        System.out.println("Loaded: "+exampleFile.getAbsolutePath());

        BrianWriter bw = new BrianWriter(lems);

        String br = bw.getMainScript();


        File brFile = new File(AppTest.getTempDir(),exampleFile.getName().replaceAll(".xml", "_brian.py"));
        System.out.println("Writing to: "+brFile.getAbsolutePath());
        
        FileUtil.writeStringToFile(br, brFile);

        
        assertTrue(brFile.exists());
	}

}
