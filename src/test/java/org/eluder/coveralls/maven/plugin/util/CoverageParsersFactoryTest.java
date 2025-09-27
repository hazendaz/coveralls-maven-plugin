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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Reporting;
import org.apache.maven.project.MavenProject;
import org.eluder.coveralls.maven.plugin.parser.CloverParser;
import org.eluder.coveralls.maven.plugin.parser.CoberturaParser;
import org.eluder.coveralls.maven.plugin.parser.JaCoCoParser;
import org.eluder.coveralls.maven.plugin.parser.SagaParser;
import org.eluder.coveralls.maven.plugin.source.SourceLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * The Class CoverageParsersFactoryTest.
 */
@ExtendWith(MockitoExtension.class)
class CoverageParsersFactoryTest {

    /** The folder. */
    @TempDir(cleanup = CleanupMode.ON_SUCCESS)
    public Path folder;

    /** The project mock. */
    @Mock
    private MavenProject projectMock;

    /** The source loader mock. */
    @Mock
    private SourceLoader sourceLoaderMock;

    /** The model mock. */
    @Mock
    private Model modelMock;

    /** The reporting mock. */
    @Mock
    private Reporting reportingMock;

    /** The build mock. */
    @Mock
    private Build buildMock;

    /** The reporting dir. */
    private Path reportingDir;

    /** The target dir. */
    private Path targetDir;

    /**
     * Inits the.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @BeforeEach
    void init() throws IOException {
        reportingDir = Files.createDirectory(folder.resolve("reportingDir"));
        targetDir = Files.createDirectory(folder.resolve("targetDir"));
        lenient().when(projectMock.getCollectedProjects()).thenReturn(Collections.<MavenProject> emptyList());
        lenient().when(projectMock.getModel()).thenReturn(modelMock);
        lenient().when(projectMock.getBuild()).thenReturn(buildMock);
        lenient().when(modelMock.getReporting()).thenReturn(reportingMock);
        lenient().when(reportingMock.getOutputDirectory()).thenReturn(reportingDir.toFile().getAbsolutePath());
        lenient().when(buildMock.getDirectory()).thenReturn(targetDir.toFile().getAbsolutePath());
    }

    /**
     * Creates the empty parsers.
     */
    @Test
    void createEmptyParsers() {
        assertThrows(IOException.class, () -> {
            createCoverageParsersFactory().createParsers();
        });
    }

    /**
     * Creates the jacoco parser.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void createJaCoCoParser() throws IOException {
        var jacocoDir = Files.createDirectory(reportingDir.resolve("jacoco"));
        Files.createFile(jacocoDir.resolve("jacoco.xml"));
        var parsers = createCoverageParsersFactory().createParsers();
        assertEquals(1, parsers.size());
        assertEquals(JaCoCoParser.class, parsers.get(0).getClass());
    }

    /**
     * Creates the cobertura parser.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void createCoberturaParser() throws IOException {
        var coberturaDir = Files.createDirectory(reportingDir.resolve("cobertura"));
        Files.createFile(coberturaDir.resolve("coverage.xml"));
        var parsers = createCoverageParsersFactory().createParsers();
        assertEquals(1, parsers.size());
        assertEquals(CoberturaParser.class, parsers.get(0).getClass());
    }

    /**
     * Creates the saga parser.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void createSagaParser() throws IOException {
        var sagaDir = Files.createDirectory(targetDir.resolve("saga-coverage"));
        Files.createFile(sagaDir.resolve("total-coverage.xml"));
        var parsers = createCoverageParsersFactory().createParsers();
        assertEquals(1, parsers.size());
        assertEquals(SagaParser.class, parsers.get(0).getClass());
    }

    /**
     * Creates the clover parser.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void createCloverParser() throws IOException {
        var cloverDir = Files.createDirectory(targetDir.resolve("clover"));
        Files.createFile(cloverDir.resolve("clover.xml"));
        var parsers = createCoverageParsersFactory().createParsers();
        assertEquals(1, parsers.size());
        assertEquals(CloverParser.class, parsers.get(0).getClass());
    }

    /**
     * With jacoco report.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void withJaCoCoReport() throws IOException {
        var jacocoFile = Files.createFile(reportingDir.resolve("jacoco-report.xml")).toFile();
        jacocoFile.createNewFile();
        var factory = createCoverageParsersFactory().withJaCoCoReports(Arrays.asList(jacocoFile));
        var parsers = factory.createParsers();
        assertEquals(1, parsers.size());
        assertEquals(JaCoCoParser.class, parsers.get(0).getClass());
    }

    /**
     * With cobertura report.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void withCoberturaReport() throws IOException {
        var coberturaFile = Files.createFile(reportingDir.resolve("cobertura-report.xml")).toFile();
        coberturaFile.createNewFile();
        var factory = createCoverageParsersFactory().withCoberturaReports(Arrays.asList(coberturaFile));
        var parsers = factory.createParsers();
        assertEquals(1, parsers.size());
        assertEquals(CoberturaParser.class, parsers.get(0).getClass());
    }

    /**
     * With saga report.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void withSagaReport() throws IOException {
        var sagaFile = Files.createFile(reportingDir.resolve("saga-report.xml")).toFile();
        sagaFile.createNewFile();
        var factory = createCoverageParsersFactory().withSagaReports(Arrays.asList(sagaFile));
        var parsers = factory.createParsers();
        assertEquals(1, parsers.size());
        assertEquals(SagaParser.class, parsers.get(0).getClass());
    }

    /**
     * With clover report.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void withCloverReport() throws IOException {
        var cloverFile = Files.createFile(reportingDir.resolve("clover-report.xml")).toFile();
        cloverFile.createNewFile();
        var factory = createCoverageParsersFactory().withCloverReports(Arrays.asList(cloverFile));
        var parsers = factory.createParsers();
        assertEquals(1, parsers.size());
        assertEquals(CloverParser.class, parsers.get(0).getClass());
    }

    /**
     * With relative report directory.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void withRelativeReportDirectory() throws IOException {
        var coberturaDir = Files.createDirectory(reportingDir.resolve("customdir"));
        Files.createFile(coberturaDir.resolve("coverage.xml"));
        var factory = createCoverageParsersFactory().withRelativeReportDirs(Arrays.asList("customdir"));
        var parsers = factory.createParsers();
        assertEquals(1, parsers.size());
        assertEquals(CoberturaParser.class, parsers.get(0).getClass());
    }

    /**
     * With root relative report directory.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void withRootRelativeReportDirectory() throws IOException {
        Files.createFile(reportingDir.resolve("coverage.xml")).toFile();
        var factory = createCoverageParsersFactory().withRelativeReportDirs(Arrays.asList(File.separator));
        var parsers = factory.createParsers();
        assertEquals(1, parsers.size());
        assertEquals(CoberturaParser.class, parsers.get(0).getClass());
    }

    /**
     * Creates the coverage parsers factory.
     *
     * @return the coverage parsers factory
     */
    private CoverageParsersFactory createCoverageParsersFactory() {
        return new CoverageParsersFactory(projectMock, sourceLoaderMock);
    }
}
