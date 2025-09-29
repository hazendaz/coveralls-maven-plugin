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
package org.eluder.coveralls.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;
import org.eluder.coveralls.maven.plugin.domain.GitRepository;
import org.eluder.coveralls.maven.plugin.domain.Job;
import org.eluder.coveralls.maven.plugin.httpclient.CoverallsClient;
import org.eluder.coveralls.maven.plugin.httpclient.CoverallsProxyClient;
import org.eluder.coveralls.maven.plugin.json.JsonWriter;
import org.eluder.coveralls.maven.plugin.logging.CoverageTracingLogger;
import org.eluder.coveralls.maven.plugin.logging.DryRunLogger;
import org.eluder.coveralls.maven.plugin.logging.JobLogger;
import org.eluder.coveralls.maven.plugin.logging.Logger;
import org.eluder.coveralls.maven.plugin.logging.Logger.Position;
import org.eluder.coveralls.maven.plugin.service.Appveyor;
import org.eluder.coveralls.maven.plugin.service.Bamboo;
import org.eluder.coveralls.maven.plugin.service.Circle;
import org.eluder.coveralls.maven.plugin.service.General;
import org.eluder.coveralls.maven.plugin.service.GitHub;
import org.eluder.coveralls.maven.plugin.service.Jenkins;
import org.eluder.coveralls.maven.plugin.service.ServiceSetup;
import org.eluder.coveralls.maven.plugin.service.Shippable;
import org.eluder.coveralls.maven.plugin.service.Travis;
import org.eluder.coveralls.maven.plugin.service.Wercker;
import org.eluder.coveralls.maven.plugin.source.SourceCallback;
import org.eluder.coveralls.maven.plugin.source.SourceLoader;
import org.eluder.coveralls.maven.plugin.source.UniqueSourceCallback;
import org.eluder.coveralls.maven.plugin.util.CoverageParsersFactory;
import org.eluder.coveralls.maven.plugin.util.SourceLoaderFactory;
import org.eluder.coveralls.maven.plugin.util.TimestampParser;

/**
 * The Class CoverallsReportMojo.
 */
@Mojo(name = "report", threadSafe = false, aggregator = true)
public class CoverallsReportMojo extends AbstractMojo {

    /**
     * File paths to additional JaCoCo coverage report files.
     */
    @Parameter(property = "jacocoReports")
    private List<File> jacocoReports;

    /**
     * File paths to additional Cobertura coverage report files.
     */
    @Parameter(property = "coberturaReports")
    private List<File> coberturaReports;

    /**
     * File paths to additional Saga coverage report files.
     */
    @Parameter(property = "sagaReports")
    private List<File> sagaReports;

    /**
     * File paths to additional Clover coverage report files.
     */
    @Parameter(property = "cloverReports")
    private List<File> cloverReports;

    /**
     * Directories for relative per module specific report files.
     */
    @Parameter(property = "relativeReportDirs")
    private List<String> relativeReportDirs;

    /**
     * File path to write and submit Coveralls data.
     */
    @Parameter(property = "coverallsFile", defaultValue = "${project.build.directory}/coveralls.json")
    File coverallsFile;

    /**
     * Url for the Coveralls API.
     */
    @Parameter(property = "coverallsUrl", defaultValue = "https://coveralls.io/api/v1/jobs")
    private String coverallsUrl;

    /**
     * Source directories.
     */
    @Parameter(property = "sourceDirectories")
    List<File> sourceDirectories;

    /**
     * Source file encoding.
     */
    @Parameter(property = "sourceEncoding", defaultValue = "${project.build.sourceEncoding}")
    String sourceEncoding;

    /**
     * CI service name.
     */
    @Parameter(property = "serviceName")
    String serviceName;

    /**
     * CI service job id.
     */
    @Parameter(property = "serviceJobId")
    String serviceJobId;

    /**
     * CI service build number.
     */
    @Parameter(property = "serviceBuildNumber")
    String serviceBuildNumber;

    /**
     * CI service build url.
     */
    @Parameter(property = "serviceBuildUrl")
    String serviceBuildUrl;

    /**
     * CI service specific environment properties.
     */
    @Parameter(property = "serviceEnvironment")
    Properties serviceEnvironment;

    /**
     * Coveralls repository token.
     */
    @Parameter(property = "repoToken")
    String repoToken;

    /**
     * Git branch name.
     */
    @Parameter(property = "branch")
    String branch;

    /**
     * GitHub pull request identifier.
     */
    @Parameter(property = "pullRequest")
    String pullRequest;

    /**
     * Coveralls parallel flag.
     */
    @Parameter(property = "parallel")
    private boolean parallel;

    /**
     * Build timestamp format. Must be in format supported by DateTimeFormatter.
     */
    @Parameter(property = "timestampFormat", defaultValue = "${maven.build.timestamp.format}")
    private String timestampFormat;

    /**
     * Build timestamp. Must be in format defined by 'timestampFormat' if it's available or in default timestamp format
     * yyyy-MM-dd'T'HH:mm:ss'Z'.
     */
    @Parameter(property = "timestamp", defaultValue = "${maven.build.timestamp}")
    private String timestamp;

    /**
     * Dry run Coveralls report without actually sending it.
     */
    @Parameter(property = "dryRun", defaultValue = "false")
    boolean dryRun;

    /**
     * Fail build if Coveralls service is not available or submission fails for internal errors.
     */
    @Parameter(property = "failOnServiceError", defaultValue = "true")
    boolean failOnServiceError;

    /**
     * Scan subdirectories for source files.
     */
    @Parameter(property = "scanForSources", defaultValue = "false")
    private boolean scanForSources;

    /**
     * Base directory of the project.
     */
    @Parameter(property = "coveralls.basedir", defaultValue = "${project.basedir}")
    File basedir;

    /**
     * Skip the plugin execution.
     */
    @Parameter(property = "coveralls.skip", defaultValue = "false")
    boolean skip;

    /**
     * Maven settings.
     */
    @Parameter(defaultValue = "${settings}", readonly = true, required = true)
    Settings settings;

    /**
     * Maven project for runtime value resolution.
     */
    @Parameter(defaultValue = "${project}", readonly = true)
    MavenProject project;

    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {
        if (this.skip) {
            this.getLog().info("Skip property set, skipping plugin execution");
            return;
        }

        try {
            this.createEnvironment().setup();

            final var job = this.createJob();
            job.validate().throwOrInform(this.getLog());

            final var sourceLoader = this.createSourceLoader(job);

            final var parsers = this.createCoverageParsers(sourceLoader);

            final var client = this.createCoverallsClient();

            final List<Logger> reporters = new ArrayList<>();
            reporters.add(new JobLogger(job));

            try (var writer = this.createJsonWriter(job)) {
                // For tests (its the same instance as in writer)
                this.coverallsFile = writer.getCoverallsFile();

                final var sourceCallback = this.createSourceCallbackChain(writer, reporters);
                reporters.add(new DryRunLogger(job.isDryRun(), this.coverallsFile));

                this.report(reporters, Position.BEFORE);
                this.writeCoveralls(writer, sourceCallback, parsers);
                this.report(reporters, Position.AFTER);
            }

            if (!job.isDryRun()) {
                this.submitData(client, this.coverallsFile);
            }
        } catch (final ProcessingException ex) {
            throw new MojoFailureException("Processing of input or output data failed", ex);
        } catch (final IOException ex) {
            throw new MojoFailureException("I/O operation failed", ex);
        } catch (final Exception ex) {
            throw new MojoExecutionException("Build error", ex);
        }
    }

    /**
     * Creates the coverage parsers.
     *
     * @param sourceLoader
     *            source loader that extracts source files
     *
     * @return coverage parsers for all maven modules and additional reports
     *
     * @throws IOException
     *             if parsers cannot be created
     */
    protected List<CoverageParser> createCoverageParsers(final SourceLoader sourceLoader) throws IOException {
        return new CoverageParsersFactory(this.project, sourceLoader).withJaCoCoReports(this.jacocoReports)
                .withCoberturaReports(this.coberturaReports).withSagaReports(this.sagaReports)
                .withCloverReports(this.cloverReports).withRelativeReportDirs(this.relativeReportDirs).createParsers();
    }

    /**
     * Creates the source loader.
     *
     * @param job
     *            the job describing the coveralls report
     *
     * @return source loader that extracts source files
     */
    protected SourceLoader createSourceLoader(final Job job) {
        return new SourceLoaderFactory(job.getGit().getBaseDir(), this.project, Charset.forName(this.sourceEncoding))
                .withSourceDirectories(this.sourceDirectories).withScanForSources(this.scanForSources)
                .createSourceLoader();
    }

    /**
     * Creates the environment.
     *
     * @return environment to setup mojo and service specific properties
     */
    protected Environment createEnvironment() {
        return new Environment(this, this.getServices());
    }

    /**
     * Gets the services.
     *
     * @return list of available continuous integration services
     */
    protected List<ServiceSetup> getServices() {
        final var env = System.getenv();
        final List<ServiceSetup> services = new ArrayList<>();
        services.add(new GitHub(env));
        services.add(new Shippable(env));
        services.add(new Travis(env));
        services.add(new Circle(env));
        services.add(new Jenkins(env));
        services.add(new Bamboo(env));
        services.add(new Appveyor(env));
        services.add(new Wercker(env));
        services.add(new General(env));
        return services;
    }

    /**
     * Creates the job.
     *
     * @return job that describes the coveralls report
     *
     * @throws ProcessingException
     *             if processing of timestamp fails
     * @throws IOException
     *             if an I/O error occurs
     */
    protected Job createJob() throws ProcessingException, IOException {
        final var git = new GitRepository(this.basedir).load();
        final var time = this.timestamp == null ? null
                : new TimestampParser(this.timestampFormat).parse(this.timestamp).toEpochMilli();

        // Log all non-secret items for debugging and transparency
        this.getLog().info("Coveralls Job Configuration:");
        this.getLog().info("  serviceName: " + this.serviceName);
        this.getLog().info("  serviceJobId: " + this.serviceJobId);
        this.getLog().info("  serviceBuildNumber: " + this.serviceBuildNumber);
        this.getLog().info("  serviceBuildUrl: " + this.serviceBuildUrl);
        this.getLog().info("  parallel: " + this.parallel);
        this.getLog().info("  branch: " + this.branch);
        this.getLog().info("  pullRequest: " + this.pullRequest);
        this.getLog().info("  timestamp: " + time);
        this.getLog().info("  basedir: " + (this.basedir != null ? this.basedir.getAbsolutePath() : "null"));
        this.getLog().info("  sourceEncoding: " + this.sourceEncoding);
        this.getLog().info(
                "  coverallsFile: " + (this.coverallsFile != null ? this.coverallsFile.getAbsolutePath() : "null"));
        this.getLog().info("  coverallsUrl: " + this.coverallsUrl);

        return new Job().withRepoToken(this.repoToken).withServiceName(this.serviceName)
                .withServiceJobId(this.serviceJobId).withServiceBuildNumber(this.serviceBuildNumber)
                .withServiceBuildUrl(this.serviceBuildUrl).withParallel(this.parallel)
                .withServiceEnvironment(this.serviceEnvironment).withDryRun(this.dryRun).withBranch(this.branch)
                .withPullRequest(this.pullRequest).withTimestamp(time).withGit(git);
    }

    /**
     * Creates the json writer.
     *
     * @param job
     *            the job describing the coveralls report
     *
     * @return JSON writer that writes the coveralls data
     *
     * @throws IOException
     *             if an I/O error occurs
     */
    protected JsonWriter createJsonWriter(final Job job) throws IOException {
        return new JsonWriter(job, this.coverallsFile);
    }

    /**
     * Creates the coveralls client.
     *
     * @return http client that submits the coveralls data
     */
    protected CoverallsClient createCoverallsClient() {
        return new CoverallsProxyClient(this.coverallsUrl, this.settings.getActiveProxy());
    }

    /**
     * Creates the source callback chain.
     *
     * @param writer
     *            the JSON writer
     * @param reporters
     *            the logging reporters
     *
     * @return source callback chain for different source handlers
     */
    protected SourceCallback createSourceCallbackChain(final JsonWriter writer, final List<Logger> reporters) {
        SourceCallback chain = writer;
        if (this.getLog().isInfoEnabled()) {
            final var coverageTracingReporter = new CoverageTracingLogger(chain);
            chain = coverageTracingReporter;
            reporters.add(coverageTracingReporter);
        }
        return new UniqueSourceCallback(chain);
    }

    /**
     * Writes coverage data to JSON file.
     *
     * @param writer
     *            JSON writer that writes the coveralls data
     * @param sourceCallback
     *            the source callback handler
     * @param parsers
     *            list of coverage parsers
     *
     * @throws ProcessingException
     *             if process to to create JSON file fails
     * @throws IOException
     *             if an I/O error occurs
     */
    protected void writeCoveralls(final JsonWriter writer, final SourceCallback sourceCallback,
            final List<CoverageParser> parsers) throws ProcessingException, IOException {
        this.getLog().info("Writing Coveralls data to " + this.coverallsFile.getAbsolutePath() + "...");
        final var now = System.currentTimeMillis();
        sourceCallback.onBegin();
        for (final CoverageParser parser : parsers) {
            this.getLog().info("Processing coverage report from " + parser.getCoverageFile().getAbsolutePath());
            parser.parse(sourceCallback);
        }
        sourceCallback.onComplete();
        final var duration = System.currentTimeMillis() - now;
        this.getLog().info("Successfully wrote Coveralls data in " + duration + "ms");
    }

    /**
     * Submit data.
     *
     * @param client
     *            the client
     * @param coverallsFile
     *            the coveralls file
     *
     * @throws ProcessingException
     *             the processing exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private void submitData(final CoverallsClient client, final File coverallsFile)
            throws ProcessingException, IOException {
        this.getLog().info("Submitting Coveralls data to API");
        final var now = System.currentTimeMillis();
        try {
            final var response = client.submit(coverallsFile);
            final var duration = System.currentTimeMillis() - now;
            this.getLog()
                    .info("Successfully submitted Coveralls data in " + duration + "ms for " + response.getMessage());
            this.getLog().info(response.getUrl());
            this.getLog().info("*** Coverage results are usually available immediately on Coveralls.");
            this.getLog().info("    If you see question marks or missing data, please allow some time for processing.");
        } catch (final ProcessingException ex) {
            final var duration = System.currentTimeMillis() - now;
            final var message = "Submission failed in " + duration + "ms while processing data";
            this.handleSubmissionError(ex, message, true);
        } catch (final IOException ex) {
            final var duration = System.currentTimeMillis() - now;
            final var message = "Submission failed in " + duration + "ms while handling I/O operations";
            this.handleSubmissionError(ex, message, this.failOnServiceError);
        }
    }

    /**
     * Handle submission error.
     *
     * @param <T>
     *            the generic type
     * @param ex
     *            the ex
     * @param message
     *            the message
     * @param failOnException
     *            the fail on exception
     *
     * @throws T
     *             the t
     */
    private <T extends Exception> void handleSubmissionError(final T ex, final String message,
            final boolean failOnException) throws T {
        if (failOnException) {
            this.getLog().error(message);
            throw ex;
        }
        this.getLog().warn(message);
    }

    /**
     * Report.
     *
     * @param reporters
     *            the reporters
     * @param position
     *            the position
     */
    private void report(final List<Logger> reporters, final Position position) {
        for (final Logger reporter : reporters) {
            if (position.equals(reporter.getPosition())) {
                reporter.log(this.getLog());
            }
        }
    }
}
