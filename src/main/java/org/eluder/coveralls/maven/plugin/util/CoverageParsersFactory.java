/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 - 2023, Tapio Rautonen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.eluder.coveralls.maven.plugin.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.maven.project.MavenProject;
import org.eluder.coveralls.maven.plugin.CoverageParser;
import org.eluder.coveralls.maven.plugin.parser.CloverParser;
import org.eluder.coveralls.maven.plugin.parser.CoberturaParser;
import org.eluder.coveralls.maven.plugin.parser.JaCoCoParser;
import org.eluder.coveralls.maven.plugin.parser.SagaParser;
import org.eluder.coveralls.maven.plugin.source.SourceLoader;

/**
 * A factory for creating CoverageParsers objects.
 */
public class CoverageParsersFactory {

    /** The Constant JACOCO_FILE. */
    private static final String JACOCO_FILE = "jacoco.xml";

    /** The Constant JACOCO_DIRECTORY. */
    private static final String JACOCO_DIRECTORY = "jacoco";

    /** The Constant JACOCO_IT_DIRECTORY. */
    private static final String JACOCO_IT_DIRECTORY = "jacoco-it";

    /** The Constant COBERTURA_FILE. */
    private static final String COBERTURA_FILE = "coverage.xml";

    /** The Constant COBERTURA_DIRECTORY. */
    private static final String COBERTURA_DIRECTORY = "cobertura";

    /** The Constant CLOVER_FILE. */
    private static final String CLOVER_FILE = "clover.xml";

    /** The Constant CLOVER_DIRECTORY. */
    private static final String CLOVER_DIRECTORY = "clover";

    /** The Constant SAGA_FILE. */
    private static final String SAGA_FILE = "total-coverage.xml";

    /** The Constant SAGA_DIRECTORY. */
    private static final String SAGA_DIRECTORY = "saga-coverage";

    /** The project. */
    private final MavenProject project;

    /** The source loader. */
    private final SourceLoader sourceLoader;

    /** The jacoco reports. */
    private List<File> jacocoReports;

    /** The cobertura reports. */
    private List<File> coberturaReports;

    /** The saga reports. */
    private List<File> sagaReports;

    /** The clover reports. */
    private List<File> cloverReports;

    /** The relative report dirs. */
    private List<String> relativeReportDirs;

    /**
     * Instantiates a new coverage parsers factory.
     *
     * @param project
     *            the project
     * @param sourceLoader
     *            the source loader
     */
    public CoverageParsersFactory(final MavenProject project, final SourceLoader sourceLoader) {
        this.project = project;
        this.sourceLoader = sourceLoader;
    }

    /**
     * With jacoco reports.
     *
     * @param jacocoReports
     *            the jacoco reports
     *
     * @return the coverage parsers factory
     */
    public CoverageParsersFactory withJaCoCoReports(final List<File> jacocoReports) {
        this.jacocoReports = jacocoReports;
        return this;
    }

    /**
     * With cobertura reports.
     *
     * @param coberturaReports
     *            the cobertura reports
     *
     * @return the coverage parsers factory
     */
    public CoverageParsersFactory withCoberturaReports(final List<File> coberturaReports) {
        this.coberturaReports = coberturaReports;
        return this;
    }

    /**
     * With saga reports.
     *
     * @param sagaReports
     *            the saga reports
     *
     * @return the coverage parsers factory
     */
    public CoverageParsersFactory withSagaReports(final List<File> sagaReports) {
        this.sagaReports = sagaReports;
        return this;
    }

    /**
     * With clover reports.
     *
     * @param cloverReports
     *            the clover reports
     *
     * @return the coverage parsers factory
     */
    public CoverageParsersFactory withCloverReports(final List<File> cloverReports) {
        this.cloverReports = cloverReports;
        return this;
    }

    /**
     * With relative report dirs.
     *
     * @param relativeReportDirs
     *            the relative report dirs
     *
     * @return the coverage parsers factory
     */
    public CoverageParsersFactory withRelativeReportDirs(final List<String> relativeReportDirs) {
        this.relativeReportDirs = relativeReportDirs;
        return this;
    }

    /**
     * Creates a new CoverageParsers object.
     *
     * @return the list of coverage parsers
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public List<CoverageParser> createParsers() throws IOException {
        List<CoverageParser> parsers = new ArrayList<>();
        var projects = new MavenProjectCollector(project).collect();

        var jacocoFiles = ExistingFiles.create(jacocoReports);
        var coberturaFiles = ExistingFiles.create(coberturaReports);
        var sagaFiles = ExistingFiles.create(sagaReports);
        var cloverFiles = ExistingFiles.create(cloverReports);
        for (MavenProject p : projects) {
            var reportingDirectory = Path.of(p.getModel().getReporting().getOutputDirectory());
            var buildDirectory = Path.of(p.getBuild().getDirectory());

            jacocoFiles.add(reportingDirectory.resolve(JACOCO_DIRECTORY).resolve(JACOCO_FILE).toFile());
            jacocoFiles.add(reportingDirectory.resolve(JACOCO_IT_DIRECTORY).resolve(JACOCO_FILE).toFile());
            coberturaFiles.add(reportingDirectory.resolve(COBERTURA_DIRECTORY).resolve(COBERTURA_FILE).toFile());
            sagaFiles.add(buildDirectory.resolve(SAGA_DIRECTORY).resolve(SAGA_FILE).toFile());
            cloverFiles.add(reportingDirectory.resolve(CLOVER_DIRECTORY).resolve(CLOVER_FILE).toFile());
            cloverFiles.add(buildDirectory.resolve(CLOVER_DIRECTORY).resolve(CLOVER_FILE).toFile());

            if (relativeReportDirs != null) {
                for (String relativeReportPath : relativeReportDirs) {
                    var relativeReportingDirectory = reportingDirectory;
                    var relativeBuildDirectory = buildDirectory;
                    if (!relativeReportPath.isEmpty() && !File.separator.equals(relativeReportPath)) {
                        relativeReportingDirectory = reportingDirectory.resolve(relativeReportPath);
                        relativeBuildDirectory = buildDirectory.resolve(relativeReportPath);
                    }

                    jacocoFiles.add(relativeReportingDirectory.resolve(JACOCO_FILE).toFile());
                    jacocoFiles.add(relativeBuildDirectory.resolve(JACOCO_FILE).toFile());
                    coberturaFiles.add(relativeReportingDirectory.resolve(COBERTURA_FILE).toFile());
                    coberturaFiles.add(relativeBuildDirectory.resolve(COBERTURA_FILE).toFile());
                    sagaFiles.add(relativeReportingDirectory.resolve(SAGA_FILE).toFile());
                    sagaFiles.add(relativeBuildDirectory.resolve(SAGA_FILE).toFile());
                    cloverFiles.add(relativeReportingDirectory.resolve(CLOVER_FILE).toFile());
                    cloverFiles.add(relativeBuildDirectory.resolve(CLOVER_FILE).toFile());
                }
            }
        }

        // Use ExistingFiles.toParsers to create parser instances
        parsers.addAll(jacocoFiles.toParsers(file -> new JaCoCoParser(file, sourceLoader)));
        parsers.addAll(coberturaFiles.toParsers(file -> new CoberturaParser(file, sourceLoader)));
        parsers.addAll(sagaFiles.toParsers(file -> new SagaParser(file, sourceLoader)));
        parsers.addAll(cloverFiles.toParsers(file -> new CloverParser(file, sourceLoader)));

        if (parsers.isEmpty()) {
            throw new IOException("No coverage report files found");
        }

        return Collections.unmodifiableList(parsers);
    }

}
