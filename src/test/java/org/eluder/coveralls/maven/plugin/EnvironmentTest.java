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
package org.eluder.coveralls.maven.plugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.maven.plugin.logging.Log;
import org.eluder.coveralls.maven.plugin.service.ServiceSetup;
import org.eluder.coveralls.maven.plugin.source.SourceLoader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * The Class EnvironmentTest.
 */
@ExtendWith(MockitoExtension.class)
class EnvironmentTest {

    /** The mojo. */
    CoverallsReportMojo mojo;

    /** The coverage parser mock. */
    @Mock
    CoverageParser coverageParserMock;

    /** The log mock. */
    @Mock
    Log logMock;

    /** The service mock. */
    @Mock
    ServiceSetup serviceMock;

    /**
     * Inits the.
     */
    @BeforeEach
    void init() {
        this.mojo = new CoverallsReportMojo() {
            @Override
            protected List<CoverageParser> createCoverageParsers(SourceLoader sourceLoader) {
                return Arrays.asList(EnvironmentTest.this.coverageParserMock);
            }

            @Override
            public Log getLog() {
                return EnvironmentTest.this.logMock;
            }
        };
        this.mojo.serviceName = "service";
        this.mojo.sourceEncoding = "UTF-8";
        Mockito.lenient().when(this.serviceMock.isSelected()).thenReturn(true);
    }

    /**
     * Missing mojo.
     */
    @Test
    void missingMojo() {
        final var mockList = Arrays.asList(this.serviceMock);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new Environment(null, mockList);
        });
    }

    /**
     * Missing services.
     */
    @Test
    void missingServices() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new Environment(this.mojo, null);
        });
    }

    /**
     * Setup without services.
     */
    @Test
    void setupWithoutServices() {
        this.create(Collections.<ServiceSetup> emptyList()).setup();
        Assertions.assertEquals("service", this.mojo.serviceName);
    }

    /**
     * Setup without source encoding.
     */
    @Test
    void setupWithoutSourceEncoding() {
        this.mojo.sourceEncoding = null;
        final var list = Arrays.asList(this.serviceMock);
        final var environment = this.create(list);
        Assertions.assertThrows(IllegalArgumentException.class, environment::setup);
    }

    /**
     * Setup with incomplete job.
     */
    @Test
    void setupWithIncompleteJob() {
        Mockito.when(this.serviceMock.getJobId()).thenReturn("");
        Mockito.when(this.serviceMock.getBuildUrl()).thenReturn("  ");

        this.create(Arrays.asList(this.serviceMock)).setup();
        Assertions.assertEquals("service", this.mojo.serviceName);
        Assertions.assertNull(this.mojo.serviceJobId);
        Assertions.assertNull(this.mojo.serviceBuildNumber);
        Assertions.assertNull(this.mojo.serviceBuildUrl);
        Assertions.assertNull(this.mojo.branch);
        Assertions.assertNull(this.mojo.pullRequest);
        Assertions.assertNull(this.mojo.serviceEnvironment);
    }

    /**
     * Setup with complete job.
     */
    @Test
    void setupWithCompleteJob() {
        this.mojo.serviceName = null;
        final var environment = new Properties();
        environment.setProperty("env", "true");
        Mockito.when(this.serviceMock.getName()).thenReturn("defined service");
        Mockito.when(this.serviceMock.getJobId()).thenReturn("123");
        Mockito.when(this.serviceMock.getBuildNumber()).thenReturn("456");
        Mockito.when(this.serviceMock.getBuildUrl()).thenReturn("https://ci.com/project");
        Mockito.when(this.serviceMock.getBranch()).thenReturn("master");
        Mockito.when(this.serviceMock.getPullRequest()).thenReturn("111");
        Mockito.when(this.serviceMock.getEnvironment()).thenReturn(environment);

        this.create(Arrays.asList(Mockito.mock(ServiceSetup.class), this.serviceMock)).setup();
        Assertions.assertEquals("defined service", this.mojo.serviceName);
        Assertions.assertEquals("123", this.mojo.serviceJobId);
        Assertions.assertEquals("456", this.mojo.serviceBuildNumber);
        Assertions.assertEquals("https://ci.com/project", this.mojo.serviceBuildUrl);
        Assertions.assertEquals("master", this.mojo.branch);
        Assertions.assertEquals("111", this.mojo.pullRequest);
        Assertions.assertEquals("true", this.mojo.serviceEnvironment.get("env"));
    }

    /**
     * Setup without job override.
     */
    @Test
    void setupWithoutJobOverride() {
        final var environment = new Properties();
        environment.setProperty("env", "true");
        final var serviceEnvironment = new Properties();
        serviceEnvironment.setProperty("env", "setProperty");
        Mockito.when(this.serviceMock.getName()).thenReturn("defined service");
        Mockito.when(this.serviceMock.getJobId()).thenReturn("123");
        Mockito.when(this.serviceMock.getBuildNumber()).thenReturn("456");
        Mockito.when(this.serviceMock.getBuildUrl()).thenReturn("https://ci.com/project");
        Mockito.when(this.serviceMock.getBranch()).thenReturn("master");
        Mockito.when(this.serviceMock.getPullRequest()).thenReturn("111");
        Mockito.when(this.serviceMock.getEnvironment()).thenReturn(environment);
        this.mojo.serviceJobId = "setJobId";
        this.mojo.serviceBuildNumber = "setBuildNumber";
        this.mojo.serviceBuildUrl = "setBuildUrl";
        this.mojo.serviceEnvironment = serviceEnvironment;
        this.mojo.branch = "setBranch";
        this.mojo.pullRequest = "setPullRequest";

        this.create(Arrays.asList(this.serviceMock)).setup();

        Assertions.assertEquals("service", this.mojo.serviceName);
        Assertions.assertEquals("setJobId", this.mojo.serviceJobId);
        Assertions.assertEquals("setBuildNumber", this.mojo.serviceBuildNumber);
        Assertions.assertEquals("setBuildUrl", this.mojo.serviceBuildUrl);
        Assertions.assertEquals("setBranch", this.mojo.branch);
        Assertions.assertEquals("setPullRequest", this.mojo.pullRequest);
        Assertions.assertEquals("setProperty", this.mojo.serviceEnvironment.get("env"));
    }

    /**
     * Creates the.
     *
     * @param services
     *            the services
     *
     * @return the environment
     */
    Environment create(final Iterable<ServiceSetup> services) {
        return new Environment(this.mojo, services);
    }
}
