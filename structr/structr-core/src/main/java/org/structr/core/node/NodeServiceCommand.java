/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.structr.core.node;

import org.structr.core.Command;

/**
 *
 * @author cmorgner
 */
public abstract class NodeServiceCommand extends Command
{
	@Override
	public Class getServiceClass()
	{
		return(NodeService.class);
	}
}
