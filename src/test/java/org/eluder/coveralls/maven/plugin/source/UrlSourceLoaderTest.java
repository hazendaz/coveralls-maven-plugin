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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eluder.coveralls.maven.plugin.domain.Source;
import org.eluder.coveralls.maven.plugin.util.TestIoUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;

class UrlSourceLoaderTest {

    @TempDir(cleanup = CleanupMode.ON_SUCCESS)
    public Path folder;

    @Test
    void missingSourceFileFromUrl() throws IOException  {
        UrlSourceLoader sourceLoader = new UrlSourceLoader(folder.toUri().toURL(), new URL("http://domainthatreallydoesnotexistsdfsmshjsfsj.com"), "UTF-8");
        assertNull(sourceLoader.load("Foo.java"));
    }

    @Test
    void loadSourceFromUrl() throws IOException {
        Path scripts = Files.createDirectory(folder.resolve("scripts"));
        File file = Files.createFile(scripts.resolve("file.coffee")).toFile();

        TestIoUtil.writeFileContent("math =\n  root:   Math.sqrt\n  square: square", file);

        String fileName = "scripts/file.coffee";
        URL sourceUrl  = folder.toUri().toURL();
        UrlSourceLoader sourceLoader = new UrlSourceLoader(folder.toUri().toURL(), sourceUrl, "UTF-8");
        Source source = sourceLoader.load(fileName);

        assertEquals(fileName, source.getName());
        assertEquals("259AEA51FD9A0FB9529BDDDECDD3FCAE41BFA7C5C8C79555D61E4FB2910D08363814EC6C02DA1FBF6FF539DCEB7DC180B5043E980651049C24497BDA1CA47DAA", source.getDigest());
        assertEquals(3, source.getCoverage().length);
    }

}
