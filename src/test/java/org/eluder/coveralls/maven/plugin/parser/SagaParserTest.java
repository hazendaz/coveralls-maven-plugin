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
 * The Class SagaParserTest.
 */
class SagaParserTest extends AbstractCoverageParserTest {

    @Override
    protected CoverageParser createCoverageParser(final File coverageFile, final SourceLoader sourceLoader) {
        return new SagaParser(coverageFile, sourceLoader);
    }

    @Override
    protected List<String> getCoverageResources() {
        return Arrays.asList("saga.xml");
    }

    @Override
    protected List<List<String>> getCoverageFixture() {
        return CoverageFixture.JAVASCRIPT_FILES;
    }

    /**
     * Parse coverage with branch coverage lines including covered and missed branches.
     *
     * @throws ProcessingException
     *             the processing exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void parseCoverageWithBranches() throws ProcessingException, IOException {
        final var content = TestIoUtil.readFileContent(TestIoUtil.getFile("BranchScript.js"));
        final var sourceLoader = Mockito.mock(SourceLoader.class);
        Mockito.when(sourceLoader.load("BranchScript.js")).thenAnswer(
                invocation -> new Source("BranchScript.js", content, TestIoUtil.getSha512DigestHex(content)));

        final SourceCallback callback = Mockito.mock(SourceCallback.class);

        final var parser = new SagaParser(TestIoUtil.getFile("saga-branches.xml"), sourceLoader);
        parser.parse(callback);

        final ArgumentCaptor<Source> captor = ArgumentCaptor.forClass(Source.class);
        Mockito.verify(callback).onSource(captor.capture());

        final var source = captor.getValue();
        Assertions.assertThat(source.getName()).isEqualTo("BranchScript.js");

        // Line 2: covered with 3 covered branches and 1 missed branch (75% (3/4))
        // Line 3: missed with 0 covered and 2 missed branches (0% (0/2))
        // Line 4: branch=true but no condition-coverage attribute (null case - returns early)
        final var branches = source.getBranchesList();
        // Line 2 contributes 3 covered branches and 1 missed branch
        // Line 3 contributes 2 missed branches
        // Line 4 contributes nothing (null condition-coverage → early return)
        final var coveredCount = branches.stream().filter(b -> b.getHits() > 0).count();
        final var missedCount = branches.stream().filter(b -> b.getHits() == 0).count();
        Assertions.assertThat(coveredCount).isEqualTo(3);
        Assertions.assertThat(missedCount).isEqualTo(3);
    }

}
