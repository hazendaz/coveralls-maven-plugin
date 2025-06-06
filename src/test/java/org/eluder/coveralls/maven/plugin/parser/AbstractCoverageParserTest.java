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
package org.eluder.coveralls.maven.plugin.parser;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eluder.coveralls.maven.plugin.CoverageFixture;
import org.eluder.coveralls.maven.plugin.CoverageParser;
import org.eluder.coveralls.maven.plugin.ProcessingException;
import org.eluder.coveralls.maven.plugin.domain.Branch;
import org.eluder.coveralls.maven.plugin.domain.Source;
import org.eluder.coveralls.maven.plugin.source.ChainingSourceCallback;
import org.eluder.coveralls.maven.plugin.source.SourceCallback;
import org.eluder.coveralls.maven.plugin.source.SourceLoader;
import org.eluder.coveralls.maven.plugin.source.UniqueSourceCallback;
import org.eluder.coveralls.maven.plugin.util.TestIoUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

@ExtendWith(MockitoExtension.class)
public abstract class AbstractCoverageParserTest {

    @Mock
    SourceLoader sourceLoaderMock;

    @Mock
    SourceCallback sourceCallbackMock;

    public AbstractCoverageParserTest() {
        // Do Nothing
    }

    @BeforeEach
    void init() throws IOException {
        for (String[] coverageFile : getCoverageFixture()) {
            final var name = sourceName(coverageFile[0]);
            final var content = TestIoUtil.readFileContent(TestIoUtil.getFile(name));
            lenient().when(sourceLoaderMock.load(name)).then(sourceAnswer(name, content));
        }
    }

    String sourceName(final String coverageFile) {
        return coverageFile;
    }

    Answer<Source> sourceAnswer(final String name, final String content) {
        return invocation -> new Source(name, content, TestIoUtil.getSha512DigestHex(content));
    }

    @Test
    void parseCoverage() throws ProcessingException, IOException {
        for (String coverageResource : getCoverageResources()) {
            var parser = createCoverageParser(TestIoUtil.getFile(coverageResource), sourceLoaderMock);
            parser.parse(sourceCallbackMock);
        }

        var fixture = getCoverageFixture();

        ArgumentCaptor<Source> captor = ArgumentCaptor.forClass(Source.class);
        verify(sourceCallbackMock, atLeast(CoverageFixture.getTotalFiles(fixture))).onSource(captor.capture());

        var sourceCollector = new SourceCollector();
        var uniqueSourceCallback = new UniqueSourceCallback(sourceCollector);
        var classifierRemover = new ClassifierRemover(uniqueSourceCallback);
        classifierRemover.onBegin();
        for (Source source : captor.getAllValues()) {
            classifierRemover.onSource(source);
        }
        classifierRemover.onComplete();

        for (String[] coverageFile : fixture) {
            assertCoverage(sourceCollector.sources, coverageFile[0], Integer.parseInt(coverageFile[1]),
                    toIntegerSet(coverageFile[2]), toIntegerSet(coverageFile[3]), toIntegerSet(coverageFile[4]),
                    toIntegerSet(coverageFile[5]));
        }
    }

    protected abstract CoverageParser createCoverageParser(File coverageFile, SourceLoader sourceLoader);

    protected abstract List<String> getCoverageResources();

    protected abstract String[][] getCoverageFixture();

    Set<Integer> toIntegerSet(final String commaSeparated) {
        if (commaSeparated.isEmpty()) {
            return Collections.emptySet();
        }
        var split = commaSeparated.split(",", -1);
        Set<Integer> values = new HashSet<>();
        for (String value : split) {
            values.add(Integer.valueOf(value));
        }
        return values;
    }

    static class SourceCollector implements SourceCallback {

        private List<Source> sources = new ArrayList<>();

        @Override
        public void onBegin() {
            // Does nothing
        }

        @Override
        public void onSource(Source source) {
            sources.add(source);
        }

        @Override
        public void onComplete() {
            // Does Nothing
        }
    }

    static class ClassifierRemover extends ChainingSourceCallback {

        public ClassifierRemover(SourceCallback chained) {
            super(chained);
        }

        @Override
        protected void onSourceInternal(Source source) {
            source.setClassifier(null);
        }
    }

    static void assertCoverage(final Collection<Source> sources, final String name, final int lines,
            final Set<Integer> coveredLines, final Set<Integer> missedLines, final Set<Integer> coveredBranches,
            final Set<Integer> missedBranches) {

        Source tested = null;
        for (Source source : sources) {
            if (source.getName().endsWith(name)) {
                tested = source;
                break;
            }
        }
        if (tested == null) {
            fail("Expected source " + name + " not found from coverage report");
        }
        if (tested.getCoverage().length != lines) {
            fail("Expected " + lines + " lines for " + name + " was " + tested.getCoverage().length);
        }
        for (var i = 0; i < tested.getCoverage().length; i++) {
            int lineNumber = i + 1;
            var message = name + " line " + lineNumber + " coverage";
            if (coveredLines.contains(lineNumber)) {
                assertTrue(tested.getCoverage()[i] > 0, message);
            } else if (missedLines.contains(lineNumber)) {
                assertTrue(tested.getCoverage()[i] == 0, message);
            } else {
                assertNull(tested.getCoverage()[i], message);
            }
        }
        for (final Branch b : tested.getBranchesList()) {
            final var message = name + " branch " + b.getBranchNumber() + " coverage in line " + b.getLineNumber();
            if (b.getHits() > 0) {
                assertTrue(coveredBranches.contains(b.getLineNumber()), message);
            } else {
                assertTrue(missedBranches.contains(b.getLineNumber()), message);
            }
        }
    }

}
