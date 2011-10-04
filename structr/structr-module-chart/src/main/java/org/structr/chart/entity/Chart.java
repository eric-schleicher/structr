package org.structr.chart.entity;

import org.structr.common.PropertyKey;
import org.structr.core.entity.GenericNode;

/**
 *
 * @author Christian Morgner
 */
public abstract class Chart extends GenericNode
{
	public enum Key implements PropertyKey
	{
		width, height;
	}

	public int getWidth()
	{
		return(getIntProperty(Key.width));
	}

	public int getHeight()
	{
		return(getIntProperty(Key.height));
	}

	@Override
	public String getContentType()
	{
		return("image/png");
	}
}
