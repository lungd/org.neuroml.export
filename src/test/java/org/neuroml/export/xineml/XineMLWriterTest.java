package org.neuroml.export.xineml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.lemsml.jlems.core.expression.ParseError;
import org.lemsml.jlems.core.run.ConnectionError;
import org.lemsml.jlems.core.run.RuntimeError;
import org.lemsml.jlems.core.sim.ContentError;
import org.lemsml.jlems.core.sim.ParseException;
import org.lemsml.jlems.core.type.BuildException;
import org.lemsml.jlems.core.type.Lems;
import org.lemsml.jlems.core.xml.XMLException;
import org.lemsml.jlems.io.util.FileUtil;
import org.neuroml.export.AppTest;
import org.neuroml.export.xineml.XineMLWriter.Variant;

import junit.framework.TestCase;
import org.lemsml.export.base.GenerationException;

public class XineMLWriterTest extends TestCase {

	public void testFN() throws ContentError, ParseError, ParseException, BuildException, XMLException, IOException, ConnectionError, RuntimeError, GenerationException {

    	String exampleFilename = "LEMS_NML2_Ex9_FN.xml";
    	generateMainScript(exampleFilename);
	}
	public void testIzh() throws ContentError, ParseError, ParseException, BuildException, XMLException, IOException, ConnectionError, RuntimeError, GenerationException {

    	String exampleFilename = "LEMS_NML2_Ex2_Izh.xml";
    	generateMainScript(exampleFilename);
	}
	
	/*
	public void testHH() throws ContentError, ParseError, ParseException, BuildException, XMLException, IOException, ConnectionError, RuntimeError {

    	String exampleFilename = "LEMS_NML2_Ex1_HH.xml";
    	generateMainScript(exampleFilename);
	}*/
	
	public void generateMainScript(String exampleFilename) throws XMLException, IOException, ConnectionError, RuntimeError, ContentError, ParseError, ParseException, BuildException, GenerationException {


    	Lems lems = AppTest.readLemsFileFromExamples(exampleFilename);

        System.out.println("Loaded: "+exampleFilename);

        XineMLWriter nw = new XineMLWriter(lems, Variant.NineML);

        String nr = nw.getMainScript();
        
        System.out.println("Generated: "+nr);
        
        for (File f: nw.getFilesGenerated()) {
            System.out.println("Checking file: "+f.getAbsolutePath());
	        assertTrue(f.exists());
        }

        // Refresh..
    	lems = AppTest.readLemsFileFromExamples(exampleFilename);

        XineMLWriter sw = new XineMLWriter(lems, Variant.SpineML);

        String sr = sw.getMainScript();
        
        System.out.println("Generated: "+sr);
        
        for (File f: sw.getFilesGenerated()) {
            System.out.println("Checking file: "+f.getAbsolutePath());
	        assertTrue(f.exists());
        }
	}

}
