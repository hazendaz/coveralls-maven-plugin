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
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

import org.apache.maven.project.MavenProject;
import org.eluder.coveralls.maven.plugin.source.SourceLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SourceLoaderFactoryTest {

    @TempDir
    public File folder;

    @Mock
    private MavenProject root;

    @Mock
    private MavenProject m1;

    @Mock
    private MavenProject m2;

    private File rootSources;
    private File m1Sources;
    private File m2Sources;

    @BeforeEach
    void init() throws Exception {
        rootSources = new File(folder, "src");
        m1Sources = new File("src", "m1");
        m2Sources = new File("src", "m2");
        when(root.getCollectedProjects()).thenReturn(Arrays.asList(m1, m2));
        when(m1.getCollectedProjects()).thenReturn(Collections.<MavenProject>emptyList());
        when(m2.getCollectedProjects()).thenReturn(Collections.<MavenProject>emptyList());
        when(root.getCompileSourceRoots()).thenReturn(Arrays.asList(rootSources.getAbsolutePath()));
        when(m1.getCompileSourceRoots()).thenReturn(Arrays.asList(m1Sources.getAbsolutePath()));
        when(m2.getCompileSourceRoots()).thenReturn(Arrays.asList(m2Sources.getAbsolutePath()));
    }

    @Test
    void testCreateSourceLoader() throws Exception {
        SourceLoader sourceLoader = createSourceLoaderFactory("UTF-8").createSourceLoader();
        assertNotNull(sourceLoader);
    }

    @Test
    void testCreateSourceLoaderWithAdditionalSourceDirectories() throws Exception {
        File s1 = new File(folder, "s1");
        File s2 = new File(folder, "s2");
        SourceLoader sourceLoader = createSourceLoaderFactory("UTF-8")
                .withSourceDirectories(Arrays.asList(s1, s2))
                .createSourceLoader();
        assertNotNull(sourceLoader);
    }

    @Test
    void testCreateSourceLoaderWithScanForSources() throws Exception {
        SourceLoader sourceLoader = createSourceLoaderFactory("UTF-8")
                .withScanForSources( true )
                .createSourceLoader();
        assertNotNull(sourceLoader);
    }

    @Test
    void testCreateSourceLoaderInvalidDirectory() throws Exception {
        File file = new File(folder, "aFile");
        file.createNewFile();
        SourceLoader sourceLoader = createSourceLoaderFactory("UTF-8")
                .withSourceDirectories(Arrays.asList(file))
                .withScanForSources(true)
                .createSourceLoader();
        assertNotNull(sourceLoader);
    }

    private SourceLoaderFactory createSourceLoaderFactory(String sourceEncoding) {
        return new SourceLoaderFactory(folder, root, sourceEncoding);
    }
}
