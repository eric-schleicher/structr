/**
 * Copyright (C) 2010-2018 Structr GmbH
 *
 * This file is part of Structr <http://structr.org>.
 *
 * Structr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Structr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Structr.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.structr.schema;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.structr.api.config.Settings;
import org.structr.common.StructrTest;
import org.structr.common.error.ErrorToken;
import org.structr.common.error.FrameworkException;
import org.structr.core.GraphObject;
import org.structr.core.app.StructrApp;
import org.structr.core.entity.AbstractNode;
import org.structr.core.entity.Group;
import org.structr.core.entity.SchemaNode;
import org.structr.core.entity.SchemaRelationshipNode;
import org.structr.core.entity.TestOne;
import org.structr.core.entity.TestTwelve;
import org.structr.core.graph.NodeAttribute;
import org.structr.core.graph.NodeInterface;
import org.structr.core.graph.Tx;
import org.structr.core.property.EnumProperty;
import org.structr.core.property.PropertyKey;
import org.structr.core.property.StringProperty;

/**
 *
 *
 */
public class ValidationTest extends StructrTest {

	private static final Logger logger = LoggerFactory.getLogger(ValidationTest.class.getName());

	@Test
	public void testUUIDValidation() {

		this.cleanDatabaseAndSchema();

		try (final Tx tx = app.tx()) {

			// test 31 characters
			app.create(TestOne.class, new NodeAttribute<>(GraphObject.id, "1234567890123456789012345678901"));
			tx.success();

			fail("UUID format constraint violated!");

		} catch (FrameworkException fex) {

			final List<ErrorToken> tokens = fex.getErrorBuffer().getErrorTokens();
			final ErrorToken token = tokens.get(0);

			assertEquals("Invalid uniqueness validation result", 1, tokens.size());
			assertEquals("Invalid UUID uniqueness validation result", 422, fex.getStatus());
			assertEquals("Invalid UUID uniqueness validation result", "id", token.getProperty());
			assertEquals("Invalid UUID uniqueness validation result", "TestOne", token.getType());
			assertEquals("Invalid UUID uniqueness validation result", "must_match", token.getToken());
		}

		try (final Tx tx = app.tx()) {

			// test 33 characters
			app.create(TestOne.class, new NodeAttribute<>(GraphObject.id, "123456789012345678901234567890123"));
			tx.success();

			fail("UUID format constraint violated!");

		} catch (FrameworkException fex) {

			final List<ErrorToken> tokens = fex.getErrorBuffer().getErrorTokens();
			final ErrorToken token = tokens.get(0);

			assertEquals("Invalid uniqueness validation result", 1, tokens.size());
			assertEquals("Invalid UUID uniqueness validation result", 422, fex.getStatus());
			assertEquals("Invalid UUID uniqueness validation result", "id", token.getProperty());
			assertEquals("Invalid UUID uniqueness validation result", "TestOne", token.getType());
			assertEquals("Invalid UUID uniqueness validation result", "must_match", token.getToken());
		}

		try (final Tx tx = app.tx()) {

			// test 40 characters
			app.create(TestOne.class, new NodeAttribute<>(GraphObject.id, "1234567890123456789012345678901234567890"));
			tx.success();

			fail("UUID format constraint violated!");

		} catch (FrameworkException fex) {

			final List<ErrorToken> tokens = fex.getErrorBuffer().getErrorTokens();
			final ErrorToken token = tokens.get(0);

			assertEquals("Invalid uniqueness validation result", 1, tokens.size());
			assertEquals("Invalid UUID uniqueness validation result", 422, fex.getStatus());
			assertEquals("Invalid UUID uniqueness validation result", "id", token.getProperty());
			assertEquals("Invalid UUID uniqueness validation result", "TestOne", token.getType());
			assertEquals("Invalid UUID uniqueness validation result", "must_match", token.getToken());
		}

		try (final Tx tx = app.tx()) {

			// test wrong characters
			app.create(TestOne.class, new NodeAttribute<>(GraphObject.id, "123456789012345678g0123456789012"));
			tx.success();

			fail("UUID format constraint violated!");

		} catch (FrameworkException fex) {

			final List<ErrorToken> tokens = fex.getErrorBuffer().getErrorTokens();
			final ErrorToken token = tokens.get(0);

			assertEquals("Invalid uniqueness validation result", 1, tokens.size());
			assertEquals("Invalid UUID uniqueness validation result", 422, fex.getStatus());
			assertEquals("Invalid UUID uniqueness validation result", "id", token.getProperty());
			assertEquals("Invalid UUID uniqueness validation result", "TestOne", token.getType());
			assertEquals("Invalid UUID uniqueness validation result", "must_match", token.getToken());
		}

		try (final Tx tx = app.tx()) {

			// test wrong characters
			app.create(TestOne.class, new NodeAttribute<>(GraphObject.id, "!bcdefabcdefabcdefabcdefabcdefab"));
			tx.success();

			fail("UUID format constraint violated!");

		} catch (FrameworkException fex) {

			final List<ErrorToken> tokens = fex.getErrorBuffer().getErrorTokens();
			final ErrorToken token = tokens.get(0);

			assertEquals("Invalid uniqueness validation result", 1, tokens.size());
			assertEquals("Invalid UUID uniqueness validation result", 422, fex.getStatus());
			assertEquals("Invalid UUID uniqueness validation result", "id", token.getProperty());
			assertEquals("Invalid UUID uniqueness validation result", "TestOne", token.getType());
			assertEquals("Invalid UUID uniqueness validation result", "must_match", token.getToken());
		}

		try (final Tx tx = app.tx()) {

			// test wrong characters
			app.create(TestOne.class, new NodeAttribute<>(GraphObject.id, "sdfkgjh34t"));
			tx.success();

			fail("UUID format constraint violated!");

		} catch (FrameworkException fex) {

			final List<ErrorToken> tokens = fex.getErrorBuffer().getErrorTokens();
			final ErrorToken token = tokens.get(0);

			assertEquals("Invalid uniqueness validation result", 1, tokens.size());
			assertEquals("Invalid UUID uniqueness validation result", 422, fex.getStatus());
			assertEquals("Invalid UUID uniqueness validation result", "id", token.getProperty());
			assertEquals("Invalid UUID uniqueness validation result", "TestOne", token.getType());
			assertEquals("Invalid UUID uniqueness validation result", "must_match", token.getToken());
		}

		try (final Tx tx = app.tx()) {

			// test all allowed characters
			app.create(TestOne.class, new NodeAttribute<>(GraphObject.id, "abcdef1234567890ABCDEF1234567890"));
			tx.success();

		} catch (FrameworkException fex) {

			fail("UUID validation failed for valid result.");
		}

		try (final Tx tx = app.tx()) {

			app.create(TestOne.class, new NodeAttribute<>(GraphObject.id, "xy-"));
			tx.success();

			fail("UUID format constraint violated!");

		} catch (FrameworkException fex) {

			final List<ErrorToken> tokens = fex.getErrorBuffer().getErrorTokens();
			final ErrorToken token = tokens.get(0);

			assertEquals("Invalid uniqueness validation result", 1, tokens.size());
			assertEquals("Invalid UUID uniqueness validation result", 422, fex.getStatus());
			assertEquals("Invalid UUID uniqueness validation result", "id", token.getProperty());
			assertEquals("Invalid UUID uniqueness validation result", "TestOne", token.getType());
			assertEquals("Invalid UUID uniqueness validation result", "must_match", token.getToken());
		}

		try (final Tx tx = app.tx()) {

			app.create(TestOne.class, new NodeAttribute<>(GraphObject.id, ""));
			tx.success();

			fail("UUID not empty constraint violated!");

		} catch (FrameworkException fex) {

			final List<ErrorToken> tokens = fex.getErrorBuffer().getErrorTokens();
			final ErrorToken token1 = tokens.get(0);
			final ErrorToken token2 = tokens.get(1);

			assertEquals("Invalid uniqueness validation result", 2, tokens.size());
			assertEquals("Invalid UUID uniqueness validation result", 422, fex.getStatus());

			assertEquals("Invalid UUID uniqueness validation result", "id",                token1.getProperty());
			assertEquals("Invalid UUID uniqueness validation result", "TestOne",           token1.getType());
			assertEquals("Invalid UUID uniqueness validation result", "must_not_be_empty", token1.getToken());

			assertEquals("Invalid UUID uniqueness validation result", "id",         token2.getProperty());
			assertEquals("Invalid UUID uniqueness validation result", "TestOne",    token2.getType());
			assertEquals("Invalid UUID uniqueness validation result", "must_match", token2.getToken());
		}
	}

	@Test
	public void testSchemaNodeNameValidation() {

		this.cleanDatabaseAndSchema();

		try (final Tx tx = app.tx()) {

			app.create(SchemaNode.class, "lowercase");
			tx.success();

			fail("SchemaNode name constraint violation!");

		} catch (FrameworkException fex) {

			final List<ErrorToken> tokens = fex.getErrorBuffer().getErrorTokens();
			final ErrorToken token = tokens.get(0);

			assertEquals("Invalid uniqueness validation result", 1, tokens.size());
			assertEquals("Invalid constraint violation error token", 422, fex.getStatus());
			assertEquals("Invalid constraint violation error token", "name", token.getProperty());
			assertEquals("Invalid constraint violation error token", "SchemaNode", token.getType());
			assertEquals("Invalid constraint violation error token", "must_match", token.getToken());
		}

		try (final Tx tx = app.tx()) {

			app.create(SchemaNode.class, "7NumberAsFirstChar");
			tx.success();

			fail("SchemaNode name constraint violation!");

		} catch (FrameworkException fex) {

			final List<ErrorToken> tokens = fex.getErrorBuffer().getErrorTokens();
			final ErrorToken token = tokens.get(0);

			assertEquals("Invalid uniqueness validation result", 1, tokens.size());
			assertEquals("Invalid constraint violation error token", 422, fex.getStatus());
			assertEquals("Invalid constraint violation error token", "name", token.getProperty());
			assertEquals("Invalid constraint violation error token", "SchemaNode", token.getType());
			assertEquals("Invalid constraint violation error token", "must_match", token.getToken());
		}

		try (final Tx tx = app.tx()) {

			app.create(SchemaNode.class, "7Number");
			tx.success();

			fail("SchemaNode name constraint violation!");

		} catch (FrameworkException fex) {

			final List<ErrorToken> tokens = fex.getErrorBuffer().getErrorTokens();
			final ErrorToken token = tokens.get(0);

			assertEquals("Invalid uniqueness validation result", 1, tokens.size());
			assertEquals("Invalid constraint violation error token", 422, fex.getStatus());
			assertEquals("Invalid constraint violation error token", "name", token.getProperty());
			assertEquals("Invalid constraint violation error token", "SchemaNode", token.getType());
			assertEquals("Invalid constraint violation error token", "must_match", token.getToken());
		}

		try (final Tx tx = app.tx()) {

			app.create(SchemaNode.class, "7Number");
			tx.success();

			fail("SchemaNode name constraint violation!");

		} catch (FrameworkException fex) {

			final List<ErrorToken> tokens = fex.getErrorBuffer().getErrorTokens();
			final ErrorToken token = tokens.get(0);

			assertEquals("Invalid uniqueness validation result", 1, tokens.size());
			assertEquals("Invalid constraint violation error token", 422, fex.getStatus());
			assertEquals("Invalid constraint violation error token", "name", token.getProperty());
			assertEquals("Invalid constraint violation error token", "SchemaNode", token.getType());
			assertEquals("Invalid constraint violation error token", "must_match", token.getToken());
		}

		try (final Tx tx = app.tx()) {

			app.create(SchemaNode.class, "Valid");
			app.create(SchemaNode.class, "Valid");
			tx.success();

			fail("SchemaNode uniqueness constraint violation!");

		} catch (FrameworkException fex) {

			final List<ErrorToken> tokens = fex.getErrorBuffer().getErrorTokens();
			final ErrorToken token = tokens.get(0);

			assertEquals("Invalid uniqueness validation result", 1, tokens.size());
			assertEquals("Invalid constraint violation error token", 422, fex.getStatus());
			assertEquals("Invalid constraint violation error token", "name", token.getProperty());
			assertEquals("Invalid constraint violation error token", "SchemaNode", token.getType());
			assertEquals("Invalid constraint violation error token", "already_taken", token.getToken());
		}
	}

	@Test
	public void testGlobalUniqueness() {

		this.cleanDatabaseAndSchema();

		try (final Tx tx = app.tx()) {

			app.create(SchemaNode.class, new NodeAttribute<>(AbstractNode.name, "Test"));
			tx.success();

		} catch (FrameworkException fex) {
			fail("Unexpected exception.");
		}

		final Class testType = StructrApp.getConfiguration().getNodeEntityClass("Test");
		if (testType != null) {

			String uuid = null;

			try (final Tx tx = app.tx()) {

				uuid = app.create(testType, new NodeAttribute<>(GraphObject.id, "00000000000000000000000000000000")).getUuid();
				tx.success();

			} catch (FrameworkException fex) {

				fail("Unexpected exception.");
			}

			for (int i=0; i<5; i++) {

				try (final Tx tx = app.tx()) {

					app.create(testType, new NodeAttribute<>(GraphObject.id, "00000000000000000000000000000000"));
					tx.success();

					fail("UUID uniqueness constraint violated!");

				} catch (FrameworkException fex) {

					final ErrorToken token = fex.getErrorBuffer().getErrorTokens().get(0);

					assertEquals("Invalid UUID uniqueness validation result", 422, fex.getStatus());
					assertEquals("Invalid UUID uniqueness validation result", "id", token.getProperty());
					assertEquals("Invalid UUID uniqueness validation result", "Test", token.getType());
					assertEquals("Invalid UUID uniqueness validation result", "already_taken", token.getToken());
					assertEquals("Invalid UUID uniqueness validation result", uuid, token.getDetail());
				}
			}

		}
	}

	@Test
	public void testConcurrentValidation() {

		this.cleanDatabaseAndSchema();

		final int count = 100;

		try (final Tx tx = app.tx()) {

			app.create(SchemaNode.class,
				new NodeAttribute(SchemaNode.name, "Item"),
				new NodeAttribute(new StringProperty("_name"), "+String!")
			);

			tx.success();

		} catch (FrameworkException fex) {

			logger.warn("", fex);
			fail("Unexpected exception.");
		}


		final Class type = StructrApp.getConfiguration().getNodeEntityClass("Item");
		assertNotNull(type);


		final PropertyKey name = StructrApp.key(type, "name");
		assertNotNull(name);

		final Runnable tester = new Runnable() {

			@Override
			public void run() {

				for (int i=0; i<count; i++) {

					// testing must be done in an isolated transaction
					try (final Tx tx = app.tx()) {

						app.create(type, "Item" + i);

						tx.success();

					} catch (FrameworkException ignore) {}


				}
			}
		};

		// submit three test instances
		final ExecutorService executor = Executors.newCachedThreadPool();
		final Future f1                = executor.submit(tester);
		final Future f2                = executor.submit(tester);
		final Future f3                = executor.submit(tester);

		try {
			f1.get();
			f2.get();
			f3.get();

		} catch (Throwable ex) {}

		List<GraphObject> result = null;

		try (final Tx tx = app.tx()) {

			result = app.nodeQuery(type).getAsList();


			tx.success();

		} catch (FrameworkException fex) {

			logger.warn("", fex);
			fail("Unexpected exception.");
		}


		// verify that only count entities have been created.
		assertEquals("Invalid concurrent validation result", count, result.size());


		executor.shutdownNow();
	}

	@Test
	public void testConcurrentValidationWithInheritance() {

		this.cleanDatabaseAndSchema();

		final int count = 100;

		try (final Tx tx = app.tx()) {

			app.create(SchemaNode.class,
				new NodeAttribute(SchemaNode.name, "Item"),
				new NodeAttribute(new StringProperty("_name"), "+String!")
			);

			app.create(SchemaNode.class,
				new NodeAttribute(SchemaNode.name, "ItemDerived"),
				new NodeAttribute(SchemaNode.extendsClass, "org.structr.dynamic.Item")
			);

			tx.success();

		} catch (FrameworkException fex) {

			logger.warn("", fex);
			fail("Unexpected exception.");
		}


		final Class baseType    = StructrApp.getConfiguration().getNodeEntityClass("Item");
		final Class derivedType = StructrApp.getConfiguration().getNodeEntityClass("ItemDerived");

		assertNotNull(baseType);
		assertNotNull(derivedType);

		final PropertyKey name = StructrApp.key(baseType, "name");
		assertNotNull(name);

		final Runnable tester = new Runnable() {

			@Override
			public void run() {

				for (int i=0; i<count; i++) {

					// testing must be done in an isolated transaction
					try (final Tx tx = app.tx()) {

						if (Math.random() < 0.5) {

							app.create(derivedType, "Item" + i);

						} else {

							app.create(baseType, "Item" + i);
						}

						tx.success();

					} catch (FrameworkException ignore) {}
				}
			}
		};

		// submit three test instances
		final ExecutorService executor = Executors.newCachedThreadPool();
		final Future f1                = executor.submit(tester);
		final Future f2                = executor.submit(tester);
		final Future f3                = executor.submit(tester);

		try {
			f1.get();
			f2.get();
			f3.get();

		} catch (Throwable ex) {}


		List<GraphObject> result = null;

		try (final Tx tx = app.tx()) {

			result = app.nodeQuery(baseType).getAsList();


			tx.success();

		} catch (FrameworkException fex) {

			logger.warn("", fex);
			fail("Unexpected exception.");
		}


		// verify that only count entities have been created.
		assertEquals("Invalid concurrent validation result", count, result.size());


		executor.shutdownNow();
	}

	@Test
	public void testConcurrentValidationOnDynamicProperty() {

		this.cleanDatabaseAndSchema();

		final int count = 100;

		try (final Tx tx = app.tx()) {

			app.create(SchemaNode.class,
				new NodeAttribute(SchemaNode.name, "Item"),
				new NodeAttribute(new StringProperty("_testXYZ"), "+String!")
			);

			tx.success();

		} catch (FrameworkException fex) {

			logger.warn("", fex);
			fail("Unexpected exception.");
		}


		final Class type = StructrApp.getConfiguration().getNodeEntityClass("Item");
		assertNotNull(type);


		final PropertyKey testXYZ = StructrApp.key(type, "testXYZ");
		assertNotNull(testXYZ);

		final Runnable tester = new Runnable() {

			@Override
			public void run() {

				for (int i=0; i<count; i++) {

					// testing must be done in an isolated transaction
					try (final Tx tx = app.tx()) {

						app.create(type, new NodeAttribute(testXYZ, "Item" + i));

						tx.success();

					} catch (FrameworkException fex) {}

				}
			}
		};

		// submit three test instances
		final ExecutorService executor = Executors.newCachedThreadPool();
		final Future f1                = executor.submit(tester);
		final Future f2                = executor.submit(tester);
		final Future f3                = executor.submit(tester);

		try {
			f1.get();
			f2.get();
			f3.get();

		} catch (Throwable ex) {}


		List<GraphObject> result = null;

		try (final Tx tx = app.tx()) {

			result = app.nodeQuery(type).getAsList();

			tx.success();

		} catch (FrameworkException fex) {

			logger.warn("", fex);
			fail("Unexpected exception.");
		}


		// verify that only count entities have been created.
		assertEquals("Invalid concurrent validation result", count, result.size());

		executor.shutdownNow();
	}

	@Test
	public void testNamePropertyValidation() {

		this.cleanDatabaseAndSchema();

		// The goal of this test is to ensure that validation
		// only includes actual derived classes.

		// override name property
		try (final Tx tx = app.tx()) {

			// create some nodes with identical names
			app.create(Group.class,   "unique");
			app.create(TestOne.class, "unique");

			tx.success();

		} catch (FrameworkException fex) {
			logger.warn("", fex);
			fail("Unexpected exception.");
		}

		try (final Tx tx = app.tx()) {

			// should succeed
			app.create(TestTwelve.class, new NodeAttribute<>(AbstractNode.name, "unique"));

			tx.success();

		} catch (FrameworkException fex) {

			fail("Uniqueness constraint includes wrong type(s)!");
		}
	}

	// ----- string property validation tests -----
	@Test
	public void testEmptyStringPropertyValidationWithEmptyStrings() {

		this.cleanDatabaseAndSchema();

		try (final Tx tx = app.tx()) {

			app.create(SchemaNode.class,
				new NodeAttribute<>(AbstractNode.name, "Test"),
				new NodeAttribute<>(new StringProperty("_testUnique"), "String!")
			);

			tx.success();

		} catch (FrameworkException fex) {
			logger.warn("", fex);
			fail("Unexpected exception.");
		}

		final Class testType = StructrApp.getConfiguration().getNodeEntityClass("Test");
		if (testType != null) {

			final PropertyKey key = StructrApp.key(testType, "testUnique");
			if (key != null) {

				String uuid = null;

				try (final Tx tx = app.tx()) {

					// for string properties, null equals the empty string
					uuid = app.create(testType, new NodeAttribute<>(key, "")).getUuid();
					app.create(testType, new NodeAttribute<>(key, ""));

					tx.success();

					fail("Uniqueness constraint violated!");

				} catch (FrameworkException fex) {

					final List<ErrorToken> tokens = fex.getErrorBuffer().getErrorTokens();
					final ErrorToken token = tokens.get(0);

					assertEquals("Invalid uniqueness validation result", 1,       tokens.size());
					assertEquals("Invalid uniqueness validation result", 422, fex.getStatus());
					assertEquals("Invalid uniqueness validation result", "testUnique", token.getProperty());
					assertEquals("Invalid uniqueness validation result", "Test", token.getType());
					assertEquals("Invalid uniqueness validation result", "already_taken", token.getToken());
					assertEquals("Invalid uniqueness validation result", uuid, token.getDetail());
				}
			}
		}
	}

	@Test
	public void testEmptyStringPropertyValidationWithNulls() {

		this.cleanDatabaseAndSchema();

		try (final Tx tx = app.tx()) {

			app.create(SchemaNode.class,
				new NodeAttribute<>(AbstractNode.name, "Test"),
				new NodeAttribute<>(new StringProperty("_testUnique"), "String!")
			);

			tx.success();

		} catch (FrameworkException fex) {
			logger.warn("", fex);
			fail("Unexpected exception.");
		}

		final Class testType = StructrApp.getConfiguration().getNodeEntityClass("Test");
		if (testType != null) {

			final PropertyKey key = StructrApp.key(testType, "testUnique");
			if (key != null) {

				String uuid = null;

				try (final Tx tx = app.tx()) {

					// for string properties, null equals the empty string
					uuid = app.create(testType, new NodeAttribute<>(key, null)).getUuid();
					app.create(testType, new NodeAttribute<>(key, null));

					tx.success();

				} catch (FrameworkException fex) {

					fail("Invalid uniqueness constraint validation for null values!");
				}
			}
		}
	}

	@Test
	public void testStringPropertyUniqueness() {

		this.cleanDatabaseAndSchema();

		try (final Tx tx = app.tx()) {

			app.create(SchemaNode.class,
				new NodeAttribute<>(AbstractNode.name, "Test"),
				new NodeAttribute<>(new StringProperty("_testUnique"), "String!")
			);

			tx.success();

		} catch (FrameworkException fex) {
			logger.warn("", fex);
			fail("Unexpected exception.");
		}

		final Class testType = StructrApp.getConfiguration().getNodeEntityClass("Test");
		if (testType != null) {

			final PropertyKey key = StructrApp.key(testType, "testUnique");
			if (key != null) {

				try (final Tx tx = app.tx()) {

					// key must be unique, but can empty
					app.create(testType, new NodeAttribute<>(key, "unique"));
					app.create(testType, new NodeAttribute<>(key, ""));
					tx.success();

				} catch (FrameworkException fex) {
					logger.warn("", fex);
					fail("Unexpected exception.");
				}

				for (int i=0; i<5; i++) {

					try (final Tx tx = app.tx()) {

						app.create(testType, new NodeAttribute<>(key, "unique"));
						tx.success();

						fail("Uniqueness constraint violated!");

					} catch (FrameworkException fex) {

						final List<ErrorToken> tokens = fex.getErrorBuffer().getErrorTokens();
						final ErrorToken token = tokens.get(0);

						assertEquals("Invalid uniqueness validation result", 1,       tokens.size());
						assertEquals("Invalid uniqueness validation result", 422, fex.getStatus());
						assertEquals("Invalid uniqueness validation result", "testUnique", token.getProperty());
						assertEquals("Invalid uniqueness validation result", "Test", token.getType());
						assertEquals("Invalid uniqueness validation result", "already_taken", token.getToken());
					}
				}
			}
		}
	}

	@Test
	public void testInheritedStringPropertyUniqueness() {

		this.cleanDatabaseAndSchema();

		try (final Tx tx = app.tx()) {

			final SchemaNode testType = app.create(SchemaNode.class,
				new NodeAttribute<>(AbstractNode.name, "Test"),
				new NodeAttribute<>(new StringProperty("_testUnique"), "String!")
			);

			app.create(SchemaNode.class,
				new NodeAttribute<>(AbstractNode.name, "TestDerived"),
				new NodeAttribute<>(SchemaNode.extendsClass, "org.structr.dynamic.Test")
			);

			tx.success();

		} catch (FrameworkException fex) {
			fex.printStackTrace();
			fail("Unexpected exception.");
		}

		final Class testType = StructrApp.getConfiguration().getNodeEntityClass("TestDerived");
		if (testType != null) {

			final PropertyKey key = StructrApp.key(testType, "testUnique");
			if (key != null) {

				Settings.CypherDebugLogging.setValue(true);

				try (final Tx tx = app.tx()) {

					// key must be unique, but can empty
					app.create(testType, new NodeAttribute<>(key, "unique"));
					app.create(testType, new NodeAttribute<>(key, ""));
					tx.success();

				} catch (FrameworkException fex) {
					fex.printStackTrace();
					fail("Unexpected exception.");
				}

				Settings.CypherDebugLogging.setValue(true);

				for (int i=0; i<5; i++) {

					try (final Tx tx = app.tx()) {

						app.create(testType, new NodeAttribute<>(key, "unique"));
						tx.success();

						fail("Uniqueness constraint violated!");

					} catch (FrameworkException fex) {

						final List<ErrorToken> tokens = fex.getErrorBuffer().getErrorTokens();
						final ErrorToken token = tokens.get(0);

						assertEquals("Invalid uniqueness validation result", 1,       tokens.size());
						assertEquals("Invalid uniqueness validation result", 422, fex.getStatus());
						assertEquals("Invalid uniqueness validation result", "testUnique", token.getProperty());
						assertEquals("Invalid uniqueness validation result", "TestDerived", token.getType());
						assertEquals("Invalid uniqueness validation result", "already_taken", token.getToken());
					}
				}
			}
		}
	}

	@Test
	public void testStringPropertyUniquenessAgain() {

		this.cleanDatabaseAndSchema();

		try (final Tx tx = app.tx()) {

			app.create(SchemaNode.class,
				new NodeAttribute<>(AbstractNode.name, "Test"),
				new NodeAttribute<>(new StringProperty("_testUnique"), "String!")
			);

			tx.success();

		} catch (FrameworkException fex) {
			logger.warn("", fex);
			fail("Unexpected exception.");
		}

		final Class testType = StructrApp.getConfiguration().getNodeEntityClass("Test");
		if (testType != null) {

			final PropertyKey key = StructrApp.key(testType, "testUnique");
			if (key != null) {

				final Random random = new Random();

				try (final Tx tx = app.tx()) {

					app.create(testType, new NodeAttribute<>(AbstractNode.name, "unique06"), new NodeAttribute<>(key, "unique00"));
					app.create(testType, new NodeAttribute<>(AbstractNode.name, "unique05"), new NodeAttribute<>(key, "unique01"));
					app.create(testType, new NodeAttribute<>(AbstractNode.name, "unique04"), new NodeAttribute<>(key, "unique02"));
					app.create(testType, new NodeAttribute<>(AbstractNode.name, "unique03"), new NodeAttribute<>(key, "unique03"));
					app.create(testType, new NodeAttribute<>(AbstractNode.name, "unique02"), new NodeAttribute<>(key, "unique04"));
					app.create(testType, new NodeAttribute<>(AbstractNode.name, "unique01"), new NodeAttribute<>(key, "unique05"));
					app.create(testType, new NodeAttribute<>(AbstractNode.name, "unique00"), new NodeAttribute<>(key, "unique06"));
					tx.success();

				} catch (FrameworkException fex) {
					logger.warn("", fex);
					fail("Unexpected exception.");
				}

				for (int i=0; i<5; i++) {

					try (final Tx tx = app.tx()) {

						app.create(testType, new NodeAttribute<>(key, "unique0" + random.nextInt(7)));
						tx.success();

						fail("Uniqueness constraint violated!");

					} catch (FrameworkException fex) {

						final List<ErrorToken> tokens = fex.getErrorBuffer().getErrorTokens();
						final ErrorToken token = tokens.get(0);

						assertEquals("Invalid uniqueness validation result", 1,       tokens.size());
						assertEquals("Invalid uniqueness validation result", 422, fex.getStatus());
						assertEquals("Invalid uniqueness validation result", "testUnique", token.getProperty());
						assertEquals("Invalid uniqueness validation result", "Test", token.getType());
						assertEquals("Invalid uniqueness validation result", "already_taken", token.getToken());
					}
				}
			}
		}
	}

	@Test
	public void testStringPropertyNotNull() {

		this.cleanDatabaseAndSchema();

		try (final Tx tx = app.tx()) {

			app.create(SchemaNode.class,
				new NodeAttribute<>(AbstractNode.name, "Test"),
				new NodeAttribute<>(new StringProperty("_nonempty"), "+String")
			);

			tx.success();

		} catch (FrameworkException fex) {
			logger.warn("", fex);
			fail("Unexpected exception.");
		}

		final Class testType = StructrApp.getConfiguration().getNodeEntityClass("Test");
		if (testType != null) {

			final PropertyKey key = StructrApp.key(testType, "nonempty");
			if (key != null) {

				try (final Tx tx = app.tx()) {

					app.create(testType, new NodeAttribute<>(key, null));
					tx.success();

					fail("Not empty constraint violated!");

				} catch (FrameworkException fex) {

					final List<ErrorToken> tokens = fex.getErrorBuffer().getErrorTokens();
					final ErrorToken token = tokens.get(0);

					assertEquals("Invalid uniqueness validation result", 1,       tokens.size());
					assertEquals("Invalid uniqueness validation result", 422, fex.getStatus());
					assertEquals("Invalid uniqueness validation result", "nonempty", token.getProperty());
					assertEquals("Invalid uniqueness validation result", "Test", token.getType());
					assertEquals("Invalid uniqueness validation result", "must_not_be_empty", token.getToken());
				}
			}
		}
	}

	@Test
	public void testStringPropertyRegexMatch() {

		this.cleanDatabaseAndSchema();

		final String keyName  = "regex";
		final Class testType  = createTypeWithProperty("Test", keyName, "String([a-zA-Z0-9]+)");
		final PropertyKey key = StructrApp.key(testType, keyName);

		if (key != null) {

			try (final Tx tx = app.tx()) {

				app.create(testType, new NodeAttribute<>(key, "abcdefg_"));
				tx.success();

				fail("Regex constraint violated!");

			} catch (FrameworkException fex) {

				final List<ErrorToken> tokens = fex.getErrorBuffer().getErrorTokens();
				final ErrorToken token = tokens.get(0);

				assertEquals("Invalid uniqueness validation result", 1,       tokens.size());
				assertEquals("Invalid regex validation result", 422,          fex.getStatus());
				assertEquals("Invalid regex validation result", "regex",      token.getProperty());
				assertEquals("Invalid regex validation result", "Test",       token.getType());
				assertEquals("Invalid regex validation result", "must_match", token.getToken());
			}
		}
	}

	// ----- array property validation tests -----
	@Test
	public void testArrayPropertyNotNullValidation() {

		this.cleanDatabaseAndSchema();

		final String keyName  = "stringArray";
		final Class testType  = createTypeWithProperty("Test", keyName, "+String[]");
		final PropertyKey key = StructrApp.key(testType, keyName);

		if (key != null) {

			try (final Tx tx = app.tx()) {

				app.create(testType, new NodeAttribute<>(key, null));
				tx.success();

				fail("Array property validation failed!");

			} catch (FrameworkException fex) {

				final ErrorToken token = fex.getErrorBuffer().getErrorTokens().get(0);

				assertEquals("Invalid uniqueness validation result", 422, fex.getStatus());
				assertEquals("Invalid uniqueness validation result", keyName, token.getProperty());
				assertEquals("Invalid uniqueness validation result", "Test", token.getType());
				assertEquals("Invalid uniqueness validation result", "must_not_be_empty", token.getToken());
			}
		}
	}

	@Test
	public void testArrayPropertyUniquenessValidation() {

		this.cleanDatabaseAndSchema();

		final String keyName                = "stringArray";
		final Class<NodeInterface> testType = createTypeWithProperty("Test", keyName, "String[]!");
		final PropertyKey key               = StructrApp.key(testType, keyName);
		String uuid1                        = null;
		String uuid2                        = null;

		if (key != null) {

			// test failure
			try (final Tx tx = app.tx()) {

				uuid1 = app.create(testType, new NodeAttribute<>(key, new String[] { "one", "two" } )).getUuid();

				// make sure creation of the two objects is more than 1ms apart
				try { Thread.sleep(1); }catch (Throwable t) {}

				uuid2 = app.create(testType, new NodeAttribute<>(key, new String[] { "one", "two" } )).getUuid();

				tx.success();

				fail("Array property validation failed!");

			} catch (FrameworkException fex) {

				final List<ErrorToken> tokens = fex.getErrorBuffer().getErrorTokens();
				final ErrorToken token = tokens.get(0);

				assertEquals("Invalid uniqueness validation result", 1, tokens.size());
				assertEquals("Invalid uniqueness validation result", 422, fex.getStatus());
				assertEquals("Invalid uniqueness validation result", keyName, token.getProperty());
				assertEquals("Invalid uniqueness validation result", "Test", token.getType());
				assertEquals("Invalid uniqueness validation result", "already_taken", token.getToken());
				assertEquals("Invalid uniqueness validation result", uuid1, token.getDetail());
			}

			removeInstances(testType);

			// test success
			try (final Tx tx = app.tx()) {

				app.create(testType, new NodeAttribute<>(key, new String[] { "one" } ));
				app.create(testType, new NodeAttribute<>(key, new String[] { "one", "two" } ));
				app.create(testType, new NodeAttribute<>(key, new String[] { "one", "two", "three" } ));
				tx.success();

			} catch (FrameworkException fex) {

				fail("Array property validation error!");
			}
		}
	}

	// ----- boolean property validation tests -----
	@Test
	public void testBooleanPropertyNotNullValidation() {

		this.cleanDatabaseAndSchema();

		final String keyName                = "notNull";
		final Class<NodeInterface> testType = createTypeWithProperty("Test", keyName, "+Boolean");
		final PropertyKey key               = StructrApp.key(testType, keyName);

		if (key != null) {

			// test failure
			try (final Tx tx = app.tx()) {

				/* Boolean properties are special. A boolean property will
				 * _always_ have a value, i.e. "null" equals "false" and
				 * will be returned as false.
				 *
				 * => a boolean property value can never be null!
				 */

				app.create(testType, new NodeAttribute<>(key, null));
				app.create(testType, new NodeAttribute<>(key, true));
				app.create(testType, new NodeAttribute<>(key, false));
				tx.success();

			} catch (FrameworkException fex) {
				fail("Unexpected array property validation exception.");
			}
		}
	}

	@Test
	public void testBooleanPropertyUniquenessValidation() {

		this.cleanDatabaseAndSchema();

		final String keyName                = "unique";
		final Class<NodeInterface> testType = createTypeWithProperty("Test", keyName, "Boolean!");
		final PropertyKey key               = StructrApp.key(testType, keyName);
		String uuid                         = null;

		if (key != null) {

			// test failure
			try (final Tx tx = app.tx()) {

				uuid = app.create(testType, new NodeAttribute<>(key, true)).getUuid();

				// make sure creation of the two objects is more than 1ms apart
				try { Thread.sleep(1); }catch (Throwable t) {}

				app.create(testType, new NodeAttribute<>(key, false));
				app.create(testType, new NodeAttribute<>(key, true));

				tx.success();

				fail("Array property uniqueness constraint violated!");

			} catch (FrameworkException fex) {

				final List<ErrorToken> tokens = fex.getErrorBuffer().getErrorTokens();
				final ErrorToken token = tokens.get(0);

				assertEquals("Invalid uniqueness validation result", 1, tokens.size());
				assertEquals("Invalid uniqueness validation result", 422, fex.getStatus());
				assertEquals("Invalid uniqueness validation result", keyName, token.getProperty());
				assertEquals("Invalid uniqueness validation result", "Test", token.getType());
				assertEquals("Invalid uniqueness validation result", "already_taken", token.getToken());
				assertEquals("Invalid uniqueness validation result", uuid, token.getDetail());
			}
		}
	}

	// ----- date property validation tests -----
	@Test
	public void testDatePropertyUniquenessValidation() {

		this.cleanDatabaseAndSchema();

		final String keyName                = "unique";
		final Class<NodeInterface> testType = createTypeWithProperty("Test", keyName, "Date!");
		final PropertyKey key               = StructrApp.key(testType, keyName);
		final Date date                     = new Date();
		String uuid                         = null;

		if (key != null) {

			// test failure
			try (final Tx tx = app.tx()) {

				uuid = app.create(testType, new NodeAttribute<>(key, date)).getUuid();

				// make sure creation of the two objects is more than 1ms apart
				try { Thread.sleep(1); }catch (Throwable t) {}

				app.create(testType, new NodeAttribute<>(key, date));

				tx.success();

				fail("Date property uniqueness constraint violated!");

			} catch (FrameworkException fex) {

				final List<ErrorToken> tokens = fex.getErrorBuffer().getErrorTokens();
				final ErrorToken token = tokens.get(0);

				assertEquals("Invalid date validation result", 1, tokens.size());
				assertEquals("Invalid date validation result", 422, fex.getStatus());
				assertEquals("Invalid date validation result", keyName, token.getProperty());
				assertEquals("Invalid date validation result", "Test", token.getType());
				assertEquals("Invalid date validation result", "already_taken", token.getToken());
				assertEquals("Invalid date validation result", uuid, token.getDetail());
			}
		}
	}

	@Test
	public void testDatePropertyNotNullValidation() {

		this.cleanDatabaseAndSchema();

		final String keyName                = "notnull";
		final Class<NodeInterface> testType = createTypeWithProperty("Test", keyName, "+Date");
		final PropertyKey key               = StructrApp.key(testType, keyName);

		if (key != null) {

			// test failure
			try (final Tx tx = app.tx()) {

				app.create(testType, new NodeAttribute<>(key, null));

				tx.success();

				fail("Date property not null constraint violated!");

			} catch (FrameworkException fex) {

				final List<ErrorToken> tokens = fex.getErrorBuffer().getErrorTokens();
				final ErrorToken token = tokens.get(0);

				assertEquals("Invalid date validation result", 1, tokens.size());
				assertEquals("Invalid date validation result", 422, fex.getStatus());
				assertEquals("Invalid date validation result", keyName, token.getProperty());
				assertEquals("Invalid date validation result", "Test", token.getType());
				assertEquals("Invalid date validation result", "must_not_be_empty", token.getToken());
			}
		}
	}

	// ----- double property validation tests -----
	@Test
	public void testDoublePropertyUniquenessValidation() {

		this.cleanDatabaseAndSchema();

		final String keyName                = "unique";
		final Class<NodeInterface> testType = createTypeWithProperty("Test", keyName, "Double!");
		final PropertyKey key               = StructrApp.key(testType, keyName);
		String uuid                         = null;

		if (key != null) {

			// test failure
			try (final Tx tx = app.tx()) {

				uuid = app.create(testType, new NodeAttribute<>(key, 0.123)).getUuid();

				// make sure creation of the two objects is more than 1ms apart
				try { Thread.sleep(1); }catch (Throwable t) {}

				app.create(testType, new NodeAttribute<>(key, 0.123));

				tx.success();

				fail("Double property uniqueness constraint violated!");

			} catch (FrameworkException fex) {

				final List<ErrorToken> tokens = fex.getErrorBuffer().getErrorTokens();
				final ErrorToken token = tokens.get(0);

				assertEquals("Invalid double validation result", 1, tokens.size());
				assertEquals("Invalid double validation result", 422, fex.getStatus());
				assertEquals("Invalid double validation result", keyName, token.getProperty());
				assertEquals("Invalid double validation result", "Test", token.getType());
				assertEquals("Invalid double validation result", "already_taken", token.getToken());
				assertEquals("Invalid double validation result", uuid, token.getDetail());
			}
		}
	}

	@Test
	public void testDoublePropertyNotNullValidation() {

		this.cleanDatabaseAndSchema();

		final String keyName                = "notnull";
		final Class<NodeInterface> testType = createTypeWithProperty("Test", keyName, "+Double");
		final PropertyKey key               = StructrApp.key(testType, keyName);

		if (key != null) {

			// test failure
			try (final Tx tx = app.tx()) {

				app.create(testType, new NodeAttribute<>(key, null));

				tx.success();

				fail("Double property not null constraint violated!");

			} catch (FrameworkException fex) {

				final List<ErrorToken> tokens = fex.getErrorBuffer().getErrorTokens();
				final ErrorToken token = tokens.get(0);

				assertEquals("Invalid double validation result", 1, tokens.size());
				assertEquals("Invalid double validation result", 422, fex.getStatus());
				assertEquals("Invalid double validation result", keyName, token.getProperty());
				assertEquals("Invalid double validation result", "Test", token.getType());
				assertEquals("Invalid double validation result", "must_not_be_empty", token.getToken());
			}
		}
	}

	@Test
	public void testDoublePropertyRangeValidation1() {

		this.cleanDatabaseAndSchema();

		final Class<NodeInterface> testType = createTypeWithProperty("Test", "range1", "+Double([1,5])");
		final PropertyKey range1            = StructrApp.key(testType, "range1");

		checkRangeSuccess(testType, range1, 1.0);
		checkRangeSuccess(testType, range1, 1.00001);
		checkRangeSuccess(testType, range1, 1.1);
		checkRangeSuccess(testType, range1, 2.2);
		checkRangeSuccess(testType, range1, 3.3);
		checkRangeSuccess(testType, range1, 4.4);
		checkRangeSuccess(testType, range1, 4.999999);
		checkRangeSuccess(testType, range1, 5.0);

		try { checkRangeError(testType, range1,    -0.00001); } catch (FrameworkException fex) { checkException(fex, 1, 422, "Test", "range1", "must_be_in_range"); }
		try { checkRangeError(testType, range1,     0.00001); } catch (FrameworkException fex) { checkException(fex, 1, 422, "Test", "range1", "must_be_in_range"); }
		try { checkRangeError(testType, range1,        0.51); } catch (FrameworkException fex) { checkException(fex, 1, 422, "Test", "range1", "must_be_in_range"); }
		try { checkRangeError(testType, range1,  5.00000001); } catch (FrameworkException fex) { checkException(fex, 1, 422, "Test", "range1", "must_be_in_range"); }

	}

	@Test
	public void testDoublePropertyRangeValidation2() {

		this.cleanDatabaseAndSchema();

		final Class<NodeInterface> testType = createTypeWithProperty("Test", "range1", "+Double([0.0,0.5])");
		final PropertyKey range1            = StructrApp.key(testType, "range1");

		checkRangeSuccess(testType, range1, -0.0);
		checkRangeSuccess(testType, range1, 0.0);
		checkRangeSuccess(testType, range1, 0.00001);
		checkRangeSuccess(testType, range1, 0.1);
		checkRangeSuccess(testType, range1, 0.2);
		checkRangeSuccess(testType, range1, 0.3);
		checkRangeSuccess(testType, range1, 0.4);
		checkRangeSuccess(testType, range1, 0.49999);
		checkRangeSuccess(testType, range1, 0.5);

		try { checkRangeError(testType, range1, -0.00001); } catch (FrameworkException fex) { checkException(fex, 1, 422, "Test", "range1", "must_be_in_range"); }
		try { checkRangeError(testType, range1,     0.51); } catch (FrameworkException fex) { checkException(fex, 1, 422, "Test", "range1", "must_be_in_range"); }
		try { checkRangeError(testType, range1,     1.51); } catch (FrameworkException fex) { checkException(fex, 1, 422, "Test", "range1", "must_be_in_range"); }

	}

	@Test
	public void testDoublePropertyRangeValidation3() {

		this.cleanDatabaseAndSchema();

		final Class<NodeInterface> testType = createTypeWithProperty("Test", "range1", "+Double([0.0,0.5[)");
		final PropertyKey range1            = StructrApp.key(testType, "range1");

		checkRangeSuccess(testType, range1, -0.0);
		checkRangeSuccess(testType, range1, 0.0);
		checkRangeSuccess(testType, range1, 0.00001);
		checkRangeSuccess(testType, range1, 0.1);
		checkRangeSuccess(testType, range1, 0.2);
		checkRangeSuccess(testType, range1, 0.3);
		checkRangeSuccess(testType, range1, 0.4);
		checkRangeSuccess(testType, range1, 0.49999);

		try { checkRangeError(testType, range1, -0.00001); } catch (FrameworkException fex) { checkException(fex, 1, 422, "Test", "range1", "must_be_in_range"); }
		try { checkRangeError(testType, range1,     0.5); } catch (FrameworkException fex) { checkException(fex, 1, 422, "Test", "range1", "must_be_in_range"); }
		try { checkRangeError(testType, range1,     0.51); } catch (FrameworkException fex) { checkException(fex, 1, 422, "Test", "range1", "must_be_in_range"); }
		try { checkRangeError(testType, range1,     1.51); } catch (FrameworkException fex) { checkException(fex, 1, 422, "Test", "range1", "must_be_in_range"); }

	}

	@Test
	public void testDoublePropertyRangeValidation4() {

		this.cleanDatabaseAndSchema();

		final Class<NodeInterface> testType = createTypeWithProperty("Test", "range1", "+Double(]0.0,0.5])");
		final PropertyKey range1            = StructrApp.key(testType, "range1");

		checkRangeSuccess(testType, range1, 0.00001);
		checkRangeSuccess(testType, range1, 0.1);
		checkRangeSuccess(testType, range1, 0.2);
		checkRangeSuccess(testType, range1, 0.3);
		checkRangeSuccess(testType, range1, 0.4);
		checkRangeSuccess(testType, range1, 0.49999);
		checkRangeSuccess(testType, range1, 0.5);

		try { checkRangeError(testType, range1,     -0.0); } catch (FrameworkException fex) { checkException(fex, 1, 422, "Test", "range1", "must_be_in_range"); }
		try { checkRangeError(testType, range1,      0.0); } catch (FrameworkException fex) { checkException(fex, 1, 422, "Test", "range1", "must_be_in_range"); }
		try { checkRangeError(testType, range1, -0.00001); } catch (FrameworkException fex) { checkException(fex, 1, 422, "Test", "range1", "must_be_in_range"); }
		try { checkRangeError(testType, range1,     0.51); } catch (FrameworkException fex) { checkException(fex, 1, 422, "Test", "range1", "must_be_in_range"); }
		try { checkRangeError(testType, range1,     1.51); } catch (FrameworkException fex) { checkException(fex, 1, 422, "Test", "range1", "must_be_in_range"); }

	}

	@Test
	public void testDoublePropertyRangeValidation5() {

		this.cleanDatabaseAndSchema();

		final Class<NodeInterface> testType = createTypeWithProperty("Test", "range1", "+Double(]0.0,0.5[)");
		final PropertyKey range1            = StructrApp.key(testType, "range1");

		checkRangeSuccess(testType, range1, 0.00001);
		checkRangeSuccess(testType, range1, 0.1);
		checkRangeSuccess(testType, range1, 0.2);
		checkRangeSuccess(testType, range1, 0.3);
		checkRangeSuccess(testType, range1, 0.4);
		checkRangeSuccess(testType, range1, 0.49999);

		try { checkRangeError(testType, range1,     -0.0); } catch (FrameworkException fex) { checkException(fex, 1, 422, "Test", "range1", "must_be_in_range"); }
		try { checkRangeError(testType, range1,      0.0); } catch (FrameworkException fex) { checkException(fex, 1, 422, "Test", "range1", "must_be_in_range"); }
		try { checkRangeError(testType, range1, -0.00001); } catch (FrameworkException fex) { checkException(fex, 1, 422, "Test", "range1", "must_be_in_range"); }
		try { checkRangeError(testType, range1,     0.51); } catch (FrameworkException fex) { checkException(fex, 1, 422, "Test", "range1", "must_be_in_range"); }
		try { checkRangeError(testType, range1,     1.51); } catch (FrameworkException fex) { checkException(fex, 1, 422, "Test", "range1", "must_be_in_range"); }
		try { checkRangeError(testType, range1,      5.0); } catch (FrameworkException fex) { checkException(fex, 1, 422, "Test", "range1", "must_be_in_range"); }
	}

	// ----- enum property validation tests -----
	@Test
	public void testEnumPropertyUniquenessValidation() {

		this.cleanDatabaseAndSchema();

		final String keyName                = "unique";
		final Class<NodeInterface> testType = createTypeWithProperty("Test", keyName, "Enum(one, two, three)!");
		final PropertyKey key               = StructrApp.key(testType, keyName);
		String uuid                         = null;

		if (key != null) {

			// test failure
			try (final Tx tx = app.tx()) {

				final Object value = ((EnumProperty)key).getEnumType().getEnumConstants()[0];

				uuid = app.create(testType, new NodeAttribute<>(key, value)).getUuid();

				// make sure creation of the two objects is more than 1ms apart
				try { Thread.sleep(1); }catch (Throwable t) {}

				app.create(testType, new NodeAttribute<>(key, value));

				tx.success();

				fail("Enum property uniqueness constraint violated!");

			} catch (FrameworkException fex) {

				final List<ErrorToken> tokens = fex.getErrorBuffer().getErrorTokens();
				final ErrorToken token = tokens.get(0);

				assertEquals("Invalid enum validation result", 1, tokens.size());
				assertEquals("Invalid enum validation result", 422, fex.getStatus());
				assertEquals("Invalid enum validation result", keyName, token.getProperty());
				assertEquals("Invalid enum validation result", "Test", token.getType());
				assertEquals("Invalid enum validation result", "already_taken", token.getToken());
				assertEquals("Invalid enum validation result", uuid, token.getDetail());
			}
		}
	}

	@Test
	public void testEnumPropertyNotNullValidation() {

		this.cleanDatabaseAndSchema();

		final String keyName                = "notnull";
		final Class<NodeInterface> testType = createTypeWithProperty("Test", keyName, "+Enum(one, two, three)");
		final PropertyKey key               = StructrApp.key(testType, keyName);

		// test failure
		try (final Tx tx = app.tx()) {

			app.create(testType, new NodeAttribute<>(key, null));

			tx.success();

			fail("Enum property not null constraint violated!");

		} catch (FrameworkException fex) {

			final List<ErrorToken> tokens = fex.getErrorBuffer().getErrorTokens();
			final ErrorToken token = tokens.get(0);

			assertEquals("Invalid enum validation result", 1, tokens.size());
			assertEquals("Invalid enum validation result", 422, fex.getStatus());
			assertEquals("Invalid enum validation result", keyName, token.getProperty());
			assertEquals("Invalid enum validation result", "Test", token.getType());
			assertEquals("Invalid enum validation result", "must_not_be_empty", token.getToken());
		}
	}

	// ----- int property validation tests -----
	@Test
	public void testIntPropertyUniquenessValidation() {

		this.cleanDatabaseAndSchema();

		final String keyName                = "unique";
		final Class<NodeInterface> testType = createTypeWithProperty("Test", keyName, "Integer!");
		final PropertyKey key               = StructrApp.key(testType, keyName);
		String uuid                         = null;

		if (key != null) {

			// test failure
			try (final Tx tx = app.tx()) {

				uuid = app.create(testType, new NodeAttribute<>(key, 42)).getUuid();

				// make sure creation of the two objects is more than 1ms apart
				try { Thread.sleep(1); }catch (Throwable t) {}

				app.create(testType, new NodeAttribute<>(key, 42));

				tx.success();

				fail("Int property uniqueness constraint violated!");

			} catch (FrameworkException fex) {

				final List<ErrorToken> tokens = fex.getErrorBuffer().getErrorTokens();
				final ErrorToken token = tokens.get(0);

				assertEquals("Invalid int validation result", 1, tokens.size());
				assertEquals("Invalid int validation result", 422, fex.getStatus());
				assertEquals("Invalid int validation result", keyName, token.getProperty());
				assertEquals("Invalid int validation result", "Test", token.getType());
				assertEquals("Invalid int validation result", "already_taken", token.getToken());
				assertEquals("Invalid int validation result", uuid, token.getDetail());
			}
		}
	}

	@Test
	public void testIntPropertyNotNullValidation() {

		this.cleanDatabaseAndSchema();

		final String keyName                = "notnull";
		final Class<NodeInterface> testType = createTypeWithProperty("Test", keyName, "+Integer");
		final PropertyKey key               = StructrApp.key(testType, keyName);

		if (key != null) {

			// test failure
			try (final Tx tx = app.tx()) {

				app.create(testType, new NodeAttribute<>(key, null));

				tx.success();

				fail("Int property not null constraint violated!");

			} catch (FrameworkException fex) {

				final List<ErrorToken> tokens = fex.getErrorBuffer().getErrorTokens();
				final ErrorToken token = tokens.get(0);

				assertEquals("Invalid int validation result", 1, tokens.size());
				assertEquals("Invalid int validation result", 422, fex.getStatus());
				assertEquals("Invalid int validation result", keyName, token.getProperty());
				assertEquals("Invalid int validation result", "Test", token.getType());
				assertEquals("Invalid int validation result", "must_not_be_empty", token.getToken());
			}
		}
	}

	@Test
	public void testIntPropertyRangeValidation1() {

		this.cleanDatabaseAndSchema();

		final Class<NodeInterface> testType = createTypeWithProperty("Test", "range1", "+Integer([1,5])");
		final PropertyKey range1            = StructrApp.key(testType, "range1");

		checkRangeSuccess(testType, range1, 1);
		checkRangeSuccess(testType, range1, 2);
		checkRangeSuccess(testType, range1, 3);
		checkRangeSuccess(testType, range1, 4);
		checkRangeSuccess(testType, range1, 5);

		try { checkRangeError(testType, range1, -0); } catch (FrameworkException fex) { checkException(fex, 1, 422, "Test", "range1", "must_be_in_range"); }
		try { checkRangeError(testType, range1,  0); } catch (FrameworkException fex) { checkException(fex, 1, 422, "Test", "range1", "must_be_in_range"); }
		try { checkRangeError(testType, range1,  6); } catch (FrameworkException fex) { checkException(fex, 1, 422, "Test", "range1", "must_be_in_range"); }

	}

	@Test
	public void testIntPropertyRangeValidation3() {

		this.cleanDatabaseAndSchema();

		final Class<NodeInterface> testType = createTypeWithProperty("Test", "range1", "+Integer([0,5[)");
		final PropertyKey range1            = StructrApp.key(testType, "range1");

		checkRangeSuccess(testType, range1, -0);
		checkRangeSuccess(testType, range1, 0);
		checkRangeSuccess(testType, range1, 1);
		checkRangeSuccess(testType, range1, 2);
		checkRangeSuccess(testType, range1, 3);
		checkRangeSuccess(testType, range1, 4);

		try { checkRangeError(testType, range1, 5); } catch (FrameworkException fex) { checkException(fex, 1, 422, "Test", "range1", "must_be_in_range"); }
		try { checkRangeError(testType, range1, 6); } catch (FrameworkException fex) { checkException(fex, 1, 422, "Test", "range1", "must_be_in_range"); }

	}

	@Test
	public void testIntPropertyRangeValidation4() {

		this.cleanDatabaseAndSchema();

		final Class<NodeInterface> testType = createTypeWithProperty("Test", "range1", "+Integer(]0,5])");
		final PropertyKey range1            = StructrApp.key(testType, "range1");

		checkRangeSuccess(testType, range1, 1);
		checkRangeSuccess(testType, range1, 2);
		checkRangeSuccess(testType, range1, 3);
		checkRangeSuccess(testType, range1, 4);
		checkRangeSuccess(testType, range1, 5);

		try { checkRangeError(testType, range1, 0); } catch (FrameworkException fex) { checkException(fex, 1, 422, "Test", "range1", "must_be_in_range"); }
		try { checkRangeError(testType, range1, 6); } catch (FrameworkException fex) { checkException(fex, 1, 422, "Test", "range1", "must_be_in_range"); }
	}

	@Test
	public void testIntPropertyRangeValidation5() {

		this.cleanDatabaseAndSchema();

		final Class<NodeInterface> testType = createTypeWithProperty("Test", "range1", "+Integer(]0,5[)");
		final PropertyKey range1            = StructrApp.key(testType, "range1");

		checkRangeSuccess(testType, range1, 1);
		checkRangeSuccess(testType, range1, 2);
		checkRangeSuccess(testType, range1, 3);
		checkRangeSuccess(testType, range1, 4);

		try { checkRangeError(testType, range1, 0); } catch (FrameworkException fex) { checkException(fex, 1, 422, "Test", "range1", "must_be_in_range"); }
		try { checkRangeError(testType, range1, 5); } catch (FrameworkException fex) { checkException(fex, 1, 422, "Test", "range1", "must_be_in_range"); }
		try { checkRangeError(testType, range1, 6); } catch (FrameworkException fex) { checkException(fex, 1, 422, "Test", "range1", "must_be_in_range"); }
	}

	// ----- long property validation tests -----
	@Test
	public void testLongPropertyUniquenessValidation() {

		this.cleanDatabaseAndSchema();

		final String keyName                = "unique";
		final Class<NodeInterface> testType = createTypeWithProperty("Test", keyName, "Long!");
		final PropertyKey key               = StructrApp.key(testType, keyName);
		String uuid                         = null;

		if (key != null) {

			// test failure
			try (final Tx tx = app.tx()) {

				uuid = app.create(testType, new NodeAttribute<>(key, 42000000000L)).getUuid();

				// make sure creation of the two objects is more than 1ms apart
				try { Thread.sleep(1); }catch (Throwable t) {}

				app.create(testType, new NodeAttribute<>(key, 42000000000L));

				tx.success();

				fail("Long property uniqueness constraint violated!");

			} catch (FrameworkException fex) {

				final List<ErrorToken> tokens = fex.getErrorBuffer().getErrorTokens();
				final ErrorToken token = tokens.get(0);

				assertEquals("Invalid long validation result", 1, tokens.size());
				assertEquals("Invalid long validation result", 422, fex.getStatus());
				assertEquals("Invalid long validation result", keyName, token.getProperty());
				assertEquals("Invalid long validation result", "Test", token.getType());
				assertEquals("Invalid long validation result", "already_taken", token.getToken());
				assertEquals("Invalid long validation result", uuid, token.getDetail());
			}
		}
	}

	@Test
	public void testLongPropertyNotNullValidation() {

		this.cleanDatabaseAndSchema();

		final String keyName                = "notnull";
		final Class<NodeInterface> testType = createTypeWithProperty("Test", keyName, "+Long");
		final PropertyKey key               = StructrApp.key(testType, keyName);

		if (key != null) {

			// test failure
			try (final Tx tx = app.tx()) {

				app.create(testType, new NodeAttribute<>(key, null));

				tx.success();

				fail("Long property not null constraint violated!");

			} catch (FrameworkException fex) {

				final List<ErrorToken> tokens = fex.getErrorBuffer().getErrorTokens();
				final ErrorToken token = tokens.get(0);

				assertEquals("Invalid long validation result", 1, tokens.size());
				assertEquals("Invalid long validation result", 422, fex.getStatus());
				assertEquals("Invalid long validation result", keyName, token.getProperty());
				assertEquals("Invalid long validation result", "Test", token.getType());
				assertEquals("Invalid long validation result", "must_not_be_empty", token.getToken());
			}
		}
	}

	@Test
	public void testLongPropertyRangeValidation1() {

		this.cleanDatabaseAndSchema();

		final Class<NodeInterface> testType = createTypeWithProperty("Test", "range1", "+Long([1,5])");
		final PropertyKey range1            = StructrApp.key(testType, "range1");

		checkRangeSuccess(testType, range1, 1);
		checkRangeSuccess(testType, range1, 2);
		checkRangeSuccess(testType, range1, 3);
		checkRangeSuccess(testType, range1, 4);
		checkRangeSuccess(testType, range1, 5);

		try { checkRangeError(testType, range1, -0); } catch (FrameworkException fex) { checkException(fex, 1, 422, "Test", "range1", "must_be_in_range"); }
		try { checkRangeError(testType, range1,  0); } catch (FrameworkException fex) { checkException(fex, 1, 422, "Test", "range1", "must_be_in_range"); }
		try { checkRangeError(testType, range1,  6); } catch (FrameworkException fex) { checkException(fex, 1, 422, "Test", "range1", "must_be_in_range"); }

	}

	@Test
	public void testLongPropertyRangeValidation3() {

		this.cleanDatabaseAndSchema();

		final Class<NodeInterface> testType = createTypeWithProperty("Test", "range1", "+Long([0,5[)");
		final PropertyKey range1            = StructrApp.key(testType, "range1");

		checkRangeSuccess(testType, range1, -0);
		checkRangeSuccess(testType, range1, 0);
		checkRangeSuccess(testType, range1, 1);
		checkRangeSuccess(testType, range1, 2);
		checkRangeSuccess(testType, range1, 3);
		checkRangeSuccess(testType, range1, 4);

		try { checkRangeError(testType, range1, 5); } catch (FrameworkException fex) { checkException(fex, 1, 422, "Test", "range1", "must_be_in_range"); }
		try { checkRangeError(testType, range1, 6); } catch (FrameworkException fex) { checkException(fex, 1, 422, "Test", "range1", "must_be_in_range"); }

	}

	@Test
	public void testLongPropertyRangeValidation4() {

		this.cleanDatabaseAndSchema();

		final Class<NodeInterface> testType = createTypeWithProperty("Test", "range1", "+Long(]0,5])");
		final PropertyKey range1            = StructrApp.key(testType, "range1");

		checkRangeSuccess(testType, range1, 1);
		checkRangeSuccess(testType, range1, 2);
		checkRangeSuccess(testType, range1, 3);
		checkRangeSuccess(testType, range1, 4);
		checkRangeSuccess(testType, range1, 5);

		try { checkRangeError(testType, range1, 0); } catch (FrameworkException fex) { checkException(fex, 1, 422, "Test", "range1", "must_be_in_range"); }
		try { checkRangeError(testType, range1, 6); } catch (FrameworkException fex) { checkException(fex, 1, 422, "Test", "range1", "must_be_in_range"); }
	}

	@Test
	public void testLongPropertyRangeValidation5() {

		this.cleanDatabaseAndSchema();

		final Class<NodeInterface> testType = createTypeWithProperty("Test", "range1", "+Long(]0,5[)");
		final PropertyKey range1            = StructrApp.key(testType, "range1");

		checkRangeSuccess(testType, range1, 1);
		checkRangeSuccess(testType, range1, 2);
		checkRangeSuccess(testType, range1, 3);
		checkRangeSuccess(testType, range1, 4);

		try { checkRangeError(testType, range1, 0); } catch (FrameworkException fex) { checkException(fex, 1, 422, "Test", "range1", "must_be_in_range"); }
		try { checkRangeError(testType, range1, 5); } catch (FrameworkException fex) { checkException(fex, 1, 422, "Test", "range1", "must_be_in_range"); }
		try { checkRangeError(testType, range1, 6); } catch (FrameworkException fex) { checkException(fex, 1, 422, "Test", "range1", "must_be_in_range"); }
	}

	// schema relationship node validation
	@Test
	public void testSchemaRelationshipNodeValidation() {

		this.cleanDatabaseAndSchema();

		try (final Tx tx = app.tx()) {

			app.create(SchemaRelationshipNode.class,
				new NodeAttribute<>(SchemaRelationshipNode.sourceNode, null),
				new NodeAttribute<>(SchemaRelationshipNode.targetNode, null)
			);

			tx.success();

			fail("SchemaRelationshipNode constraint violation, source and target node must not be null.");

		} catch (FrameworkException fex) {

			final List<ErrorToken> tokens = fex.getErrorBuffer().getErrorTokens();

			assertEquals("Invalid SchemaRelationshipNode validation result", 3, tokens.size());
			assertEquals("Invalid SchemaRelationshipNode validation result", 422, fex.getStatus());

			final ErrorToken token1 = tokens.get(0);
			final ErrorToken token2 = tokens.get(1);
			final ErrorToken token3 = tokens.get(2);

			assertEquals("Invalid SchemaRelationshipNode validation result", "SchemaRelationshipNode", token1.getType());
			assertEquals("Invalid SchemaRelationshipNode validation result", "relationshipType", token1.getProperty());
			assertEquals("Invalid SchemaRelationshipNode validation result", "must_not_be_empty", token1.getToken());

			assertEquals("Invalid SchemaRelationshipNode validation result", "SchemaRelationshipNode", token2.getType());
			assertEquals("Invalid SchemaRelationshipNode validation result", "sourceNode", token2.getProperty());
			assertEquals("Invalid SchemaRelationshipNode validation result", "must_not_be_empty", token2.getToken());

			assertEquals("Invalid SchemaRelationshipNode validation result", "SchemaRelationshipNode", token3.getType());
			assertEquals("Invalid SchemaRelationshipNode validation result", "targetNode", token3.getProperty());
			assertEquals("Invalid SchemaRelationshipNode validation result", "must_not_be_empty", token3.getToken());
		}

		try (final Tx tx = app.tx()) {

			app.create(SchemaRelationshipNode.class,
				new NodeAttribute<>(SchemaRelationshipNode.relationshipType, "test"),
				new NodeAttribute<>(SchemaRelationshipNode.sourceNode, null),
				new NodeAttribute<>(SchemaRelationshipNode.targetNode, null)
			);

			tx.success();

			fail("SchemaRelationshipNode constraint violation, source and target node must not be null.");

		} catch (FrameworkException fex) {

			final List<ErrorToken> tokens = fex.getErrorBuffer().getErrorTokens();

			assertEquals("Invalid SchemaRelationshipNode validation result", 2, tokens.size());
			assertEquals("Invalid SchemaRelationshipNode validation result", 422, fex.getStatus());

			final ErrorToken token1 = tokens.get(0);
			final ErrorToken token2 = tokens.get(1);

			assertEquals("Invalid SchemaRelationshipNode validation result", "SchemaRelationshipNode", token1.getType());
			assertEquals("Invalid SchemaRelationshipNode validation result", "sourceNode", token1.getProperty());
			assertEquals("Invalid SchemaRelationshipNode validation result", "must_not_be_empty", token1.getToken());

			assertEquals("Invalid SchemaRelationshipNode validation result", "SchemaRelationshipNode", token2.getType());
			assertEquals("Invalid SchemaRelationshipNode validation result", "targetNode", token2.getProperty());
			assertEquals("Invalid SchemaRelationshipNode validation result", "must_not_be_empty", token2.getToken());
		}

		try (final Tx tx = app.tx()) {

			app.create(SchemaRelationshipNode.class,
				new NodeAttribute<>(SchemaRelationshipNode.relationshipType, "test"),
				new NodeAttribute<>(SchemaRelationshipNode.sourceNode, app.nodeQuery(SchemaNode.class).andName("Group").getFirst()),
				new NodeAttribute<>(SchemaRelationshipNode.targetNode, null)
			);

			tx.success();

			fail("SchemaRelationshipNode constraint violation, target node must not be null.");

		} catch (FrameworkException fex) {

			final List<ErrorToken> tokens = fex.getErrorBuffer().getErrorTokens();
			final ErrorToken token = tokens.get(0);

			assertEquals("Invalid SchemaRelationshipNode validation result", 1, tokens.size());

			assertEquals("Invalid SchemaRelationshipNode validation result", 422, fex.getStatus());
			assertEquals("Invalid SchemaRelationshipNode validation result", "SchemaRelationshipNode", token.getType());
			assertEquals("Invalid SchemaRelationshipNode validation result", "targetNode", token.getProperty());
			assertEquals("Invalid SchemaRelationshipNode validation result", "must_not_be_empty", token.getToken());
		}


		try (final Tx tx = app.tx()) {

			app.create(SchemaRelationshipNode.class,
				new NodeAttribute<>(SchemaRelationshipNode.relationshipType, "test"),
				new NodeAttribute<>(SchemaRelationshipNode.sourceNode, null),
				new NodeAttribute<>(SchemaRelationshipNode.targetNode, app.nodeQuery(SchemaNode.class).andName("Group").getFirst())
			);

			tx.success();

			fail("SchemaRelationshipNode constraint violation, source node must not be null.");

		} catch (FrameworkException fex) {

			final List<ErrorToken> tokens = fex.getErrorBuffer().getErrorTokens();
			final ErrorToken token = tokens.get(0);

			assertEquals("Invalid SchemaRelationshipNode validation result", 1, tokens.size());

			assertEquals("Invalid SchemaRelationshipNode validation result", 422, fex.getStatus());
			assertEquals("Invalid SchemaRelationshipNode validation result", "SchemaRelationshipNode", token.getType());
			assertEquals("Invalid SchemaRelationshipNode validation result", "sourceNode", token.getProperty());
			assertEquals("Invalid SchemaRelationshipNode validation result", "must_not_be_empty", token.getToken());
		}
	}

	@Test
	public void testCompoundUniqueness() {

		this.cleanDatabaseAndSchema();

		try (final Tx tx = app.tx()) {

			app.create(SchemaNode.class,
				new NodeAttribute<>(AbstractNode.name, "TestType"),
				new NodeAttribute<>(new StringProperty("_key1"), "String!!"),
				new NodeAttribute<>(new StringProperty("_key2"), "String!!"),
				new NodeAttribute<>(new StringProperty("_key3"), "String!!")
			);

			tx.success();

		} catch (FrameworkException fex) {
			fex.printStackTrace();
		}

		final Class type         = StructrApp.getConfiguration().getNodeEntityClass("TestType");
		final PropertyKey key1   = StructrApp.key(type, "key1");
		final PropertyKey key2   = StructrApp.key(type, "key2");
		final PropertyKey key3   = StructrApp.key(type, "key3");
		final PropertyKey[] keys = new PropertyKey[] { key1, key2, key3 };

		// test success
		try (final Tx tx = app.tx()) {

			app.create(type,
				new NodeAttribute<>(key1, "one"),
				new NodeAttribute<>(key2, "two"),
				new NodeAttribute<>(key3, "three")
			);

			app.create(type,
				new NodeAttribute<>(key1, "one"),
				new NodeAttribute<>(key2, "one"),
				new NodeAttribute<>(key3, "three")
			);

			tx.success();

		} catch (FrameworkException fex) {
			fail("Invalid compound indexing validation result.");
		}

		// test success
		try (final Tx tx = app.tx()) {

			app.create(type,
				new NodeAttribute<>(key1, "one"),
				new NodeAttribute<>(key3, "three")
			);

			app.create(type,
				new NodeAttribute<>(key1, "one"),
				new NodeAttribute<>(key3, "four")
			);

			tx.success();

		} catch (FrameworkException fex) {
			fail("Invalid compound indexing validation result.");
		}

		String uuid = null;

		// test failure
		try (final Tx tx = app.tx()) {

			uuid = app.create(type,
				new NodeAttribute<>(key1, "one"),
				new NodeAttribute<>(key2, "two"),
				new NodeAttribute<>(key3, "three")
			).getUuid();

			app.create(type,
				new NodeAttribute<>(key1, "one"),
				new NodeAttribute<>(key2, "two"),
				new NodeAttribute<>(key3, "three")
			);

			tx.success();

			fail("Invalid compound indexing validation result.");

		} catch (FrameworkException fex) {

			System.out.println(fex.toJSON());

			final ErrorToken token = fex.getErrorBuffer().getErrorTokens().get(0);

			assertEquals("Invalid validation status code", fex.getStatus(), 422);
			assertEquals("Invalid validation error token", "already_taken", token.getToken());
			assertEquals("Invalid validation error type", "TestType",       token.getType());
			assertEquals("Invalid validation error type", uuid,             token.getDetail());
			assertTrue("Invalid validation error type", Arrays.equals(keys, (PropertyKey[])token.getValue()));

		}
	}

	// ----- private methods -----
	private void checkRangeSuccess(final Class<NodeInterface> type, final PropertyKey key, final Object value) {

		try (final Tx tx = app.tx()) {

			app.create(type, new NodeAttribute<>(key, value));
			tx.success();

		} catch (FrameworkException fex) {
			fail("Property range constraint validation failure!");
		}
	}

	private void checkRangeError(final Class<NodeInterface> type, final PropertyKey key, final Object value) throws FrameworkException {

		try (final Tx tx = app.tx()) {

			app.create(type, new NodeAttribute<>(key, value));
			tx.success();

			fail("Property range constraint violated!");

		} catch (FrameworkException fex) {
			checkException(fex, 1, 422, "Test", "range1", "must_be_in_range");
		}
	}

	private void checkException(final FrameworkException fex, final int numberOfTokens, final int statusCode, final String typeName, final String keyName, final String errorToken) {
		checkException(fex, numberOfTokens, statusCode, typeName, keyName, errorToken, null);
	}

	private void checkException(final FrameworkException fex, final int numberOfTokens, final int statusCode, final String typeName, final String keyName, final String errorToken, final String uuid) {

		final List<ErrorToken> tokens = fex.getErrorBuffer().getErrorTokens();
		final ErrorToken token        = tokens.get(0);

		assertEquals("Invalid validation result", numberOfTokens, tokens.size());
		assertEquals("Invalid validation result", statusCode,     fex.getStatus());
		assertEquals("Invalid validation result", keyName,        token.getProperty());
		assertEquals("Invalid validation result", typeName,       token.getType());
		assertEquals("Invalid validation result", errorToken,     token.getToken());

		if (uuid != null) {

			assertEquals("Invalid validation result", uuid, token.getDetail());
		}

	}

	private Class createTypeWithProperty(final String typeName, final String keyName, final String keyType) {

		try (final Tx tx = app.tx()) {

			app.create(SchemaNode.class,
				new NodeAttribute<>(AbstractNode.name, typeName),
				new NodeAttribute<>(new StringProperty("_" + keyName), keyType)
			);

			tx.success();

		} catch (FrameworkException fex) {
			logger.warn("", fex);
			fail("Unexpected exception.");
		}

		return StructrApp.getConfiguration().getNodeEntityClass(typeName);
	}

	private void removeInstances(final Class<NodeInterface> type) {

		// clear database
		try (final Tx tx = app.tx()) {

			for (final NodeInterface node : app.nodeQuery(type).getAsList()) {
				app.delete(node);
			}
			tx.success();

		} catch (FrameworkException fex) {
			fail("Unexpected exception.");
		}
	}
}
