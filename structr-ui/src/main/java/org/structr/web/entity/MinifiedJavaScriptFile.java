/**
 * Copyright (C) 2010-2016 Structr GmbH
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
package org.structr.web.entity;

import com.google.javascript.jscomp.BasicErrorManager;
import com.google.javascript.jscomp.CheckLevel;
import com.google.javascript.jscomp.CommandLineRunner;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JSError;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.parsing.parser.util.format.SimpleFormat;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.structr.common.PropertyView;
import org.structr.common.View;
import org.structr.common.error.FrameworkException;
import org.structr.core.property.EnumProperty;
import org.structr.core.property.Property;
import org.structr.core.property.StringProperty;
import org.structr.web.common.FileHelper;
import org.structr.web.entity.relation.MinificationNeighbor;

public class MinifiedJavaScriptFile extends AbstractMinifiedFile {

	private static final Logger logger = Logger.getLogger(MinifiedJavaScriptFile.class.getName());

	public static final Property<CompilationLevel> optimizationLevel = new EnumProperty<>("optimizationLevel", CompilationLevel.class, CompilationLevel.WHITESPACE_ONLY);
	public static final Property<String> warnings                    = new StringProperty("warnings");
	public static final Property<String> errors                      = new StringProperty("errors");

	public static final View defaultView = new View(MinifiedJavaScriptFile.class, PropertyView.Public, minificationSources, optimizationLevel, warnings, errors);
	public static final View uiView      = new View(MinifiedJavaScriptFile.class, PropertyView.Ui, minificationSources, optimizationLevel, warnings, errors);

	@Override
	public void minify() throws FrameworkException, IOException {

		final Compiler compiler = new Compiler();
		final CompilerOptions options = new CompilerOptions();
		final CompilationLevel selectedLevel = getProperty(optimizationLevel);
		selectedLevel.setOptionsForCompilationLevel(options);

		compiler.setErrorManager(new BasicErrorManager() {
			@Override
			public void println(CheckLevel level, JSError error) {
//				if (level != CheckLevel.OFF) {
//					logger.log((level == CheckLevel.ERROR) ? Level.SEVERE : Level.WARNING, error.toString());
//				}
			}

			@Override
			protected void printSummary() {
				final Level level = (getErrorCount() + getWarningCount() == 0) ? Level.INFO : Level.WARNING;
				if (getTypedPercent() > 0) {
					logger.log(level, SimpleFormat.format("%d error(s), %d warning(s), %.1f%% typed", getErrorCount(), getWarningCount(), getTypedPercent()));
				} else if (getErrorCount() + getWarningCount() > 0) {
					logger.log(level, SimpleFormat.format("%d error(s), %d warning(s)", getErrorCount(), getWarningCount()));
				}
			}
		});
		compiler.compile(CommandLineRunner.getBuiltinExterns(options), getSourceFileList(), options);

		FileHelper.setFileData(this, compiler.toSource().getBytes(), null);

		final String separator = System.lineSeparator().concat("----").concat(System.lineSeparator());

		setProperty(warnings, StringUtils.join(compiler.getWarnings(), separator));
		setProperty(errors, StringUtils.join(compiler.getErrors(), separator));
	}

	private ArrayList<SourceFile> getSourceFileList() throws FrameworkException, IOException {

		ArrayList<SourceFile> sourceList = new ArrayList();

		int cnt = 0;
		for (MinificationNeighbor rel : getSortedRelationships()) {

			final FileBase src = rel.getTargetNode();

			sourceList.add(SourceFile.fromCode(src.getProperty(FileBase.name), FileUtils.readFileToString(src.getFileOnDisk())));

			// compact the relationships (if necessary)
			if (rel.getProperty(MinificationNeighbor.position) != cnt) {
				rel.setProperty(MinificationNeighbor.position, cnt);
			}
			cnt++;
		}

		return sourceList;
	}
}