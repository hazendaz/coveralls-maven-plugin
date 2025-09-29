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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.Mockito;
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
        this.reportingDir = Files.createDirectory(this.folder.resolve("reportingDir"));
        this.targetDir = Files.createDirectory(this.folder.resolve("targetDir"));
        Mockito.lenient().when(this.projectMock.getCollectedProjects())
                .thenReturn(Collections.<MavenProject> emptyList());
        Mockito.lenient().when(this.projectMock.getModel()).thenReturn(this.modelMock);
        Mockito.lenient().when(this.projectMock.getBuild()).thenReturn(this.buildMock);
        Mockito.lenient().when(this.modelMock.getReporting()).thenReturn(this.reportingMock);
        Mockito.lenient().when(this.reportingMock.getOutputDirectory())
                .thenReturn(this.reportingDir.toFile().getAbsolutePath());
        Mockito.lenient().when(this.buildMock.getDirectory()).thenReturn(this.targetDir.toFile().getAbsolutePath());
    }

    /**
     * Creates the empty parsers.
     */
    @Test
    void createEmptyParsers() {
        Assertions.assertThrows(IOException.class, () -> {
            this.createCoverageParsersFactory().createParsers();
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
        final var jacocoDir = Files.createDirectory(this.reportingDir.resolve("jacoco"));
        Files.createFile(jacocoDir.resolve("jacoco.xml"));
        final var parsers = this.createCoverageParsersFactory().createParsers();
        Assertions.assertEquals(1, parsers.size());
        Assertions.assertEquals(JaCoCoParser.class, parsers.get(0).getClass());
    }

    /**
     * Creates the cobertura parser.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void createCoberturaParser() throws IOException {
        final var coberturaDir = Files.createDirectory(this.reportingDir.resolve("cobertura"));
        Files.createFile(coberturaDir.resolve("coverage.xml"));
        final var parsers = this.createCoverageParsersFactory().createParsers();
        Assertions.assertEquals(1, parsers.size());
        Assertions.assertEquals(CoberturaParser.class, parsers.get(0).getClass());
    }

    /**
     * Creates the saga parser.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void createSagaParser() throws IOException {
        final var sagaDir = Files.createDirectory(this.targetDir.resolve("saga-coverage"));
        Files.createFile(sagaDir.resolve("total-coverage.xml"));
        final var parsers = this.createCoverageParsersFactory().createParsers();
        Assertions.assertEquals(1, parsers.size());
        Assertions.assertEquals(SagaParser.class, parsers.get(0).getClass());
    }

    /**
     * Creates the clover parser.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void createCloverParser() throws IOException {
        final var cloverDir = Files.createDirectory(this.targetDir.resolve("clover"));
        Files.createFile(cloverDir.resolve("clover.xml"));
        final var parsers = this.createCoverageParsersFactory().createParsers();
        Assertions.assertEquals(1, parsers.size());
        Assertions.assertEquals(CloverParser.class, parsers.get(0).getClass());
    }

    /**
     * With jacoco report.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void withJaCoCoReport() throws IOException {
        final var jacocoFile = Files.createFile(this.reportingDir.resolve("jacoco-report.xml")).toFile();
        jacocoFile.createNewFile();
        final var factory = this.createCoverageParsersFactory().withJaCoCoReports(Arrays.asList(jacocoFile));
        final var parsers = factory.createParsers();
        Assertions.assertEquals(1, parsers.size());
        Assertions.assertEquals(JaCoCoParser.class, parsers.get(0).getClass());
    }

    /**
     * With cobertura report.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void withCoberturaReport() throws IOException {
        final var coberturaFile = Files.createFile(this.reportingDir.resolve("cobertura-report.xml")).toFile();
        coberturaFile.createNewFile();
        final var factory = this.createCoverageParsersFactory().withCoberturaReports(Arrays.asList(coberturaFile));
        final var parsers = factory.createParsers();
        Assertions.assertEquals(1, parsers.size());
        Assertions.assertEquals(CoberturaParser.class, parsers.get(0).getClass());
    }

    /**
     * With saga report.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void withSagaReport() throws IOException {
        final var sagaFile = Files.createFile(this.reportingDir.resolve("saga-report.xml")).toFile();
        sagaFile.createNewFile();
        final var factory = this.createCoverageParsersFactory().withSagaReports(Arrays.asList(sagaFile));
        final var parsers = factory.createParsers();
        Assertions.assertEquals(1, parsers.size());
        Assertions.assertEquals(SagaParser.class, parsers.get(0).getClass());
    }

    /**
     * With clover report.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void withCloverReport() throws IOException {
        final var cloverFile = Files.createFile(this.reportingDir.resolve("clover-report.xml")).toFile();
        cloverFile.createNewFile();
        final var factory = this.createCoverageParsersFactory().withCloverReports(Arrays.asList(cloverFile));
        final var parsers = factory.createParsers();
        Assertions.assertEquals(1, parsers.size());
        Assertions.assertEquals(CloverParser.class, parsers.get(0).getClass());
    }

    /**
     * With relative report directory.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void withRelativeReportDirectory() throws IOException {
        final var coberturaDir = Files.createDirectory(this.reportingDir.resolve("customdir"));
        Files.createFile(coberturaDir.resolve("coverage.xml"));
        final var factory = this.createCoverageParsersFactory().withRelativeReportDirs(Arrays.asList("customdir"));
        final var parsers = factory.createParsers();
        Assertions.assertEquals(1, parsers.size());
        Assertions.assertEquals(CoberturaParser.class, parsers.get(0).getClass());
    }

    /**
     * With root relative report directory.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void withRootRelativeReportDirectory() throws IOException {
        Files.createFile(this.reportingDir.resolve("coverage.xml")).toFile();
        final var factory = this.createCoverageParsersFactory().withRelativeReportDirs(Arrays.asList(File.separator));
        final var parsers = factory.createParsers();
        Assertions.assertEquals(1, parsers.size());
        Assertions.assertEquals(CoberturaParser.class, parsers.get(0).getClass());
    }

    /**
     * Creates the coverage parsers factory.
     *
     * @return the coverage parsers factory
     */
    private CoverageParsersFactory createCoverageParsersFactory() {
        return new CoverageParsersFactory(this.projectMock, this.sourceLoaderMock);
    }

}
