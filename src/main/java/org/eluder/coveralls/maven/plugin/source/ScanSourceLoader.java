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
package org.eluder.coveralls.maven.plugin.source;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.SelectorUtils;

/**
 * The Class ScanSourceLoader.
 */
public class ScanSourceLoader extends AbstractSourceLoader {

    /** The cache. */
    private final Map<String, String[]> cache = new HashMap<>();

    /** The source directory. */
    private final File sourceDirectory;

    /**
     * Instantiates a new scan source loader.
     *
     * @param base
     *            the base
     * @param sourceDirectory
     *            the source directory
     * @param sourceEncoding
     *            the source encoding
     */
    public ScanSourceLoader(final File base, final File sourceDirectory, final Charset sourceEncoding) {
        super(base.toURI(), sourceDirectory.toURI(), sourceEncoding);
        this.sourceDirectory = sourceDirectory;
    }

    @Override
    protected InputStream locate(final String sourceFile) throws IOException {
        Path path = Path.of(sourceDirectory.toString(), getFileName(sourceFile));

        if (Files.exists(path)) {
            if (!Files.isRegularFile(path)) {
                throw new IllegalArgumentException(path.toAbsolutePath() + " is not file");
            }
            return new BufferedInputStream(Files.newInputStream(path));
        }
        return null;
    }

    /**
     * Scan for.
     *
     * @param extension
     *            the extension
     *
     * @return the string[]
     */
    private String[] scanFor(final String extension) {
        return cache.computeIfAbsent(extension, ext -> {
            DirectoryScanner scanner = new DirectoryScanner();
            scanner.setBasedir(sourceDirectory);
            scanner.addDefaultExcludes();
            scanner.setIncludes(new String[] { "**/*." + ext });
            scanner.scan();
            return scanner.getIncludedFiles();
        });
    }

    @Override
    protected String getFileName(final String sourceFile) {
        String extension = FilenameUtils.getExtension(sourceFile);
        String[] matchingExtensionFiles = scanFor(extension);

        for (String matchingExtensionFile : matchingExtensionFiles) {
            if (SelectorUtils.matchPath("**/" + sourceFile, matchingExtensionFile, true)) {
                return matchingExtensionFile;
            }
        }

        return sourceFile;
    }

}
