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

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;

import org.apache.maven.project.MavenProject;
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
        this.rootSources = Files.createDirectory(this.folder.resolve("src"));
        this.m1Sources = Files.createDirectory(this.rootSources.resolve("m1"));
        this.m2Sources = Files.createDirectory(this.m1Sources.resolve("m2"));
        Mockito.lenient().when(this.root.getCollectedProjects()).thenReturn(Arrays.asList(this.m1, this.m2));
        Mockito.lenient().when(this.m1.getCollectedProjects()).thenReturn(Collections.<MavenProject> emptyList());
        Mockito.lenient().when(this.m2.getCollectedProjects()).thenReturn(Collections.<MavenProject> emptyList());
        Mockito.lenient().when(this.root.getCompileSourceRoots())
                .thenReturn(Arrays.asList(this.rootSources.toFile().getAbsolutePath()));
        Mockito.lenient().when(this.m1.getCompileSourceRoots())
                .thenReturn(Arrays.asList(this.m1Sources.toFile().getAbsolutePath()));
        Mockito.lenient().when(this.m2.getCompileSourceRoots())
                .thenReturn(Arrays.asList(this.m2Sources.toFile().getAbsolutePath()));
    }

    /**
     * Test create source loader.
     */
    @Test
    void testCreateSourceLoader() {
        final var sourceLoader = this.createSourceLoaderFactory(StandardCharsets.UTF_8).createSourceLoader();
        Assertions.assertNotNull(sourceLoader);
    }

    /**
     * Creates the source loader with additional source directories.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void createSourceLoaderWithAdditionalSourceDirectories() throws IOException {
        final var s1 = Files.createDirectory(this.folder.resolve("s1"));
        final var s2 = Files.createDirectory(this.folder.resolve("s2"));
        final var sourceLoader = this.createSourceLoaderFactory(StandardCharsets.UTF_8)
                .withSourceDirectories(Arrays.asList(s1.toFile(), s2.toFile())).createSourceLoader();
        Assertions.assertNotNull(sourceLoader);
    }

    /**
     * Creates the source loader with scan for sources.
     */
    @Test
    void createSourceLoaderWithScanForSources() {
        final var sourceLoader = this.createSourceLoaderFactory(StandardCharsets.UTF_8).withScanForSources(true)
                .createSourceLoader();
        Assertions.assertNotNull(sourceLoader);
    }

    /**
     * Creates the source loader invalid directory.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void createSourceLoaderInvalidDirectory() throws IOException {
        final var file = Files.createDirectory(this.folder.resolve("aFile")).toFile();
        final var sourceLoader = this.createSourceLoaderFactory(StandardCharsets.UTF_8)
                .withSourceDirectories(Arrays.asList(file)).withScanForSources(true).createSourceLoader();
        Assertions.assertNotNull(sourceLoader);
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
        return new SourceLoaderFactory(this.folder.toFile(), this.root, sourceEncoding);
    }

}
