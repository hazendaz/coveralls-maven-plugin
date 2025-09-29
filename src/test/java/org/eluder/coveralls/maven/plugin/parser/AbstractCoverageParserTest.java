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
package org.eluder.coveralls.maven.plugin.parser;

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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

/**
 * The Class AbstractCoverageParserTest.
 */
@ExtendWith(MockitoExtension.class)
public abstract class AbstractCoverageParserTest {

    /** The source loader mock. */
    @Mock
    SourceLoader sourceLoaderMock;

    /** The source callback mock. */
    @Mock
    SourceCallback sourceCallbackMock;

    /**
     * Instantiates a new abstract coverage parser test.
     */
    public AbstractCoverageParserTest() {
        // Do Nothing
    }

    /**
     * Inits the.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @BeforeEach
    void init() throws IOException {
        for (final List<String> coverageFile : this.getCoverageFixture()) {
            final var name = this.sourceName(coverageFile.get(0));
            final var content = TestIoUtil.readFileContent(TestIoUtil.getFile(name));
            Mockito.lenient().when(this.sourceLoaderMock.load(name)).then(this.sourceAnswer(name, content));
        }
    }

    /**
     * Source name.
     *
     * @param coverageFile
     *            the coverage file
     *
     * @return the string
     */
    String sourceName(final String coverageFile) {
        return coverageFile;
    }

    /**
     * Source answer.
     *
     * @param name
     *            the name
     * @param content
     *            the content
     *
     * @return the answer
     */
    Answer<Source> sourceAnswer(final String name, final String content) {
        return invocation -> new Source(name, content, TestIoUtil.getSha512DigestHex(content));
    }

    /**
     * Parses the coverage.
     *
     * @throws ProcessingException
     *             the processing exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void parseCoverage() throws ProcessingException, IOException {
        for (final String coverageResource : this.getCoverageResources()) {
            final var parser = this.createCoverageParser(TestIoUtil.getFile(coverageResource), this.sourceLoaderMock);
            parser.parse(this.sourceCallbackMock);
        }

        final var fixture = this.getCoverageFixture();

        final ArgumentCaptor<Source> captor = ArgumentCaptor.forClass(Source.class);
        Mockito.verify(this.sourceCallbackMock, Mockito.atLeast(CoverageFixture.getTotalFiles(fixture)))
                .onSource(captor.capture());

        final var sourceCollector = new SourceCollector();
        final var uniqueSourceCallback = new UniqueSourceCallback(sourceCollector);
        final var classifierRemover = new ClassifierRemover(uniqueSourceCallback);
        classifierRemover.onBegin();
        for (final Source source : captor.getAllValues()) {
            classifierRemover.onSource(source);
        }
        classifierRemover.onComplete();

        for (final List<String> coverageFile : fixture) {
            AbstractCoverageParserTest.assertCoverage(sourceCollector.sources, coverageFile.get(0),
                    Integer.parseInt(coverageFile.get(1)), this.toIntegerSet(coverageFile.get(2)),
                    this.toIntegerSet(coverageFile.get(3)), this.toIntegerSet(coverageFile.get(4)),
                    this.toIntegerSet(coverageFile.get(5)));
        }
    }

    /**
     * Creates the coverage parser.
     *
     * @param coverageFile
     *            the coverage file
     * @param sourceLoader
     *            the source loader
     *
     * @return the coverage parser
     */
    protected abstract CoverageParser createCoverageParser(File coverageFile, SourceLoader sourceLoader);

    /**
     * Gets the coverage resources.
     *
     * @return the coverage resources
     */
    protected abstract List<String> getCoverageResources();

    /**
     * Gets the coverage fixture.
     *
     * @return the coverage fixture
     */
    protected abstract List<List<String>> getCoverageFixture();

    /**
     * To integer set.
     *
     * @param commaSeparated
     *            the comma separated
     *
     * @return the sets the
     */
    Set<Integer> toIntegerSet(final String commaSeparated) {
        if (commaSeparated.isEmpty()) {
            return Collections.emptySet();
        }
        final var split = commaSeparated.split(",", -1);
        final Set<Integer> values = new HashSet<>();
        for (final String value : split) {
            values.add(Integer.valueOf(value));
        }
        return values;
    }

    /**
     * The Class SourceCollector.
     */
    static class SourceCollector implements SourceCallback {

        /** The sources. */
        private final List<Source> sources = new ArrayList<>();

        @Override
        public void onBegin() {
            // Does nothing
        }

        @Override
        public void onSource(Source source) {
            this.sources.add(source);
        }

        @Override
        public void onComplete() {
            // Does Nothing
        }
    }

    /**
     * The Class ClassifierRemover.
     */
    static class ClassifierRemover extends ChainingSourceCallback {

        /**
         * Instantiates a new classifier remover.
         *
         * @param chained
         *            the chained
         */
        public ClassifierRemover(SourceCallback chained) {
            super(chained);
        }

        @Override
        protected void onSourceInternal(Source source) {
            source.setClassifier(null);
        }
    }

    /**
     * Assert coverage.
     *
     * @param sources
     *            the sources
     * @param name
     *            the name
     * @param lines
     *            the lines
     * @param coveredLines
     *            the covered lines
     * @param missedLines
     *            the missed lines
     * @param coveredBranches
     *            the covered branches
     * @param missedBranches
     *            the missed branches
     */
    static void assertCoverage(final Collection<Source> sources, final String name, final int lines,
            final Set<Integer> coveredLines, final Set<Integer> missedLines, final Set<Integer> coveredBranches,
            final Set<Integer> missedBranches) {

        Source tested = null;
        for (final Source source : sources) {
            if (source.getName().endsWith(name)) {
                tested = source;
                break;
            }
        }
        if (tested == null) {
            Assertions.fail("Expected source " + name + " not found from coverage report");
        }
        if (tested.getCoverage().length != lines) {
            Assertions.fail("Expected " + lines + " lines for " + name + " was " + tested.getCoverage().length);
        }
        for (var i = 0; i < tested.getCoverage().length; i++) {
            final var lineNumber = i + 1;
            final var message = name + " line " + lineNumber + " coverage";
            if (coveredLines.contains(lineNumber)) {
                Assertions.assertTrue(tested.getCoverage()[i] > 0, message);
            } else if (missedLines.contains(lineNumber)) {
                Assertions.assertEquals(0, tested.getCoverage()[i], message);
            } else {
                Assertions.assertNull(tested.getCoverage()[i], message);
            }
        }
        for (final Branch b : tested.getBranchesList()) {
            final var message = name + " branch " + b.getBranchNumber() + " coverage in line " + b.getLineNumber();
            if (b.getHits() > 0) {
                Assertions.assertTrue(coveredBranches.contains(b.getLineNumber()), message);
            } else {
                Assertions.assertTrue(missedBranches.contains(b.getLineNumber()), message);
            }
        }
    }

}
