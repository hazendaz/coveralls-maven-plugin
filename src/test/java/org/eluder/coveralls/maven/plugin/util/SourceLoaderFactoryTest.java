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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.lenient;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;

import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * The Class SourceLoaderFactoryTest.
 */
@ExtendWith(MockitoExtension.class)
class SourceLoaderFactoryTest {

    /** The folder. */
    @TempDir(cleanup = CleanupMode.ON_SUCCESS)
    public Path folder;

    /** The root. */
    @Mock
    private MavenProject root;

    /** The m 1. */
    @Mock
    private MavenProject m1;

    /** The m 2. */
    @Mock
    private MavenProject m2;

    /** The root sources. */
    private Path rootSources;

    /** The m 1 sources. */
    private Path m1Sources;

    /** The m 2 sources. */
    private Path m2Sources;

    /**
     * Inits the.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @BeforeEach
    void init() throws IOException {
        rootSources = Files.createDirectory(folder.resolve("src"));
        m1Sources = Files.createDirectory(rootSources.resolve("m1"));
        m2Sources = Files.createDirectory(m1Sources.resolve("m2"));
        lenient().when(root.getCollectedProjects()).thenReturn(Arrays.asList(m1, m2));
        lenient().when(m1.getCollectedProjects()).thenReturn(Collections.<MavenProject> emptyList());
        lenient().when(m2.getCollectedProjects()).thenReturn(Collections.<MavenProject> emptyList());
        lenient().when(root.getCompileSourceRoots()).thenReturn(Arrays.asList(rootSources.toFile().getAbsolutePath()));
        lenient().when(m1.getCompileSourceRoots()).thenReturn(Arrays.asList(m1Sources.toFile().getAbsolutePath()));
        lenient().when(m2.getCompileSourceRoots()).thenReturn(Arrays.asList(m2Sources.toFile().getAbsolutePath()));
    }

    /**
     * Test create source loader.
     */
    @Test
    void testCreateSourceLoader() {
        var sourceLoader = createSourceLoaderFactory(StandardCharsets.UTF_8).createSourceLoader();
        assertNotNull(sourceLoader);
    }

    /**
     * Creates the source loader with additional source directories.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void createSourceLoaderWithAdditionalSourceDirectories() throws IOException {
        var s1 = Files.createDirectory(folder.resolve("s1"));
        var s2 = Files.createDirectory(folder.resolve("s2"));
        var sourceLoader = createSourceLoaderFactory(StandardCharsets.UTF_8)
                .withSourceDirectories(Arrays.asList(s1.toFile(), s2.toFile())).createSourceLoader();
        assertNotNull(sourceLoader);
    }

    /**
     * Creates the source loader with scan for sources.
     */
    @Test
    void createSourceLoaderWithScanForSources() {
        var sourceLoader = createSourceLoaderFactory(StandardCharsets.UTF_8).withScanForSources(true)
                .createSourceLoader();
        assertNotNull(sourceLoader);
    }

    /**
     * Creates the source loader invalid directory.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void createSourceLoaderInvalidDirectory() throws IOException {
        var file = Files.createDirectory(folder.resolve("aFile")).toFile();
        var sourceLoader = createSourceLoaderFactory(StandardCharsets.UTF_8).withSourceDirectories(Arrays.asList(file))
                .withScanForSources(true).createSourceLoader();
        assertNotNull(sourceLoader);
    }

    /**
     * Creates the source loader factory.
     *
     * @param sourceEncoding
     *            the source encoding
     *
     * @return the source loader factory
     */
    private SourceLoaderFactory createSourceLoaderFactory(Charset sourceEncoding) {
        return new SourceLoaderFactory(folder.toFile(), root, sourceEncoding);
    }
}
