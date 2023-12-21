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
import java.util.List;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Reporting;
import org.apache.maven.project.MavenProject;
import org.eluder.coveralls.maven.plugin.CoverageParser;
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

@ExtendWith(MockitoExtension.class)
class CoverageParsersFactoryTest {

    @TempDir(cleanup = CleanupMode.ON_SUCCESS)
    public Path folder;

    @Mock
    private MavenProject projectMock;

    @Mock
    private SourceLoader sourceLoaderMock;

    @Mock
    private Model modelMock;

    @Mock
    private Reporting reportingMock;

    @Mock
    private Build buildMock;

    private Path reportingDir;

    private Path targetDir;

    @BeforeEach
    void init() throws Exception{
        reportingDir = Files.createDirectory(folder.resolve("reportingDir"));  
        targetDir = Files.createDirectory(folder.resolve("targetDir"));
        lenient().when(projectMock.getCollectedProjects()).thenReturn(Collections.<MavenProject>emptyList());
        lenient().when(projectMock.getModel()).thenReturn(modelMock);
        lenient().when(projectMock.getBuild()).thenReturn(buildMock);
        lenient().when(modelMock.getReporting()).thenReturn(reportingMock);
        lenient().when(reportingMock.getOutputDirectory()).thenReturn(reportingDir.toFile().getAbsolutePath());
        lenient().when(buildMock.getDirectory()).thenReturn(targetDir.toFile().getAbsolutePath());
    }

    @Test
    void testCreateEmptyParsers() throws Exception {
        assertThrows(IOException.class, () -> {
            createCoverageParsersFactory().createParsers();
        });
    }

    @Test
    void testCreateJaCoCoParser() throws Exception {
        Path jacocoDir = Files.createDirectory(reportingDir.resolve("jacoco")); 
        Files.createFile(jacocoDir.resolve("jacoco.xml"));
        List<CoverageParser> parsers = createCoverageParsersFactory().createParsers();
        assertEquals(1, parsers.size());
        assertEquals(JaCoCoParser.class, parsers.get(0).getClass());
    }

    @Test
    void testCreateCoberturaParser() throws Exception {
        Path coberturaDir = Files.createDirectory(reportingDir.resolve("cobertura"));
        Files.createFile(coberturaDir.resolve("coverage.xml"));
        List<CoverageParser> parsers = createCoverageParsersFactory().createParsers();
        assertEquals(1, parsers.size());
        assertEquals(CoberturaParser.class, parsers.get(0).getClass());
    }

    @Test
    void testCreateSagaParser() throws Exception {
        Path sagaDir = Files.createDirectory(targetDir.resolve("saga-coverage"));
        Files.createFile(sagaDir.resolve("total-coverage.xml"));
        List<CoverageParser> parsers = createCoverageParsersFactory().createParsers();
        assertEquals(1, parsers.size());
        assertEquals(SagaParser.class, parsers.get(0).getClass());
    }

    @Test
    void testWithJaCoCoReport() throws Exception {
        File jacocoFile = Files.createFile(reportingDir.resolve("jacoco-report.xml")).toFile();
        jacocoFile.createNewFile();
        CoverageParsersFactory factory = createCoverageParsersFactory().withJaCoCoReports(Arrays.asList(jacocoFile));
        List<CoverageParser> parsers = factory.createParsers();
        assertEquals(1, parsers.size());
        assertEquals(JaCoCoParser.class, parsers.get(0).getClass());
    }

    @Test
    void testWithCoberturaReport() throws Exception {
        File coberturaFile = Files.createFile(reportingDir.resolve("cobertura-report.xml")).toFile();
        coberturaFile.createNewFile();
        CoverageParsersFactory factory = createCoverageParsersFactory().withCoberturaReports(Arrays.asList(coberturaFile));
        List<CoverageParser> parsers = factory.createParsers();
        assertEquals(1, parsers.size());
        assertEquals(CoberturaParser.class, parsers.get(0).getClass());
    }

    @Test
    void testWithSagaReport() throws Exception {
        File sagaFile = Files.createFile(reportingDir.resolve("saga-report.xml")).toFile();
        sagaFile.createNewFile();
        CoverageParsersFactory factory = createCoverageParsersFactory().withSagaReports(Arrays.asList(sagaFile));
        List<CoverageParser> parsers = factory.createParsers();
        assertEquals(1, parsers.size());
        assertEquals(SagaParser.class, parsers.get(0).getClass());
    }

    @Test
    void testWithRelativeReportDirectory() throws Exception {
        Path coberturaDir = Files.createDirectory(reportingDir.resolve("customdir"));
        Files.createFile(coberturaDir.resolve("coverage.xml"));
        CoverageParsersFactory factory = createCoverageParsersFactory().withRelativeReportDirs(Arrays.asList("customdir"));
        List<CoverageParser> parsers = factory.createParsers();
        assertEquals(1, parsers.size());
        assertEquals(CoberturaParser.class, parsers.get(0).getClass());
    }

    @Test
    void testWithRootRelativeReportDirectory() throws Exception {
        Files.createFile(reportingDir.resolve("coverage.xml")).toFile();
        CoverageParsersFactory factory = createCoverageParsersFactory().withRelativeReportDirs(Arrays.asList(File.separator));
        List<CoverageParser> parsers = factory.createParsers();
        assertEquals(1, parsers.size());
        assertEquals(CoberturaParser.class, parsers.get(0).getClass());
    }

    private CoverageParsersFactory createCoverageParsersFactory() {
        return new CoverageParsersFactory(projectMock, sourceLoaderMock);
    }
}
