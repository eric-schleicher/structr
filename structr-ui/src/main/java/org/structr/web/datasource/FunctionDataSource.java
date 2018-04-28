/**
 * Copyright (C) 2010-2018 Structr GmbH
 *
 * This file is part of Structr <http://structr.org>.
 *
 * Structr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Structr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Structr.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.structr.web.datasource;

import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.structr.api.util.Iterables;
import org.structr.common.error.FrameworkException;
import org.structr.common.error.UnlicensedScriptException;
import org.structr.core.GraphObject;
import org.structr.core.script.Scripting;
import org.structr.schema.action.Function;
import org.structr.web.common.GraphDataSource;
import org.structr.web.common.RenderContext;
import org.structr.web.entity.dom.DOMNode;
import org.structr.web.function.UiFunction;

/**
 *
 *
 */
public class FunctionDataSource implements GraphDataSource<Iterable<GraphObject>> {

	@Override
	public Iterable<GraphObject> getData(final RenderContext renderContext, final DOMNode referenceNode) throws FrameworkException {

		final String functionQuery = referenceNode.getFunctionQuery();
		if (StringUtils.isBlank(functionQuery)) {

			return null;
		}

		try {

			final Object result = Scripting.evaluate(renderContext, referenceNode, "${" + functionQuery + "}", "function query");
			if (result instanceof Iterable) {

				return FunctionDataSource.map((Iterable)result);

			} else if (result instanceof Object[]) {

				return (List<GraphObject>) UiFunction.toGraphObject(result, 1);
			}

		} catch (UnlicensedScriptException ex) {
			ex.log(LoggerFactory.getLogger(FunctionDataSource.class));
		}

		return null;
	}

	// ----- public static methods -----
	public static Iterable<GraphObject> map(final Iterable<Object> src) {

		return Iterables.map((Object t) -> {

			if (t instanceof GraphObject) {
				return (GraphObject)t;
			}

			if (t instanceof Map) {

				return Function.toGraphObjectMap((Map)t);
			}

			throw new ClassCastException(t.getClass() + " cannot be cast to " + GraphObject.class.getName());

		}, src);

	}
}
