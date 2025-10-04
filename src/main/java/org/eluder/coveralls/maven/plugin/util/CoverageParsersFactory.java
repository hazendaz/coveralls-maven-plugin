/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013-2025 The Coveralls Maven Plugin Project Contributors:
 *     https://github.com/hazendaz/coveralls-maven-plugin/graphs/contributors
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

    /** The Constant DEFAULT_JACOCO_DIRECTORY. */
    static final String DEFAULT_JACOCO_DIRECTORY = "jacoco";

    /** The Constant DEFAULT_JACOCO_IT_DIRECTORY. */
    static final String DEFAULT_JACOCO_IT_DIRECTORY = "jacoco-it";

    /** The Constant DEFAULT_JACOCO_MERGED_DIRECTORY. */
    static final String DEFAULT_JACOCO_MERGED_DIRECTORY = "jacoco-merged-report";

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

    /**
     * This new property can be used in a Maven Multi-Module project that has a JaCoCo aggregate project
     *
     * @since 5.0.0
     */
    private File jacocoAggregateReport;

    /**
     * The jacoco reports option is used to add additional paths. By default the plugin already looks in standard locations
     */
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
     * With JaCoCo aggregate report
     * <p>
     * This new property is for Maven multi-module projects
     * </p>
     *
     * @param jacocoAggregateReport
     *            A single JaCoCo report file in an aggregated report
     *
     * @return the coverage parsers factory
     *
     * @since 5.0.0
     */
    public CoverageParsersFactory withJacocoAggregateReport(final File jacocoAggregateReport) {
        this.jacocoAggregateReport = jacocoAggregateReport;
        return this;
    }

    /**
     * With jacoco reports.
     * <p>
     * For Maven multi-module projects, configure an aggregate project and use
     * {@link CoverageParsersFactory#withJacocoAggregateReport}
     * </p>
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
        final List<CoverageParser> parsers = new ArrayList<>();
        final var projects = new MavenProjectCollector(this.project).collect();

        final var jacocoFiles = this.jacocoAggregateReport != null
                ? ExistingFiles.create(List.of(this.jacocoAggregateReport))
                : ExistingFiles.create(this.jacocoReports);
        final var coberturaFiles = ExistingFiles.create(this.coberturaReports);
        final var sagaFiles = ExistingFiles.create(this.sagaReports);
        final var cloverFiles = ExistingFiles.create(this.cloverReports);
        for (final MavenProject p : projects) {
            final var reportingDirectory = Path.of(p.getModel().getReporting().getOutputDirectory());
            final var buildDirectory = Path.of(p.getBuild().getDirectory());

            final File jacocoMergedReport = reportingDirectory
                    .resolve(CoverageParsersFactory.DEFAULT_JACOCO_MERGED_DIRECTORY)
                    .resolve(CoverageParsersFactory.JACOCO_FILE).toFile();

            // If a JaCoCo merged report exists there is no need to individually add reports for unit tests and IT.
            // Note that in a Maven multi-module project JaCoCo can also be configured to aggregate all reports to a
            // single module. In which case there is no need to gather reports from individual Maven projects
            // as it's already done. Therefore, we only need to add to jacocoFiles if jacocoAggregateReport is null.
            if (this.jacocoAggregateReport == null) {
                if (jacocoMergedReport.exists() && jacocoMergedReport.canRead()) {
                    jacocoFiles.add(jacocoMergedReport);
                } else {
                    jacocoFiles.add(reportingDirectory.resolve(CoverageParsersFactory.DEFAULT_JACOCO_DIRECTORY)
                            .resolve(CoverageParsersFactory.JACOCO_FILE).toFile());
                    jacocoFiles.add(reportingDirectory.resolve(CoverageParsersFactory.DEFAULT_JACOCO_IT_DIRECTORY)
                            .resolve(CoverageParsersFactory.JACOCO_FILE).toFile());
                }
            }

            coberturaFiles.add(reportingDirectory.resolve(CoverageParsersFactory.COBERTURA_DIRECTORY)
                    .resolve(CoverageParsersFactory.COBERTURA_FILE).toFile());
            sagaFiles.add(buildDirectory.resolve(CoverageParsersFactory.SAGA_DIRECTORY)
                    .resolve(CoverageParsersFactory.SAGA_FILE).toFile());
            cloverFiles.add(reportingDirectory.resolve(CoverageParsersFactory.CLOVER_DIRECTORY)
                    .resolve(CoverageParsersFactory.CLOVER_FILE).toFile());
            cloverFiles.add(buildDirectory.resolve(CoverageParsersFactory.CLOVER_DIRECTORY)
                    .resolve(CoverageParsersFactory.CLOVER_FILE).toFile());

            if (this.relativeReportDirs != null) {
                for (final String relativeReportPath : this.relativeReportDirs) {
                    var relativeReportingDirectory = reportingDirectory;
                    var relativeBuildDirectory = buildDirectory;
                    if (!relativeReportPath.isEmpty() && !File.separator.equals(relativeReportPath)) {
                        relativeReportingDirectory = reportingDirectory.resolve(relativeReportPath);
                        relativeBuildDirectory = buildDirectory.resolve(relativeReportPath);
                    }

                    jacocoFiles.add(relativeReportingDirectory.resolve(CoverageParsersFactory.JACOCO_FILE).toFile());
                    jacocoFiles.add(relativeBuildDirectory.resolve(CoverageParsersFactory.JACOCO_FILE).toFile());
                    coberturaFiles
                            .add(relativeReportingDirectory.resolve(CoverageParsersFactory.COBERTURA_FILE).toFile());
                    coberturaFiles.add(relativeBuildDirectory.resolve(CoverageParsersFactory.COBERTURA_FILE).toFile());
                    sagaFiles.add(relativeReportingDirectory.resolve(CoverageParsersFactory.SAGA_FILE).toFile());
                    sagaFiles.add(relativeBuildDirectory.resolve(CoverageParsersFactory.SAGA_FILE).toFile());
                    cloverFiles.add(relativeReportingDirectory.resolve(CoverageParsersFactory.CLOVER_FILE).toFile());
                    cloverFiles.add(relativeBuildDirectory.resolve(CoverageParsersFactory.CLOVER_FILE).toFile());
                }
            }
        }

        // Use ExistingFiles.toParsers to create parser instances
        parsers.addAll(jacocoFiles.toParsers(file -> new JaCoCoParser(file, this.sourceLoader)));
        parsers.addAll(coberturaFiles.toParsers(file -> new CoberturaParser(file, this.sourceLoader)));
        parsers.addAll(sagaFiles.toParsers(file -> new SagaParser(file, this.sourceLoader)));
        parsers.addAll(cloverFiles.toParsers(file -> new CloverParser(file, this.sourceLoader)));

        if (parsers.isEmpty()) {
            throw new IOException("No coverage report files found");
        }

        return Collections.unmodifiableList(parsers);
    }

}
