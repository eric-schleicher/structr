/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.structr.core.entity.web;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.structr.core.entity.StructrNode;
import org.structr.core.entity.User;

/**
 *
 * @author axel
 */
public class WebNode extends StructrNode {

    private final static String ICON_SRC = "/images/folder.png";
    private static final Logger logger = Logger.getLogger(StructrNode.class.getName());

    /**
     * Traverse over all child nodes to find a home page
     */
    public HomePage getHomePage(final User user) {

        List<StructrNode> childNodes = getAllChildren(HomePage.class.getSimpleName(), user);

        for (StructrNode node : childNodes) {

            if (node instanceof HomePage) {
                return ((HomePage) node);
            }

        }
        logger.log(Level.FINE, "No home page found for node {0}", this.getId());
        return null;
    }


    /**
     * Render a node-specific view as html
     */
    @Override
    public void renderView(StringBuilder out, final StructrNode startNode,
            final String editUrl, final Long editNodeId, final User user) {

        if (editNodeId != null && getId() == editNodeId.longValue()) {

            renderEditFrame(out, editUrl);

        } else {

            if (isVisible()) {

                if (hasTemplate(user)) {
                    template.setRequest(getRequest());
                    template.setCallingNode(this);
                    template.renderView(out, startNode, editUrl, editNodeId, user);
                } else {
                    out.append(getName());
                }
            }

        }
    }

}
