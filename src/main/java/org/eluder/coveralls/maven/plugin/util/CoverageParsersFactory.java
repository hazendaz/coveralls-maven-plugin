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
    private static final String JACOCO_FILE = "/jacoco.xml";

    /** The Constant JACOCO_PREFIX. */
    private static final String JACOCO_PREFIX = "/jacoco";

    /** The Constant JACOCO_IT_PREFIX. */
    private static final String JACOCO_IT_PREFIX = "/jacoco-it";

    /** The Constant COBERTURA_FILE. */
    private static final String COBERTURA_FILE = "/coverage.xml";

    /** The Constant COBERTURA_PREFIX. */
    private static final String COBERTURA_PREFIX = "/cobertura";

    /** The Constant CLOVER_FILE. */
    private static final String CLOVER_FILE = "/clover.xml";

    /** The Constant CLOVER_PREFIX. */
    private static final String CLOVER_PREFIX = "/clover";

    /** The Constant SAGA_FILE. */
    private static final String SAGA_FILE = "/total-coverage.xml";

    /** The Constant SAGA_PREFIX. */
    private static final String SAGA_PREFIX = "/saga-coverage";

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
        List<MavenProject> projects = new MavenProjectCollector(project).collect();

        ExistingFiles jacocoFiles = ExistingFiles.create(jacocoReports);
        ExistingFiles coberturaFiles = ExistingFiles.create(coberturaReports);
        ExistingFiles sagaFiles = ExistingFiles.create(sagaReports);
        ExistingFiles cloverFiles = ExistingFiles.create(cloverReports);
        for (MavenProject p : projects) {
            Path reportingDirectory = Path.of(p.getModel().getReporting().getOutputDirectory());
            Path buildDirectory = Path.of(p.getBuild().getDirectory());

            jacocoFiles.add(Path.of(reportingDirectory.toString(), JACOCO_PREFIX + JACOCO_FILE).toFile());
            jacocoFiles.add(Path.of(reportingDirectory.toString(), JACOCO_IT_PREFIX + JACOCO_FILE).toFile());
            coberturaFiles.add(Path.of(reportingDirectory.toString(), COBERTURA_PREFIX + COBERTURA_FILE).toFile());
            sagaFiles.add(Path.of(buildDirectory.toString(), SAGA_PREFIX + SAGA_FILE).toFile());
            cloverFiles.add(Path.of(reportingDirectory.toString(), CLOVER_PREFIX + CLOVER_FILE).toFile());

            if (relativeReportDirs != null) {
                for (String relativeReportPath : relativeReportDirs) {
                    Path relativeReportingDirectory = reportingDirectory;
                    Path relativeBuildDirectory = buildDirectory;
                    if (!relativeReportPath.isEmpty() && !File.separator.equals(relativeReportPath)) {
                        relativeReportingDirectory = Path.of(reportingDirectory.toString(), relativeReportPath);
                        relativeBuildDirectory = Path.of(buildDirectory.toString(), relativeReportPath);
                    }

                    jacocoFiles.add(Path.of(relativeReportingDirectory.toString(), JACOCO_FILE).toFile());
                    jacocoFiles.add(Path.of(relativeBuildDirectory.toString(), JACOCO_FILE).toFile());
                    coberturaFiles.add(Path.of(relativeReportingDirectory.toString(), COBERTURA_FILE).toFile());
                    coberturaFiles.add(Path.of(relativeBuildDirectory.toString(), COBERTURA_FILE).toFile());
                    sagaFiles.add(Path.of(relativeReportingDirectory.toString(), SAGA_FILE).toFile());
                    sagaFiles.add(Path.of(relativeBuildDirectory.toString(), SAGA_FILE).toFile());
                    cloverFiles.add(Path.of(relativeReportingDirectory.toString(), CLOVER_FILE).toFile());
                    cloverFiles.add(Path.of(relativeBuildDirectory.toString(), CLOVER_FILE).toFile());
                }
            }
        }

        for (File jacocoFile : jacocoFiles) {
            parsers.add(new JaCoCoParser(jacocoFile, sourceLoader));
        }
        for (File coberturaFile : coberturaFiles) {
            parsers.add(new CoberturaParser(coberturaFile, sourceLoader));
        }
        for (File sagaFile : sagaFiles) {
            parsers.add(new SagaParser(sagaFile, sourceLoader));
        }
        for (File cloverFile : cloverFiles) {
            parsers.add(new CloverParser(cloverFile, sourceLoader));
        }

        if (parsers.isEmpty()) {
            throw new IOException("No coverage report files found");
        }

        return Collections.unmodifiableList(parsers);
    }
}
