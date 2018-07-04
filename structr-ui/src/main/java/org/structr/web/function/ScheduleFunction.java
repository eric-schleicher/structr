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
package org.structr.web.function;

import java.util.Collections;
import org.structr.common.error.FrameworkException;
import org.structr.core.scheduler.JobQueueManager;
import org.structr.schema.action.ActionContext;
import org.structr.web.importer.ScriptJob;

public class ScheduleFunction extends UiFunction {

	public static final String ERROR_MESSAGE_SCHEDULE    = "Usage: ${schedule(script[, title])}. Example: ${schedule(\"delete(find('User'))\", \"Delete all users!\")}";
	public static final String ERROR_MESSAGE_SCHEDULE_JS = "Usage: ${{Structr.schedule(script[, title])}}. Example: ${{Structr.schedule(function() {}, 'This is a no-op!')}}";

	@Override
	public String getName() {
		return "schedule()";
	}

	@Override
	public Object apply(final ActionContext ctx, final Object caller, final Object[] sources) {

		try {
			assertArrayHasMinLengthAndMaxLengthAndAllElementsNotNull(sources, 1, 2);

			final String jobName = (sources.length == 2) ? sources[1].toString() : "Untitled script job";

			final ScriptJob job = new ScriptJob(ctx.getSecurityContext().getCachedUser(), Collections.EMPTY_MAP, sources[0], ctx.getSecurityContext().getContextStore(), jobName);

			try {

				JobQueueManager.getInstance().addJob(job);

			} catch (FrameworkException ex) {
				logException(ex, ex.getMessage(), null);
			}

			return "";

		} catch (IllegalArgumentException e) {

			logParameterError(caller, sources, e.getMessage(), ctx.isJavaScriptContext());
			return null;
		}
	}

	@Override
	public String usage(boolean inJavaScriptContext) {
		return (inJavaScriptContext ? ERROR_MESSAGE_SCHEDULE_JS : ERROR_MESSAGE_SCHEDULE);
	}

	@Override
	public String shortDescription() {
		return "Schedules a script or a function to be executed in a separate thread.";
	}
}
