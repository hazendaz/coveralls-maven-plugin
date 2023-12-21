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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;

import org.apache.maven.project.MavenProject;
import org.eluder.coveralls.maven.plugin.source.SourceLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SourceLoaderFactoryTest {

    @TempDir(cleanup = CleanupMode.ON_SUCCESS)
    public Path folder;

    @Mock
    private MavenProject root;

    @Mock
    private MavenProject m1;

    @Mock
    private MavenProject m2;

    private Path rootSources;
    private Path m1Sources;
    private Path m2Sources;

    @BeforeEach
    void init() throws IOException {
        rootSources = Files.createDirectory(folder.resolve("src")); 
        m1Sources = Files.createDirectory(rootSources.resolve("m1"));
        m2Sources = Files.createDirectory(m1Sources.resolve("m2"));
        lenient().when(root.getCollectedProjects()).thenReturn(Arrays.asList(m1, m2));
        lenient().when(m1.getCollectedProjects()).thenReturn(Collections.<MavenProject>emptyList());
        lenient().when(m2.getCollectedProjects()).thenReturn(Collections.<MavenProject>emptyList());
        lenient().when(root.getCompileSourceRoots()).thenReturn(Arrays.asList(rootSources.toFile().getAbsolutePath()));
        lenient().when(m1.getCompileSourceRoots()).thenReturn(Arrays.asList(m1Sources.toFile().getAbsolutePath()));
        lenient().when(m2.getCompileSourceRoots()).thenReturn(Arrays.asList(m2Sources.toFile().getAbsolutePath()));
    }

    @Test
    void testCreateSourceLoader() {
        SourceLoader sourceLoader = createSourceLoaderFactory("UTF-8").createSourceLoader();
        assertNotNull(sourceLoader);
    }

    @Test
    void testCreateSourceLoaderWithAdditionalSourceDirectories() throws IOException {
        Path s1 = Files.createDirectory(folder.resolve("s1"));
        Path s2 = Files.createDirectory(folder.resolve("s2"));
        SourceLoader sourceLoader = createSourceLoaderFactory("UTF-8")
                .withSourceDirectories(Arrays.asList(s1.toFile(), s2.toFile()))
                .createSourceLoader();
        assertNotNull(sourceLoader);
    }

    @Test
    void testCreateSourceLoaderWithScanForSources() {
        SourceLoader sourceLoader = createSourceLoaderFactory("UTF-8")
                .withScanForSources( true )
                .createSourceLoader();
        assertNotNull(sourceLoader);
    }

    @Test
    void testCreateSourceLoaderInvalidDirectory() throws IOException {
        File file = Files.createDirectory(folder.resolve("aFile")).toFile();
        SourceLoader sourceLoader = createSourceLoaderFactory("UTF-8")
                .withSourceDirectories(Arrays.asList(file))
                .withScanForSources(true)
                .createSourceLoader();
        assertNotNull(sourceLoader);
    }

    private SourceLoaderFactory createSourceLoaderFactory(String sourceEncoding) {
        return new SourceLoaderFactory(folder.toFile(), root, sourceEncoding);
    }
}
