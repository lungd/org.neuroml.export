package org.neuroml.export.netpyne;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.lemsml.export.dlems.DLemsKeywords;
import org.lemsml.export.dlems.DLemsWriter;
import org.lemsml.jlems.core.logging.E;
import org.lemsml.jlems.core.logging.MinimalMessageHandler;
import org.lemsml.jlems.core.sim.ContentError;
import org.lemsml.jlems.core.sim.LEMSException;
import org.lemsml.jlems.core.type.Component;
import org.lemsml.jlems.core.type.Lems;
import org.lemsml.jlems.io.util.FileUtil;
import org.neuroml.export.base.ANeuroMLBaseWriter;
import org.neuroml.export.exceptions.GenerationException;
import org.neuroml.export.exceptions.ModelFeatureSupportException;
import org.neuroml.export.neuron.NRNUtils;
import org.neuroml.export.neuron.NeuronWriter;
import org.neuroml.export.utils.Format;
import org.neuroml.export.utils.Utils;
import org.neuroml.export.utils.VelocityUtils;
import org.neuroml.export.utils.support.ModelFeature;
import org.neuroml.export.utils.support.SupportLevelInfo;
import org.neuroml.model.util.NeuroMLElements;
import org.neuroml.model.util.NeuroMLException;

@SuppressWarnings("StringConcatenationInsideStringBufferAppend")
public class NetPyNEWriter extends ANeuroMLBaseWriter
{

	String comm = "#";
	String commPre = "'''";
	String commPost = "'''";

	private final List<File> outputFiles = new ArrayList<File>();
    private final DLemsWriter dlemsw;
    private String mainDlemsFile = null;
    
    public final String CELL_DEFINITION_SUFFIX = "_celldefinition";
    public final String INPUT_DEFINITION_SUFFIX = "_inputdefinition";
	
	public NetPyNEWriter(Lems lems) throws ModelFeatureSupportException, LEMSException, NeuroMLException
	{
		super(lems, Format.NETPYNE);
        dlemsw = new DLemsWriter(lems, null, false);
        dlemsw.setPopulationMode(true);
		initializeWriter();
	}
	
	public NetPyNEWriter(Lems lems, File outputFolder, String outputFileName) throws ModelFeatureSupportException, LEMSException, NeuroMLException
	{
		super(lems, Format.NETPYNE, outputFolder, outputFileName);
        dlemsw = new DLemsWriter(lems, outputFolder, mainDlemsFile, null, false);
        dlemsw.setPopulationMode(true);
		initializeWriter();
	}
    
    
    @Override
	public void setOutputFolder(File outputFolder)
	{
		super.setOutputFolder(outputFolder);
        dlemsw.setOutputFolder(outputFolder);
	}

	private void initializeWriter()
	{
		MinimalMessageHandler.setVeryMinimal(true);
		E.setDebug(false);
	}

	@Override
	public void setSupportedFeatures()
	{
		sli.addSupportInfo(format, ModelFeature.ABSTRACT_CELL_MODEL, SupportLevelInfo.Level.LOW);
		sli.addSupportInfo(format, ModelFeature.COND_BASED_CELL_MODEL, SupportLevelInfo.Level.LOW);
		sli.addSupportInfo(format, ModelFeature.SINGLE_COMP_MODEL, SupportLevelInfo.Level.LOW);
		sli.addSupportInfo(format, ModelFeature.NETWORK_MODEL, SupportLevelInfo.Level.LOW);
		sli.addSupportInfo(format, ModelFeature.MULTI_CELL_MODEL, SupportLevelInfo.Level.LOW);
		sli.addSupportInfo(format, ModelFeature.MULTI_POPULATION_MODEL, SupportLevelInfo.Level.LOW);
		sli.addSupportInfo(format, ModelFeature.NETWORK_WITH_INPUTS_MODEL, SupportLevelInfo.Level.LOW);
		sli.addSupportInfo(format, ModelFeature.NETWORK_WITH_PROJECTIONS_MODEL, SupportLevelInfo.Level.LOW);
		sli.addSupportInfo(format, ModelFeature.MULTICOMPARTMENTAL_CELL_MODEL, SupportLevelInfo.Level.NONE);
		sli.addSupportInfo(format, ModelFeature.HH_CHANNEL_MODEL, SupportLevelInfo.Level.LOW);
		sli.addSupportInfo(format, ModelFeature.KS_CHANNEL_MODEL, SupportLevelInfo.Level.NONE);
	}

	@Override
	protected void addComment(StringBuilder sb, String comment)
	{

		if(!comment.contains("\n")) sb.append(comm + comment + "\n");
		else sb.append(commPre + "\n" + comment + "\n" + commPost + "\n");
	}

	public String getMainScript() throws GenerationException
	{
        mainDlemsFile = getOutputFileName()+"_main.json";
        dlemsw.setOutputFileName(mainDlemsFile);
        NRNUtils nrnUtils = new NRNUtils();
        dlemsw.setUnitConverter(nrnUtils);
        dlemsw.setOnlyFlattenIfNecessary(true);
		StringBuilder mainRunScript = new StringBuilder();

		addComment(mainRunScript, format + " simulator compliant export for:\n\n" + lems.textSummary(false, false) + "\n\n" + Utils.getHeaderComment(format) + "\n");


		VelocityUtils.initializeVelocity();
		VelocityContext context = new VelocityContext();

		try
		{
            NeuronWriter nrnWriter = new NeuronWriter(lems,getOutputFolder(),"---");
            List<File> files = dlemsw.convert();
            
            for (File file: files) {
                    
                E.info(">>> Processing DLEMS file: " + file.getAbsolutePath());
                    
                String dlems = FileUtil.readStringFromFile(file);

                DLemsWriter.putIntoVelocityContext(dlems, context);

                VelocityEngine ve = VelocityUtils.getVelocityEngine();
                StringWriter sw1 = new StringWriter();

                if (file.getName().equals(mainDlemsFile)) {
                    ve.evaluate(context, sw1, "LOG", VelocityUtils.getTemplateAsReader(VelocityUtils.netpyneRunTemplateFile));
                    mainRunScript.append(sw1);
                }
                else 
                {
                    StringBuilder script = new StringBuilder();
                    addComment(script, format + " simulator compliant export for:\n\n" + lems.textSummary(false, false) + "\n\n" + Utils.getHeaderComment(format) + "\n");
        
                    String name = (String) context.internalGet(DLemsKeywords.NAME.get());
                    Component comp = lems.components.getByID(name);
                    E.info("Component LEMS: " + comp.summary());
                    String suffix = null;
                    String template = null;
                    
                    if(comp.getComponentType().isOrExtends(NeuroMLElements.CELL_COMP_TYPE))
                    {

                    }
                    else if(comp.getComponentType().isOrExtends(NeuroMLElements.BASE_CELL_COMP_TYPE) || file.getName().endsWith(".cell.json"))
                    {
                        String mod = nrnWriter.generateModFile(comp);
                        nrnWriter.saveModToFile(comp, mod);
                        suffix = CELL_DEFINITION_SUFFIX;
                        template = VelocityUtils.netpyneCellTemplateFile;
                    }
                    else if(comp.getComponentType().isOrExtends(NeuroMLElements.BASE_POINT_CURR_COMP_TYPE) || file.getName().endsWith(".input.json"))
                    {
                        String mod = nrnWriter.generateModFile(comp);
                        nrnWriter.saveModToFile(comp, mod);
                        suffix = INPUT_DEFINITION_SUFFIX;
                        template = VelocityUtils.netpyneInputNeuronTemplateFile;
                    }
                    else 
                    {
                        throw new NeuroMLException("Cannot determine type of Component: "+comp.summary());
                    }
                    ve.evaluate(context, sw1, "LOG", VelocityUtils.getTemplateAsReader(template));
                    script.append(sw1);

                    E.info("Writing " + format + " file to: " + file.getAbsolutePath());
                    File scriptFile = new File(this.getOutputFolder(), name + suffix +".py");
                    FileUtil.writeStringToFile(script.toString(), scriptFile);
                    outputFiles.add(scriptFile);
                }
            }

		}
		catch(IOException e1)
		{
			throw new GenerationException("Problem converting LEMS to dLEMS", e1);
		}
		catch(VelocityException e)
		{
			throw new GenerationException("Problem using Velocity template", e);
		}
		catch(ContentError e)
		{
			throw new GenerationException("Problem using Velocity template", e);
		}
		catch(LEMSException e)
		{
			throw new GenerationException("Problem using Velocity template", e);
		}
		catch(NeuroMLException e)
		{
			throw new GenerationException("Problem using Velocity template", e);
		}

		return mainRunScript.toString();

	}

	@Override
	public List<File> convert() throws GenerationException, IOException
	{

        String code = this.getMainScript();

        File outputFile = new File(this.getOutputFolder(), this.getOutputFileName());
        FileUtil.writeStringToFile(code, outputFile);
        outputFiles.add(outputFile);
		
		return this.outputFiles;
	}
    
    public static void main(String[] args) throws Exception
    {

        ArrayList<File> lemsFiles = new ArrayList<File>();
        lemsFiles.add(new File("../NeuroML2/LEMSexamples/LEMS_NML2_Ex0_IaF.xml"));
        //lemsFiles.add(new File("../NeuroML2/LEMSexamples/LEMS_NML2_Ex5_DetCell.xml"));
        //lemsFiles.add(new File("../neuroConstruct/osb/cerebral_cortex/networks/IzhikevichModel/NeuroML2/LEMS_SmallNetwork.xml"));
        //lemsFiles.add(new File("../neuroConstruct/osb/cerebral_cortex/networks/IzhikevichModel/NeuroML2/LEMS_2007Cells.xml"));
        lemsFiles.add(new File("../neuroConstruct/osb/cerebral_cortex/networks/IzhikevichModel/NeuroML2/LEMS_2007One.xml"));

        for (File lemsFile : lemsFiles)
        {
            Lems lems = Utils.readLemsNeuroMLFile(lemsFile).getLems();
            NetPyNEWriter pw = new NetPyNEWriter(lems, lemsFile.getParentFile(), lemsFile.getName().replaceAll(".xml", "_netpyne.py"));
            
            List<File> files = pw.convert();
            for (File f : files)
            {
                System.out.println("Have created: " + f.getAbsolutePath());
            }

        }
    }

}