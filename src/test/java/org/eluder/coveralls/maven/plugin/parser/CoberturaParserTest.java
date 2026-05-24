/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013-2026 The Coveralls Maven Plugin Project Contributors:
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
package org.eluder.coveralls.maven.plugin.parser;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.eluder.coveralls.maven.plugin.CoverageFixture;
import org.eluder.coveralls.maven.plugin.CoverageParser;
import org.eluder.coveralls.maven.plugin.ProcessingException;
import org.eluder.coveralls.maven.plugin.domain.Source;
import org.eluder.coveralls.maven.plugin.source.SourceCallback;
import org.eluder.coveralls.maven.plugin.source.SourceLoader;
import org.eluder.coveralls.maven.plugin.util.TestIoUtil;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * The Class CoberturaParserTest.
 */
class CoberturaParserTest extends AbstractCoverageParserTest {

    @Override
    protected CoverageParser createCoverageParser(final File coverageFile, final SourceLoader sourceLoader) {
        return new CoberturaParser(coverageFile, sourceLoader);
    }

    @Override
    protected List<String> getCoverageResources() {
        return Arrays.asList("cobertura.xml");
    }

    @Override
    protected List<List<String>> getCoverageFixture() {
        return CoverageFixture.JAVA_FILES;
    }

    /**
     * Tests that a branch="true" line without a condition-coverage attribute causes an early return with no branches
     * added to the source (covers the null condition-coverage guard).
     *
     * @throws ProcessingException
     *             the processing exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void parseBranchLineWithNullConditionCoverage() throws ProcessingException, IOException {
        final var content = TestIoUtil.readFileContent(TestIoUtil.getFile("SimpleCoverage.java"));
        final var sourceLoader = Mockito.mock(SourceLoader.class);
        Mockito.when(sourceLoader.load("org/eluder/coverage/sample/SimpleCoverage.java"))
                .thenAnswer(invocation -> new Source("org/eluder/coverage/sample/SimpleCoverage.java", content,
                        TestIoUtil.getSha512DigestHex(content)));

        final SourceCallback callback = Mockito.mock(SourceCallback.class);

        final var parser = new CoberturaParser(TestIoUtil.getFile("cobertura-branch-null.xml"), sourceLoader);
        parser.parse(callback);

        final ArgumentCaptor<Source> captor = ArgumentCaptor.forClass(Source.class);
        Mockito.verify(callback).onSource(captor.capture());

        final var source = captor.getValue();
        // The branch=true line with no condition-coverage should NOT add any branches
        Assertions.assertThat(source.getBranchesList()).isEmpty();
    }

}
