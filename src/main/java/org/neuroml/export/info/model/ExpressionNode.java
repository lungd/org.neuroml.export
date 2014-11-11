package org.neuroml.export.info.model;

/**
 * @author Adrian Quintana (adrian.perez@ucl.ac.uk)
 */
public class ExpressionNode
{

	private String expression;
	
	private PlotMetadataNode plotMetadataNode;

	public ExpressionNode(String expression)
	{
		super();
		this.expression = expression;
	}

	public ExpressionNode(String expression, String plotTitle, String xAxisLabel, String yAxisLabel)
	{
		super();
		this.expression = expression;
		this.plotMetadataNode = new PlotMetadataNode(plotTitle, xAxisLabel, yAxisLabel);
		
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public PlotMetadataNode getPlotMetadataNode() {
		return plotMetadataNode;
	}

	public void setMetadataPlotNode(PlotMetadataNode metadataPlotNode) {
		this.plotMetadataNode = metadataPlotNode;
	}

	@Override
	public String toString()
	{
		String toString = "FunctionNode [Expression=" + expression;
		if (this.plotMetadataNode != null){
			toString += ", " + this.plotMetadataNode.toString();
		}
		toString += "]";
		return toString;
	}
	
}
