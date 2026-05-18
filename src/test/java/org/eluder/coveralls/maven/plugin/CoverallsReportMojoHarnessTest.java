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
package org.eluder.coveralls.maven.plugin;

import org.apache.maven.plugin.testing.junit5.InjectMojo;
import org.apache.maven.plugin.testing.junit5.MojoExtension;
import org.apache.maven.plugin.testing.junit5.MojoParameter;
import org.apache.maven.plugin.testing.junit5.MojoTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link CoverallsReportMojo} using the maven-plugin-testing-harness.
 * <p>
 * This provides more realistic testing by using actual Maven infrastructure to look up and configure the Mojo, rather
 * than relying solely on mocks. The harness reads the generated plugin descriptor and configures the Mojo using Plexus
 * dependency injection, closely simulating what Maven does at runtime.
 */
@MojoTest
class CoverallsReportMojoHarnessTest {

    /** The test POM path, relative to the project basedir. */
    private static final String TEST_POM = "src/test/resources/unit/test-pom.xml";

    /**
     * Verify the mojo can be looked up by goal name using the testing harness.
     * <p>
     * This confirms the plugin descriptor is correctly generated and the mojo is properly registered as a Plexus
     * component.
     *
     * @param mojo
     *            the injected mojo
     */
    @Test
    void testMojoLookupWithHarness(@InjectMojo(goal = "report", pom = TEST_POM) CoverallsReportMojo mojo) {
        Assertions.assertThat(mojo).isNotNull();
    }

    /**
     * Verify that the repoToken parameter is correctly injected from the test POM configuration.
     *
     * @param mojo
     *            the injected mojo
     *
     * @throws IllegalAccessException
     *             if field access via reflection fails
     */
    @Test
    void testRepoTokenConfigured(@InjectMojo(goal = "report", pom = TEST_POM) CoverallsReportMojo mojo)
            throws IllegalAccessException {
        Assertions.assertThat(MojoExtension.getVariableValueFromObject(mojo, "repoToken")).isEqualTo("test-token");
    }

    /**
     * Verify that the dryRun parameter is correctly injected from the test POM configuration.
     *
     * @param mojo
     *            the injected mojo
     *
     * @throws IllegalAccessException
     *             if field access via reflection fails
     */
    @Test
    void testDryRunConfigured(@InjectMojo(goal = "report", pom = TEST_POM) CoverallsReportMojo mojo)
            throws IllegalAccessException {
        Assertions.assertThat(MojoExtension.getVariableValueFromObject(mojo, "dryRun")).isEqualTo(Boolean.TRUE);
    }

    /**
     * Verify that the sourceEncoding parameter is correctly injected from the test POM configuration.
     *
     * @param mojo
     *            the injected mojo
     *
     * @throws IllegalAccessException
     *             if field access via reflection fails
     */
    @Test
    void testSourceEncodingConfigured(@InjectMojo(goal = "report", pom = TEST_POM) CoverallsReportMojo mojo)
            throws IllegalAccessException {
        Assertions.assertThat(MojoExtension.getVariableValueFromObject(mojo, "sourceEncoding")).isEqualTo("UTF-8");
    }

    /**
     * Verify that the serviceName parameter is correctly injected from the test POM configuration.
     *
     * @param mojo
     *            the injected mojo
     *
     * @throws IllegalAccessException
     *             if field access via reflection fails
     */
    @Test
    void testServiceNameConfigured(@InjectMojo(goal = "report", pom = TEST_POM) CoverallsReportMojo mojo)
            throws IllegalAccessException {
        Assertions.assertThat(MojoExtension.getVariableValueFromObject(mojo, "serviceName"))
                .isEqualTo("test-ci-service");
    }

    /**
     * Verify that the failOnServiceError parameter has the correct default value of {@code true} as declared in the
     * plugin descriptor.
     *
     * @param mojo
     *            the injected mojo
     *
     * @throws IllegalAccessException
     *             if field access via reflection fails
     */
    @Test
    void testDefaultFailOnServiceError(@InjectMojo(goal = "report", pom = TEST_POM) CoverallsReportMojo mojo)
            throws IllegalAccessException {
        Assertions.assertThat(MojoExtension.getVariableValueFromObject(mojo, "failOnServiceError"))
                .isEqualTo(Boolean.TRUE);
    }

    /**
     * Verify that the skip parameter has the correct default value of {@code false} as declared in the plugin
     * descriptor.
     *
     * @param mojo
     *            the injected mojo
     *
     * @throws IllegalAccessException
     *             if field access via reflection fails
     */
    @Test
    void testDefaultSkipIsFalse(@InjectMojo(goal = "report", pom = TEST_POM) CoverallsReportMojo mojo)
            throws IllegalAccessException {
        Assertions.assertThat(MojoExtension.getVariableValueFromObject(mojo, "skip")).isEqualTo(Boolean.FALSE);
    }

    /**
     * Verify that the {@code @MojoParameter} annotation can be used to inject individual mojo parameters without
     * specifying them in the test POM.
     *
     * @param mojo
     *            the injected mojo
     *
     * @throws IllegalAccessException
     *             if field access via reflection fails
     */
    @Test
    @MojoParameter(name = "serviceJobId", value = "build-42")
    void testMojoParameterAnnotationOverride(@InjectMojo(goal = "report", pom = TEST_POM) CoverallsReportMojo mojo)
            throws IllegalAccessException {
        Assertions.assertThat(MojoExtension.getVariableValueFromObject(mojo, "serviceJobId")).isEqualTo("build-42");
    }

}
