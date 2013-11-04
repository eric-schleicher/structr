/**
 * Copyright (C) 2010-2013 Axel Morgner, structr <structr@structr.org>
 *
 * This file is part of structr <http://structr.org>.
 *
 * structr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * structr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with structr.  If not, see <http://www.gnu.org/licenses/>.
 */


package org.structr.web.common;

import org.structr.core.property.PropertyMap;
import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;

import org.neo4j.graphdb.GraphDatabaseService;

import org.structr.common.error.FrameworkException;
import org.structr.core.Services;
import org.structr.core.entity.AbstractNode;
import org.structr.core.log.ReadLogCommand;
import org.structr.core.log.WriteLogCommand;
import org.structr.core.graph.CreateNodeCommand;
import org.structr.core.graph.CreateRelationshipCommand;
import org.structr.core.graph.DeleteNodeCommand;
import org.structr.core.graph.FindNodeCommand;
import org.structr.core.graph.GraphDatabaseCommand;
import org.structr.core.graph.StructrTransaction;
import org.structr.core.graph.TransactionCommand;
import org.structr.core.graph.search.SearchNodeCommand;
import org.structr.core.graph.search.SearchRelationshipCommand;

//~--- JDK imports ------------------------------------------------------------

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import org.structr.common.SecurityContext;
import org.structr.core.graph.DeleteRelationshipCommand;
import org.structr.core.graph.NodeInterface;
import org.structr.core.graph.RelationshipInterface;

//~--- classes ----------------------------------------------------------------

/**
 * Base class for all structr tests
 *
 * All tests are executed in superuser context
 *
 * @author Axel Morgner
 */
public class StructrTest extends TestCase {

	private static final Logger logger = Logger.getLogger(StructrTest.class.getName());

	//~--- fields ---------------------------------------------------------

	protected Map<String, String> context = new ConcurrentHashMap<String, String>(20, 0.9f, 8);
	protected CreateNodeCommand createNodeCommand;
	protected CreateRelationshipCommand createRelationshipCommand;
	protected DeleteNodeCommand deleteNodeCommand;
	protected DeleteRelationshipCommand deleteRelationshipCommand;
	protected FindNodeCommand findNodeCommand;
	protected GraphDatabaseCommand graphDbCommand;
	protected ReadLogCommand readLogCommand;
	protected SearchNodeCommand searchNodeCommand;
	protected SearchRelationshipCommand searchRelationshipCommand;
	protected SecurityContext securityContext;
	protected TransactionCommand transactionCommand;
	protected WriteLogCommand writeLogCommand;

	//~--- methods --------------------------------------------------------

	public void test00DbAvailable() {

		GraphDatabaseService graphDb = graphDbCommand.execute();

		assertTrue(graphDb != null);
	}

	@Override
	protected void tearDown() throws Exception {

		Services.shutdown();

		try {
			File testDir = new File(context.get(Services.BASE_PATH));

			if (testDir.isDirectory()) {

				FileUtils.deleteDirectory(testDir);
			} else {

				testDir.delete();
			}
			
		} catch(Throwable t) {
		}

		super.tearDown();

	}

	/**
	 * Recursive method used to find all classes in a given directory and subdirs.
	 *
	 * @param directory   The base directory
	 * @param packageName The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {

		List<Class> classes = new ArrayList<Class>();

		if (!directory.exists()) {

			return classes;
		}

		File[] files = directory.listFiles();

		for (File file : files) {

			if (file.isDirectory()) {

				assert !file.getName().contains(".");

				classes.addAll(findClasses(file, packageName + "." + file.getName()));

			} else if (file.getName().endsWith(".class")) {

				classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
			}

		}

		return classes;

	}

	protected List<NodeInterface> createTestNodes(final String type, final int number) throws FrameworkException {

		final PropertyMap props = new PropertyMap();
		props.put(AbstractNode.type, type);

		return transactionCommand.execute(new StructrTransaction<List<NodeInterface>>() {

			@Override
			public List<NodeInterface> execute() throws FrameworkException {

				List<NodeInterface> nodes = new LinkedList<>();

				for (int i = 0; i < number; i++) {

					nodes.add(createNodeCommand.execute(props));
				}

				return nodes;

			}

		});

	}

	protected <T extends NodeInterface> T createTestNode(final Class<T> type) throws FrameworkException {
		return (T)createTestNode(type.getSimpleName(), new PropertyMap());
	}

	protected NodeInterface createTestNode(final String type) throws FrameworkException {
		return createTestNode(type, new PropertyMap());
	}

	protected NodeInterface createTestNode(final String type, final PropertyMap props) throws FrameworkException {

		props.put(AbstractNode.type, type);

		return transactionCommand.execute(new StructrTransaction<NodeInterface>() {

			@Override
			public NodeInterface execute() throws FrameworkException {

				return createNodeCommand.execute(props);

			}

		});

	}

	protected List<RelationshipInterface> createTestRelationships(final Class relType, final int number) throws FrameworkException {

		List<NodeInterface> nodes     = createTestNodes("UnknownTestType", 2);
		final NodeInterface startNode = nodes.get(0);
		final NodeInterface endNode   = nodes.get(1);

		return transactionCommand.execute(new StructrTransaction<List<RelationshipInterface>>() {

			@Override
			public List<RelationshipInterface> execute() throws FrameworkException {

				List<RelationshipInterface> rels = new LinkedList<>();

				for (int i = 0; i < number; i++) {

					rels.add(createRelationshipCommand.execute(startNode, endNode, relType));
				}

				return rels;

			}

		});

	}

	protected RelationshipInterface createTestRelationship(final AbstractNode startNode, final AbstractNode endNode, final Class relType) throws FrameworkException {

		return transactionCommand.execute(new StructrTransaction<RelationshipInterface>() {

			@Override
			public RelationshipInterface execute() throws FrameworkException {

				return createRelationshipCommand.execute(startNode, endNode, relType);

			}

		});

	}

	protected void assertNodeExists(final String nodeId) throws FrameworkException {

		AbstractNode node = null;

		try {

			node = (AbstractNode) findNodeCommand.execute(nodeId);

		} catch (Throwable t) {}

		assertTrue(node != null);

	}

	protected void assertNodeNotFound(final String nodeId) {

		try {

			findNodeCommand.execute(nodeId);
			fail("Node exists!");

		} catch (FrameworkException fe) {}

	}

	//~--- get methods ----------------------------------------------------

	/**
	 * Get classes in given package and subpackages, accessible from the context class loader
	 *
	 * @param packageName The base package
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	protected static List<Class> getClasses(String packageName) throws ClassNotFoundException, IOException {

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		assert classLoader != null;

		String path                = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs            = new ArrayList<File>();

		while (resources.hasMoreElements()) {

			URL resource = resources.nextElement();

			dirs.add(new File(resource.getFile()));

		}

		List<Class> classList = new ArrayList<Class>();

		for (File directory : dirs) {

			classList.addAll(findClasses(directory, packageName));
		}

		return classList;

	}

	//~--- set methods ----------------------------------------------------

	@Override
	protected void setUp() throws Exception {


		Date now       = new Date();
		long timestamp = now.getTime();

		context.put(Services.CONFIGURED_SERVICES, "ModuleService NodeService LogService");
		context.put(Services.APPLICATION_TITLE, "structr unit test app" + timestamp);
		context.put(Services.TMP_PATH, "/tmp/");
		context.put(Services.BASE_PATH, "/tmp/structr-test-" + timestamp);
		context.put(Services.DATABASE_PATH, "/tmp/structr-test-" + timestamp + "/db");
		context.put(Services.FILES_PATH, "/tmp/structr-test-" + timestamp + "/files");
		context.put(Services.LOG_DATABASE_PATH, "/tmp/structr-test-" + timestamp + "/logDb.dat");
		context.put(Services.TCP_PORT, "13465");
		context.put(Services.SERVER_IP, "127.0.0.1");
		context.put(Services.UDP_PORT, "13466");
		context.put(Services.SUPERUSER_USERNAME, "superadmin");
		context.put(Services.SUPERUSER_PASSWORD, "sehrgeheim");
		
		Services.initialize(context);

		securityContext           = SecurityContext.getSuperUserInstance();
		createNodeCommand         = Services.command(securityContext, CreateNodeCommand.class);
		createRelationshipCommand = Services.command(securityContext, CreateRelationshipCommand.class);
		deleteNodeCommand         = Services.command(securityContext, DeleteNodeCommand.class);
		deleteRelationshipCommand = Services.command(securityContext, DeleteRelationshipCommand.class);
		transactionCommand        = Services.command(securityContext, TransactionCommand.class);
		graphDbCommand            = Services.command(securityContext, GraphDatabaseCommand.class);
		findNodeCommand           = Services.command(securityContext, FindNodeCommand.class);
		searchNodeCommand         = Services.command(securityContext, SearchNodeCommand.class);
		searchRelationshipCommand = Services.command(securityContext, SearchRelationshipCommand.class);
		writeLogCommand           = Services.command(securityContext, WriteLogCommand.class);
		readLogCommand            = Services.command(securityContext, ReadLogCommand.class);

		// wait for service layer to be initialized
		do {
			try { Thread.sleep(10); } catch(Throwable t) {}
			
		} while(!Services.isInitialized());

	}

}
