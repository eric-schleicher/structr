/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.structr.ui.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.apache.click.Page;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.click.control.Form;
import org.apache.click.control.Panel;
import org.apache.click.control.PasswordField;
import org.apache.click.control.Submit;
import org.apache.click.control.TextField;
import org.apache.click.extras.tree.TreeNode;
import org.apache.click.util.Bindable;
import org.structr.core.Command;
import org.structr.core.Services;
import org.structr.core.entity.StructrNode;
import org.structr.core.entity.SuperUser;
import org.structr.core.entity.User;
import org.structr.core.node.FindNodeCommand;
import org.structr.core.node.FindUserCommand;
import org.structr.ui.page.admin.Admin;
import org.structr.ui.page.admin.DefaultView;

/**
 *
 * @author amorgner
 */
public class LoginPage extends Admin {

    //private final static String DOMAIN_KEY = "domain";
    private final static String PASSWORD_KEY = "password";
    private final static String SUPERADMIN_PASSWORD_KEY = "sehrgeheim";
    @Bindable
    protected Panel loginPanel = new Panel("loginPanel", "/panel/login-panel.htm");
    @Bindable
    protected Form loginForm = new Form();

    // use template for backend pages
    @Override
    public String getTemplate() {
        return "/login.htm";
    }

    public LoginPage() {

        super();

        title = "Login";

        //loginForm.add(new TextField(DOMAIN_KEY, true));
        loginForm.add(new TextField(USERNAME_KEY, "Username", 20, true));
        loginForm.add(new PasswordField(PASSWORD_KEY, "Password", 20, true));

        loginForm.add(new Submit("login", " Click to login ", this, "onLogin"));

    }

    /**
     * @see Page#onSecurityCheck()
     */
    @Override
    public boolean onSecurityCheck() {
        userName = (String) getContext().getRequest().getSession().getAttribute(USERNAME_KEY);
        if (userName != null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean onLogin() {

        if (loginForm.isValid()) {

            //String domainValue = loginForm.getFieldValue(DOMAIN_KEY);
            String userValue = loginForm.getFieldValue(USERNAME_KEY);
            String passwordValue = loginForm.getFieldValue(PASSWORD_KEY);

            // TODO: remove superadmin login!!
            if (SUPERUSER_KEY.equals(userValue) && SUPERADMIN_PASSWORD_KEY.equals(passwordValue)) {

                logger.log(Level.INFO, "############# Logged in as superadmin! ############");
                userName = SUPERUSER_KEY;
                isSuperUser = true;

                user = new SuperUser();
                getContext().getRequest().getSession().setAttribute(USERNAME_KEY, userValue);

                Services.initialize();

                // redirect superuser to maintenance
                setRedirect("/admin/maintenance.htm");

            } else {

                Services.initialize();

                Command findUser = Services.createCommand(FindUserCommand.class);

                user = (User) findUser.execute(userValue);//, domainValue);

//                if (domainValue == null) {
//                    logger.log(Level.INFO, "No domain at login");
//                    errorMsg = "No domain";
//                    return true;
//                }

                if (user == null) {
                    logger.log(Level.INFO, "No user found for name {0}", user);
                    errorMsg = "Wrong username or password, or user is blocked. Check caps lock. Note: Username is case sensitive!";
                    return true;
                }

                if (user.isBlocked()) {
                    logger.log(Level.INFO, "User {0} is blocked", user);
                    errorMsg = "Wrong username or password, or user is blocked. Check caps lock. Note: Username is case sensitive!";
                    return true;
                }

                if (passwordValue == null) {
                    logger.log(Level.INFO, "Password for user {0} is null", user);
                    errorMsg = "You should enter a password.";
                    return true;
                }

                String encryptedPasswordValue = DigestUtils.sha512Hex(passwordValue);

                if (!encryptedPasswordValue.equals(user.getProperty(PASSWORD_KEY))) {
                    logger.log(Level.INFO, "Wrong password for user {0}", user);
                    errorMsg = "Wrong username or password, or user is blocked. Check caps lock. Note: Username is case sensitive!";
                    return true;
                }

                // username and password are both valid
                userName = userValue;

                getContext().getRequest().getSession().setAttribute(USERNAME_KEY, userValue);


                String startNodeId = getNodeId();
                if (startNodeId == null) {
                    startNodeId = restoreLastVisitedNodeFromUserProfile();
                    nodeId = startNodeId;
                }

                StructrNode startNode = getNodeByIdOrPath(startNodeId);

                Class<? extends Page> targetPage = getRedirectPage((startNode), this);

                if (targetPage == null) {
                    targetPage = DefaultView.class;
                }

                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put(NODE_ID_KEY, String.valueOf(getNodeId()));
                //parameters.put(RENDER_MODE_KEY, renderMode);
                //parameters.put(OK_MSG_KEY, okMsg);

                // default after login is edit mode
                Class<? extends Page> editPage = getEditPageClass(getNodeByIdOrPath(nodeId));
                setRedirect(editPage, parameters);

                long[] expandedNodesArray = getExpandedNodesFromUserProfile();
                if (expandedNodesArray != null && expandedNodesArray.length > 0) {

                    openNodes = new ArrayList<TreeNode>();

                    Command findNode = Services.createCommand(FindNodeCommand.class);
                    for (Long s : expandedNodesArray) {

                        StructrNode n = (StructrNode) findNode.execute(user, s);
                        if (n != null) {
                            //openNodes.add(new TreeNode(String.valueOf(n.getId())));
                            openNodes.add(new TreeNode(n, String.valueOf(n.getId())));
                        }

                    }
                    // fill session
                    getContext().getSession().setAttribute(EXPANDED_NODES_KEY, openNodes);
                }
            }

            return false;

        }

        return true;
    }
}
