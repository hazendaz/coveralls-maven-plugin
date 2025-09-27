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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.project.MavenProject;
import org.eluder.coveralls.maven.plugin.source.DirectorySourceLoader;
import org.eluder.coveralls.maven.plugin.source.MultiSourceLoader;
import org.eluder.coveralls.maven.plugin.source.ScanSourceLoader;
import org.eluder.coveralls.maven.plugin.source.SourceLoader;

/**
 * A factory for creating SourceLoader objects.
 */
public class SourceLoaderFactory {

    /** The base dir. */
    private final File baseDir;

    /** The project. */
    private final MavenProject project;

    /** The source encoding. */
    private final String sourceEncoding;

    /** The source directories. */
    private List<File> sourceDirectories;

    /** The scan for sources. */
    private boolean scanForSources;

    /**
     * Instantiates a new source loader factory.
     *
     * @param baseDir
     *            the base dir
     * @param project
     *            the project
     * @param sourceEncoding
     *            the source encoding
     */
    public SourceLoaderFactory(final File baseDir, final MavenProject project, final String sourceEncoding) {
        this.baseDir = baseDir;
        this.project = project;
        this.sourceEncoding = sourceEncoding;
    }

    /**
     * With source directories.
     *
     * @param sourceDirectories
     *            the source directories
     *
     * @return the source loader factory
     */
    public SourceLoaderFactory withSourceDirectories(final List<File> sourceDirectories) {
        this.sourceDirectories = sourceDirectories;
        return this;
    }

    /**
     * With scan for sources.
     *
     * @param scanForSources
     *            the scan for sources
     *
     * @return the source loader factory
     */
    public SourceLoaderFactory withScanForSources(final boolean scanForSources) {
        this.scanForSources = scanForSources;
        return this;
    }

    /**
     * Creates a new SourceLoader object.
     *
     * @return the source loader
     */
    public SourceLoader createSourceLoader() {
        MultiSourceLoader multiSourceLoader = new MultiSourceLoader();
        List<File> directories = new ArrayList<>();
        List<MavenProject> modules = new MavenProjectCollector(project).collect();
        for (MavenProject module : modules) {
            for (String sourceRoot : module.getCompileSourceRoots()) {
                Path sourceDirectory = Path.of(sourceRoot);
                directories.add(sourceDirectory.toFile());
            }
        }
        if (sourceDirectories != null) {
            directories.addAll(sourceDirectories);
        }
        for (File directory : directories) {
            if (directory.exists() && directory.isDirectory()) {
                DirectorySourceLoader moduleSourceLoader = new DirectorySourceLoader(baseDir, directory,
                        sourceEncoding);
                multiSourceLoader.add(moduleSourceLoader);
            }
        }

        if (scanForSources) {
            for (File directory : directories) {
                if (directory.exists() && directory.isDirectory()) {
                    ScanSourceLoader scanSourceLoader = new ScanSourceLoader(baseDir, directory, sourceEncoding);
                    multiSourceLoader.add(scanSourceLoader);
                }
            }
        }
        return multiSourceLoader;
    }
}
