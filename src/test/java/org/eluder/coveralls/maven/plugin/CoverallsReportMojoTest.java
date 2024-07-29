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
package org.eluder.coveralls.maven.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

@ExtendWith(MockitoExtension.class)
public class CoverallsReportMojoTest {

    @TempDir(cleanup = CleanupMode.ON_SUCCESS)
    public Path folder;

    public File coverallsFile;

    private CoverallsReportMojo mojo;

    @Mock
    private CoverallsClient coverallsClientMock;

    @Mock
    private SourceLoader sourceLoaderMock;

    @Mock
    private Job jobMock;

    @Mock
    private Log logMock;

    @Mock
    private MavenProject projectMock;

    @Mock
    private MavenProject collectedProjectMock;

    @Mock
    private Model modelMock;

    @Mock
    private Reporting reportingMock;

    @Mock
    private Build buildMock;

    @Mock
    private Settings settingsMock;

    @BeforeEach
    void init() throws IOException  {
        coverallsFile = Files.createFile(folder.resolve("coverallsFile.json")).toFile();

        lenient().when(sourceLoaderMock.load(anyString())).then(new Answer<Source>() {
            @Override
            public Source answer(final InvocationOnMock invocation) throws IOException {
                String sourceFile = invocation.getArguments()[0].toString();
                String content = readFileContent(sourceFile);
                return new Source(sourceFile, content, TestIoUtil.getSha512DigestHex(content));
            }
        });
        lenient().when(logMock.isInfoEnabled()).thenReturn(true);
        lenient().when(jobMock.validate()).thenReturn(new ValidationErrors());


        mojo = new CoverallsReportMojo() {
            @Override
            protected SourceLoader createSourceLoader(final Job job) {
                return sourceLoaderMock;
            }
            @Override
            protected List<CoverageParser> createCoverageParsers(SourceLoader sourceLoader) {
                List<CoverageParser> parsers = new ArrayList<CoverageParser>();
                parsers.add(new CoberturaParser(TestIoUtil.getFile("cobertura.xml"), sourceLoader));
                return parsers;
            }
            @Override
            protected Environment createEnvironment() {
                return new Environment(this, Collections.<ServiceSetup>emptyList());
            }
            @Override
            protected Job createJob() {
                return jobMock;
            }
            @Override
            protected JsonWriter createJsonWriter(final Job job) throws IOException {
                return new JsonWriter(jobMock, CoverallsReportMojoTest.this.coverallsFile);
            }
            @Override
            protected CoverallsClient createCoverallsClient() {
                return coverallsClientMock;
            }
            @Override
            public Log getLog() {
                return logMock;
            }
        };
        mojo.settings = settingsMock;
        mojo.project = projectMock;
        mojo.sourceEncoding = "UTF-8";
        mojo.failOnServiceError = true;

        lenient().when(modelMock.getReporting()).thenReturn(reportingMock);
        lenient().when(reportingMock.getOutputDirectory()).thenReturn(folder.toFile().getAbsolutePath());
        lenient().when(buildMock.getDirectory()).thenReturn(folder.toFile().getAbsolutePath());

        List<MavenProject> projects = new ArrayList<MavenProject>();
        projects.add(collectedProjectMock);
        lenient().when(projectMock.getCollectedProjects()).thenReturn(projects);
        lenient().when(projectMock.getBuild()).thenReturn(buildMock);
        lenient().when(projectMock.getModel()).thenReturn(modelMock);
        List<String> sourceRoots = new ArrayList<String>();
        sourceRoots.add(folder.toFile().getAbsolutePath());
        lenient().when(collectedProjectMock.getCompileSourceRoots()).thenReturn(sourceRoots);
        lenient().when(collectedProjectMock.getBuild()).thenReturn(buildMock);
        lenient().when(collectedProjectMock.getModel()).thenReturn(modelMock);
    }

    @Test
    void createCoverageParsersWithoutCoverageReports() {
        mojo = new CoverallsReportMojo();
        mojo.settings = settingsMock;
        mojo.project = projectMock;
        assertThrows(IOException.class, () -> {
            mojo.createCoverageParsers(sourceLoaderMock);
        });
    }

    @Test
    void testCreateSourceLoader() throws IOException {
        Git gitMock = Mockito.mock(Git.class);
        Path git = Files.createDirectory(folder.resolve("git"));
        when(gitMock.getBaseDir()).thenReturn(git.toFile());
        when(jobMock.getGit()).thenReturn(gitMock);
        TestIoUtil.writeFileContent("public interface Test { }", Files.createFile(git.resolve("source.java")).toFile()); 
        mojo = new CoverallsReportMojo();
        mojo.settings = settingsMock;
        mojo.project = projectMock;
        mojo.sourceEncoding = "UTF-8";
        SourceLoader sourceLoader = mojo.createSourceLoader(jobMock);
        Source source = sourceLoader.load("git/source.java");
        assertNotNull(source);
    }

    @Test
    void defaultBehavior() throws IOException, MojoExecutionException, MojoFailureException {
        mojo = new CoverallsReportMojo() {
            @Override
            protected SourceLoader createSourceLoader(final Job job) {
                return sourceLoaderMock;
            }
            @Override
            protected List<CoverageParser> createCoverageParsers(SourceLoader sourceLoader) throws IOException {
                return Collections.emptyList();
            }
        };
        mojo.sourceDirectories = Arrays.asList(TestIoUtil.getFile("/"));
        mojo.sourceEncoding = "UTF-8";
        mojo.settings = settingsMock;
        mojo.project = projectMock;
        mojo.repoToken = "asdfg";
        mojo.coverallsFile = Files.createFile(folder.resolve("mojoCoverallsFile")).toFile(); 
        mojo.dryRun = true;
        mojo.skip = false;
        mojo.basedir = TestIoUtil.getFile("/");

        mojo.execute();
    }

    @Test
    void successfullSubmission() throws ProcessingException, IOException, MojoExecutionException, MojoFailureException {
        when(coverallsClientMock.submit(any(File.class))).thenReturn(new CoverallsResponse("success", false, null));
        mojo.execute();
        String json = TestIoUtil.readFileContent(coverallsFile);
        assertNotNull(json);

        String[][] fixture = CoverageFixture.JAVA_FILES;
        for (String[] coverageFile : fixture) {
            assertTrue(json.contains(coverageFile[0]));
        }

        verifySuccessfullSubmit(logMock, fixture);
    }

    @Test
    void failWithProcessingException() throws ProcessingException, IOException, MojoExecutionException {
        when(coverallsClientMock.submit(any(File.class))).thenThrow(new ProcessingException());
        try {
            mojo.execute();
            fail("Should have failed with MojoFailureException");
        } catch (MojoFailureException ex) {
            assertEquals(ProcessingException.class, ex.getCause().getClass());
        }
    }

    @Test
    void processingExceptionWithAllowedServiceFailure() throws ProcessingException, IOException, MojoExecutionException {
        mojo.failOnServiceError = false;
        when(coverallsClientMock.submit(any(File.class))).thenThrow(new ProcessingException());
        try {
            mojo.execute();
            fail("Should have failed with MojoFailureException");
        } catch (MojoFailureException ex) {
            assertEquals(ProcessingException.class, ex.getCause().getClass());
        }
    }

    @Test
    void failWithIOException() throws ProcessingException, IOException, MojoExecutionException {
        when(coverallsClientMock.submit(any(File.class))).thenThrow(new IOException());
        try {
            mojo.execute();
            fail("Should have failed with MojoFailureException");
        } catch (MojoFailureException ex) {
            assertEquals(IOException.class, ex.getCause().getClass());
        }
    }

    @Test
    void iOExceptionWithAllowedServiceFailure() throws ProcessingException, IOException, MojoExecutionException, MojoFailureException {
        mojo.failOnServiceError = false;
        when(coverallsClientMock.submit(any(File.class))).thenThrow(new IOException());
        mojo.execute();
        verify(logMock).warn(anyString());
    }

    @Test
    void failWithNullPointerException() throws ProcessingException, IOException, MojoFailureException {
        when(coverallsClientMock.submit(any(File.class))).thenThrow(new NullPointerException());
        try {
            mojo.execute();
            fail("Should have failed with MojoFailureException");
        } catch (MojoExecutionException ex) {
            assertEquals(NullPointerException.class, ex.getCause().getClass());
        }
    }

    @Test
    void skipExecution() throws MojoExecutionException, MojoFailureException {
        mojo.skip = true;
        mojo.execute();

        verifyNoInteractions(jobMock);
    }

    public static void verifySuccessfullSubmit(Log logMock, String[][] fixture) {
        verify(logMock).info("Gathered code coverage metrics for " + CoverageFixture.getTotalFiles(fixture) + " source files with " + CoverageFixture.getTotalLines(fixture) + " lines of code:");
        verify(logMock).info("*** It might take hours for Coveralls to update the actual coverage numbers for a job");
    }

    protected String readFileContent(final String sourceFile) throws FileNotFoundException, IOException  {
        return TestIoUtil.readFileContent(TestIoUtil.getFile(sourceFile));
    }
}
