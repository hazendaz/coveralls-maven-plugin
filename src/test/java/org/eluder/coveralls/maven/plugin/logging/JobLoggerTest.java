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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.maven.plugin.logging.Log;
import org.eluder.coveralls.maven.plugin.domain.Git;
import org.eluder.coveralls.maven.plugin.domain.Git.Head;
import org.eluder.coveralls.maven.plugin.domain.Job;
import org.eluder.coveralls.maven.plugin.logging.Logger.Position;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * The Class JobLoggerTest.
 */
@ExtendWith(MockitoExtension.class)
class JobLoggerTest {

    /** The job mock. */
    @Mock
    Job jobMock;

    /** The log mock. */
    @Mock
    Log logMock;

    /** The json mapper mock. */
    @Mock
    ObjectMapper jsonMapperMock;

    /**
     * Missing job.
     */
    @Test
    void missingJob() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new JobLogger(null));
    }

    /**
     * Test get position.
     */
    @Test
    void position() {
        Assertions.assertEquals(Position.BEFORE, new JobLogger(this.jobMock).getPosition());
    }

    /**
     * Log job with id.
     */
    @Test
    void logJobWithId() {
        final var git = new Git(null, new Head("ab679cf2d81ac", null, null, null, null, null), "master", null);
        Mockito.when(this.jobMock.getServiceName()).thenReturn("service");
        Mockito.when(this.jobMock.getServiceJobId()).thenReturn("666");
        Mockito.when(this.jobMock.getRepoToken()).thenReturn("123456789");
        Mockito.when(this.jobMock.isDryRun()).thenReturn(true);
        Mockito.when(this.jobMock.getGit()).thenReturn(git);

        new JobLogger(this.jobMock).log(this.logMock);

        Mockito.verify(this.logMock).info("Starting Coveralls job for service (666) in dry run mode");
        Mockito.verify(this.logMock).info("Using repository token <secret>");
        Mockito.verify(this.logMock).info("Git commit ab679cf in master");
        Mockito.verify(this.logMock).isDebugEnabled();
        Mockito.verifyNoMoreInteractions(this.logMock);
    }

    /**
     * Log with build number and url.
     */
    @Test
    void logWithBuildNumberAndUrl() {
        Mockito.when(this.jobMock.getServiceName()).thenReturn("service");
        Mockito.when(this.jobMock.getServiceBuildNumber()).thenReturn("10");
        Mockito.when(this.jobMock.getServiceBuildUrl()).thenReturn("https://ci.com/build/10");

        new JobLogger(this.jobMock).log(this.logMock);

        Mockito.verify(this.logMock).info("Starting Coveralls job for service (10 / https://ci.com/build/10)");
        Mockito.verify(this.logMock).isDebugEnabled();
        Mockito.verifyNoMoreInteractions(this.logMock);
    }

    /**
     * Log dry run.
     */
    @Test
    void logDryRun() {
        Mockito.when(this.jobMock.isDryRun()).thenReturn(true);

        new JobLogger(this.jobMock).log(this.logMock);

        Mockito.verify(this.logMock).info("Starting Coveralls job in dry run mode");
        Mockito.verify(this.logMock).isDebugEnabled();
        Mockito.verifyNoMoreInteractions(this.logMock);
    }

    /**
     * Log parallel.
     */
    @Test
    void logParallel() {
        Mockito.when(this.jobMock.isParallel()).thenReturn(true);

        new JobLogger(this.jobMock).log(this.logMock);

        Mockito.verify(this.logMock).info("Starting Coveralls job with parallel option enabled");
        Mockito.verify(this.logMock).isDebugEnabled();
        Mockito.verifyNoMoreInteractions(this.logMock);

    }

    /**
     * Log job with debug.
     *
     * @throws JsonProcessingException
     *             the json processing exception
     */
    @Test
    void logJobWithDebug() throws JsonProcessingException {
        Mockito.when(this.logMock.isDebugEnabled()).thenReturn(true);
        Mockito.when(this.jobMock.getServiceName()).thenReturn("service");
        Mockito.when(this.jsonMapperMock.writeValueAsString(ArgumentMatchers.same(this.jobMock)))
                .thenReturn("{\"serviceName\":\"service\"}");

        new JobLogger(this.jobMock, this.jsonMapperMock).log(this.logMock);

        Mockito.verify(this.logMock).info("Starting Coveralls job for service");
        Mockito.verify(this.logMock).isDebugEnabled();
        Mockito.verify(this.logMock).debug("Complete Job description:\n{\"serviceName\":\"service\"}");
        Mockito.verifyNoMoreInteractions(this.logMock);
    }

    /**
     * Log job with error in debug.
     *
     * @throws JsonProcessingException
     *             the json processing exception
     */
    @Test
    void logJobWithErrorInDebug() throws JsonProcessingException {
        Mockito.when(this.logMock.isDebugEnabled()).thenReturn(true);
        Mockito.when(this.jobMock.getServiceName()).thenReturn("service");
        Mockito.when(this.jsonMapperMock.writeValueAsString(ArgumentMatchers.same(this.jobMock)))
                .thenThrow(JsonProcessingException.class);

        final var jobLogger = new JobLogger(this.jobMock, this.jsonMapperMock);
        Assertions.assertThrows(RuntimeException.class, () -> jobLogger.log(this.logMock));
    }

}
