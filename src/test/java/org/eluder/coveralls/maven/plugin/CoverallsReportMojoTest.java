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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Reporting;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;
import org.eluder.coveralls.maven.plugin.domain.CoverallsResponse;
import org.eluder.coveralls.maven.plugin.domain.Git;
import org.eluder.coveralls.maven.plugin.domain.Job;
import org.eluder.coveralls.maven.plugin.domain.Source;
import org.eluder.coveralls.maven.plugin.httpclient.CoverallsClient;
import org.eluder.coveralls.maven.plugin.json.JsonWriter;
import org.eluder.coveralls.maven.plugin.parser.CoberturaParser;
import org.eluder.coveralls.maven.plugin.service.ServiceSetup;
import org.eluder.coveralls.maven.plugin.source.SourceLoader;
import org.eluder.coveralls.maven.plugin.util.TestIoUtil;
import org.eluder.coveralls.maven.plugin.validation.ValidationErrors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * The Class CoverallsReportMojoTest.
 */
@ExtendWith(MockitoExtension.class)
class CoverallsReportMojoTest {

    /** The folder. */
    @TempDir(cleanup = CleanupMode.ON_SUCCESS)
    Path folder;

    /** The coveralls file. */
    File coverallsFile;

    /** The mojo. */
    CoverallsReportMojo mojo;

    /** The coveralls client mock. */
    @Mock
    CoverallsClient coverallsClientMock;

    /** The source loader mock. */
    @Mock
    SourceLoader sourceLoaderMock;

    /** The job mock. */
    @Mock
    Job jobMock;

    /** The log mock. */
    @Mock
    Log logMock;

    /** The project mock. */
    @Mock
    MavenProject projectMock;

    /** The collected project mock. */
    @Mock
    MavenProject collectedProjectMock;

    /** The model mock. */
    @Mock
    Model modelMock;

    /** The reporting mock. */
    @Mock
    Reporting reportingMock;

    /** The build mock. */
    @Mock
    Build buildMock;

    /** The settings mock. */
    @Mock
    Settings settingsMock;

    /**
     * Inits the.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @BeforeEach
    void init() throws IOException {
        this.coverallsFile = Files.createFile(this.folder.resolve("coverallsFile.json")).toFile();

        Mockito.lenient().when(this.sourceLoaderMock.load(ArgumentMatchers.anyString())).then(invocation -> {
            final var sourceFile = invocation.getArguments()[0].toString();
            final var content = this.readFileContent(sourceFile);
            return new Source(sourceFile, content, TestIoUtil.getSha512DigestHex(content));
        });
        Mockito.lenient().when(this.logMock.isInfoEnabled()).thenReturn(true);
        Mockito.lenient().when(this.jobMock.validate()).thenReturn(new ValidationErrors());

        this.mojo = new CoverallsReportMojo() {
            @Override
            protected SourceLoader createSourceLoader(final Job job) {
                return CoverallsReportMojoTest.this.sourceLoaderMock;
            }

            @Override
            protected List<CoverageParser> createCoverageParsers(SourceLoader sourceLoader) {
                final List<CoverageParser> parsers = new ArrayList<>();
                parsers.add(new CoberturaParser(TestIoUtil.getFile("cobertura.xml"), sourceLoader));
                return parsers;
            }

            @Override
            protected Environment createEnvironment() {
                return new Environment(this, Collections.<ServiceSetup> emptyList());
            }

            @Override
            protected Job createJob() {
                return CoverallsReportMojoTest.this.jobMock;
            }

            @Override
            protected JsonWriter createJsonWriter(final Job job) throws IOException {
                return new JsonWriter(CoverallsReportMojoTest.this.jobMock, CoverallsReportMojoTest.this.coverallsFile);
            }

            @Override
            protected CoverallsClient createCoverallsClient() {
                return CoverallsReportMojoTest.this.coverallsClientMock;
            }

            @Override
            public Log getLog() {
                return CoverallsReportMojoTest.this.logMock;
            }
        };
        this.mojo.settings = this.settingsMock;
        this.mojo.project = this.projectMock;
        this.mojo.sourceEncoding = "UTF-8";
        this.mojo.failOnServiceError = true;

        Mockito.lenient().when(this.modelMock.getReporting()).thenReturn(this.reportingMock);
        Mockito.lenient().when(this.reportingMock.getOutputDirectory())
                .thenReturn(this.folder.toFile().getAbsolutePath());
        Mockito.lenient().when(this.buildMock.getDirectory()).thenReturn(this.folder.toFile().getAbsolutePath());

        final List<MavenProject> projects = new ArrayList<>();
        projects.add(this.collectedProjectMock);
        Mockito.lenient().when(this.projectMock.getCollectedProjects()).thenReturn(projects);
        Mockito.lenient().when(this.projectMock.getBuild()).thenReturn(this.buildMock);
        Mockito.lenient().when(this.projectMock.getModel()).thenReturn(this.modelMock);
        final List<String> sourceRoots = new ArrayList<>();
        sourceRoots.add(this.folder.toFile().getAbsolutePath());
        Mockito.lenient().when(this.collectedProjectMock.getCompileSourceRoots()).thenReturn(sourceRoots);
        Mockito.lenient().when(this.collectedProjectMock.getBuild()).thenReturn(this.buildMock);
        Mockito.lenient().when(this.collectedProjectMock.getModel()).thenReturn(this.modelMock);
    }

    /**
     * Creates the coverage parsers without coverage reports.
     */
    @Test
    void createCoverageParsersWithoutCoverageReports() {
        this.mojo = new CoverallsReportMojo();
        this.mojo.settings = this.settingsMock;
        this.mojo.project = this.projectMock;
        Assertions.assertThrows(IOException.class, () -> {
            this.mojo.createCoverageParsers(this.sourceLoaderMock);
        });
    }

    /**
     * Test create source loader.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testCreateSourceLoader() throws IOException {
        final var gitMock = Mockito.mock(Git.class);
        final var git = Files.createDirectory(this.folder.resolve("git"));
        Mockito.when(gitMock.getBaseDir()).thenReturn(git.toFile());
        Mockito.when(this.jobMock.getGit()).thenReturn(gitMock);
        TestIoUtil.writeFileContent("public interface Test { }", Files.createFile(git.resolve("source.java")).toFile());
        this.mojo = new CoverallsReportMojo();
        this.mojo.settings = this.settingsMock;
        this.mojo.project = this.projectMock;
        this.mojo.sourceEncoding = "UTF-8";
        final var sourceLoader = this.mojo.createSourceLoader(this.jobMock);
        final var source = sourceLoader.load("git/source.java");
        Assertions.assertNotNull(source);
    }

    /**
     * Default behavior.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws MojoExecutionException
     *             the mojo execution exception
     * @throws MojoFailureException
     *             the mojo failure exception
     */
    @Test
    void defaultBehavior() throws IOException, MojoExecutionException, MojoFailureException {
        this.mojo = new CoverallsReportMojo() {
            @Override
            protected SourceLoader createSourceLoader(final Job job) {
                return CoverallsReportMojoTest.this.sourceLoaderMock;
            }

            @Override
            protected List<CoverageParser> createCoverageParsers(SourceLoader sourceLoader) throws IOException {
                return Collections.emptyList();
            }
        };
        this.mojo.sourceDirectories = Arrays.asList(TestIoUtil.getFile("/"));
        this.mojo.sourceEncoding = "UTF-8";
        this.mojo.settings = this.settingsMock;
        this.mojo.project = this.projectMock;
        this.mojo.repoToken = "asdfg";
        this.mojo.coverallsFile = Files.createFile(this.folder.resolve("mojoCoverallsFile")).toFile();
        this.mojo.dryRun = true;
        this.mojo.skip = false;
        this.mojo.basedir = TestIoUtil.getFile("/");

        Assertions.assertDoesNotThrow(() -> {
            this.mojo.execute();
        });
    }

    /**
     * Successfull submission.
     *
     * @throws ProcessingException
     *             the processing exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws MojoExecutionException
     *             the mojo execution exception
     * @throws MojoFailureException
     *             the mojo failure exception
     */
    @Test
    void successfulSubmission() throws ProcessingException, IOException, MojoExecutionException, MojoFailureException,
            InterruptedException {
        Mockito.when(coverallsClientMock.submit(ArgumentMatchers.any(File.class)))
                .thenReturn(new CoverallsResponse("success", false, null));
        mojo.execute();
        var json = TestIoUtil.readFileContent(coverallsFile);
        Assertions.assertNotNull(json);

        final List<List<String>> fixture = CoverageFixture.JAVA_FILES;
        for (final List<String> coverageFile : fixture) {
            Assertions.assertTrue(json.contains(coverageFile.get(0)));
        }

        verifySuccessfulSubmit(logMock, fixture);
    }

    /**
     * Fail with processing exception.
     *
     * @throws ProcessingException
     *             the processing exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws MojoExecutionException
     *             the mojo execution exception
     */
    @Test
    void failWithProcessingException() throws ProcessingException, IOException, MojoExecutionException, InterruptedException {
        Mockito.when(coverallsClientMock.submit(ArgumentMatchers.any(File.class))).thenThrow(new ProcessingException());
        try {
            this.mojo.execute();
            Assertions.fail("Should have failed with MojoFailureException");
        } catch (final MojoFailureException ex) {
            Assertions.assertEquals(ProcessingException.class, ex.getCause().getClass());
        }
    }

    /**
     * Processing exception with allowed service failure.
     *
     * @throws ProcessingException
     *             the processing exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws MojoExecutionException
     *             the mojo execution exception
     */
    @Test
    void processingExceptionWithAllowedServiceFailure()
            throws ProcessingException, IOException, MojoExecutionException, InterruptedException {
        mojo.failOnServiceError = false;
        Mockito.when(coverallsClientMock.submit(ArgumentMatchers.any(File.class))).thenThrow(new ProcessingException());
        try {
            this.mojo.execute();
            Assertions.fail("Should have failed with MojoFailureException");
        } catch (final MojoFailureException ex) {
            Assertions.assertEquals(ProcessingException.class, ex.getCause().getClass());
        }
    }

    /**
     * Fail with IO exception.
     *
     * @throws ProcessingException
     *             the processing exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws MojoExecutionException
     *             the mojo execution exception
     */
    @Test
    void failWithIOException() throws ProcessingException, IOException, MojoExecutionException, InterruptedException {
        Mockito.when(coverallsClientMock.submit(ArgumentMatchers.any(File.class))).thenThrow(new IOException());
        try {
            this.mojo.execute();
            Assertions.fail("Should have failed with MojoFailureException");
        } catch (final MojoFailureException ex) {
            Assertions.assertEquals(IOException.class, ex.getCause().getClass());
        }
    }

    /**
     * I O exception with allowed service failure.
     *
     * @throws ProcessingException
     *             the processing exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws MojoExecutionException
     *             the mojo execution exception
     * @throws MojoFailureException
     *             the mojo failure exception
     */
    @Test
    void iOExceptionWithAllowedServiceFailure() throws ProcessingException, IOException, MojoExecutionException,
            MojoFailureException, InterruptedException {
        mojo.failOnServiceError = false;
        Mockito.when(coverallsClientMock.submit(ArgumentMatchers.any(File.class))).thenThrow(new IOException());
        mojo.execute();
        Mockito.verify(logMock).warn(ArgumentMatchers.anyString());
    }

    /**
     * Fail with null pointer exception.
     *
     * @throws ProcessingException
     *             the processing exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws MojoFailureException
     *             the mojo failure exception
     */
    @Test
    void failWithNullPointerException()
            throws ProcessingException, IOException, MojoFailureException, InterruptedException {
        Mockito.when(coverallsClientMock.submit(ArgumentMatchers.any(File.class)))
                .thenThrow(new NullPointerException());
        try {
            this.mojo.execute();
            Assertions.fail("Should have failed with MojoFailureException");
        } catch (final MojoExecutionException ex) {
            Assertions.assertEquals(NullPointerException.class, ex.getCause().getClass());
        }
    }

    /**
     * Skip execution.
     *
     * @throws MojoExecutionException
     *             the mojo execution exception
     * @throws MojoFailureException
     *             the mojo failure exception
     */
    @Test
    void skipExecution() throws MojoExecutionException, MojoFailureException {
        this.mojo.skip = true;
        this.mojo.execute();

        Mockito.verifyNoInteractions(this.jobMock);
    }

    static void verifySuccessfulSubmit(Log logMock, List<List<String>> fixture) {
        Mockito.verify(logMock, Mockito.atMostOnce())
                .info("Gathered code coverage metrics for " + CoverageFixture.getTotalFiles(fixture)
                        + " source files with " + CoverageFixture.getTotalLines(fixture) + " lines of code:");

        Mockito.verify(logMock, Mockito.atMostOnce())
                .info("*** Coverage results are usually available immediately on Coveralls.");
    }

    /**
     * Read file content.
     *
     * @param sourceFile
     *            the source file
     *
     * @return the string
     *
     * @throws FileNotFoundException
     *             the file not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    String readFileContent(final String sourceFile) throws IOException {
        return TestIoUtil.readFileContent(TestIoUtil.getFile(sourceFile));
    }

}
