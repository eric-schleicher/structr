/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.structr.core.node;

/**
 *
 * @author cmorgner
 */
public interface StructrTransaction
{
	public Object execute() throws Throwable;
}
