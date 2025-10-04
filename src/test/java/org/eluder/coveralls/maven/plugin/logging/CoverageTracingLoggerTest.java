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
package org.eluder.coveralls.maven.plugin.logging;

import java.io.IOException;

import org.apache.maven.plugin.logging.Log;
import org.eluder.coveralls.maven.plugin.ProcessingException;
import org.eluder.coveralls.maven.plugin.domain.Source;
import org.eluder.coveralls.maven.plugin.logging.Logger.Position;
import org.eluder.coveralls.maven.plugin.source.SourceCallback;
import org.eluder.coveralls.maven.plugin.source.UniqueSourceCallback;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * The Class CoverageTracingLoggerTest.
 */
@ExtendWith(MockitoExtension.class)
class CoverageTracingLoggerTest {

    /** The log mock. */
    @Mock
    Log logMock;

    /** The source callback mock. */
    @Mock
    SourceCallback sourceCallbackMock;

    /**
     * Constructor with null.
     */
    @Test
    void constructorWithNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new CoverageTracingLogger(null);
        });
    }

    /**
     * Test get position.
     */
    @Test
    void position() {
        Assertions.assertEquals(Position.AFTER, new CoverageTracingLogger(this.sourceCallbackMock).getPosition());
    }

    /**
     * Log for sources.
     *
     * @throws ProcessingException
     *             the processing exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void logForSources() throws ProcessingException, IOException {
        final var source1 = new Source("Source1.java", "public class Source1 {\n  if(true) { }\n}\n",
                "FE0538639E8CE73733E77659C1043B5C");
        source1.addCoverage(1, 0);
        source1.addCoverage(2, 0);
        source1.addCoverage(3, 0);
        source1.addBranchCoverage(2, 0, 0, 3);
        source1.addBranchCoverage(2, 0, 1, 0);
        final var source2 = new Source("Source2.java",
                "public class Source2 {\n    new Interface() { public void run() { } };\n}\n",
                "34BD6501A6D1CE5181AECEA688C7D382");
        source2.addCoverage(1, 1);
        source2.addCoverage(3, 1);
        final var source2inner = new Source("Source2.java",
                "public class Source2 {\n    new Interface() { public void run() { } };\n}\n",
                "34BD6501A6D1CE5181AECEA688C7D382");
        source2inner.setClassifier("$1");
        source2inner.addCoverage(2, 1);

        final var coverageTracingLogger = new CoverageTracingLogger(this.sourceCallbackMock);
        final var uniqueSourceCallback = new UniqueSourceCallback(coverageTracingLogger);
        uniqueSourceCallback.onSource(source1);
        uniqueSourceCallback.onSource(source2);
        uniqueSourceCallback.onSource(source2inner);
        uniqueSourceCallback.onComplete();
        coverageTracingLogger.log(this.logMock);

        Assertions.assertEquals(8, coverageTracingLogger.getLines());
        Assertions.assertEquals(6, coverageTracingLogger.getRelevant());
        Assertions.assertEquals(3, coverageTracingLogger.getCovered());
        Assertions.assertEquals(3, coverageTracingLogger.getMissed());
        Assertions.assertEquals(2, coverageTracingLogger.getBranches());
        Assertions.assertEquals(1, coverageTracingLogger.getCoveredBranches());
        Assertions.assertEquals(1, coverageTracingLogger.getMissedBranches());
        Mockito.verify(this.sourceCallbackMock, Mockito.times(2)).onSource(ArgumentMatchers.any(Source.class));
        Mockito.verify(this.logMock).info("Gathered code coverage metrics for 2 source files with 8 lines of code:");
        Mockito.verify(this.logMock).info("- 6 relevant lines");
        Mockito.verify(this.logMock).info("- 3 covered lines");
        Mockito.verify(this.logMock).info("- 3 missed lines");
        Mockito.verify(this.logMock).info("- 2 branches");
        Mockito.verify(this.logMock).info("- 2 branches");
        Mockito.verify(this.logMock).info("- 1 covered branches");
        Mockito.verify(this.logMock).info("- 1 missed branches");
    }

}
