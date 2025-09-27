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
package org.eluder.coveralls.maven.plugin.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eluder.coveralls.maven.plugin.ProcessingException;
import org.eluder.coveralls.maven.plugin.domain.Git;
import org.eluder.coveralls.maven.plugin.domain.Job;
import org.eluder.coveralls.maven.plugin.domain.Source;
import org.eluder.coveralls.maven.plugin.util.TestIoUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;

/**
 * The Class JsonWriterTest.
 */
class JsonWriterTest {

    /** The Constant TEST_TIME. */
    static final long TEST_TIME = 1357009200000L;

    /** The folder. */
    @TempDir(cleanup = CleanupMode.NEVER)
    Path folder;

    /** The file. */
    File file;

    /**
     * Inits the.
     */
    @BeforeEach
    void init() {
        file = folder.resolve("file").toFile();
    }

    /**
     * Sub directory creation.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void subDirectoryCreation() throws IOException {
        var f = folder.resolve("sub1").resolve("sub2");
        var job = job();
        try (var writer = new JsonWriter(job, f.toFile())) {
            assertTrue(writer.getCoverallsFile().getParentFile().isDirectory());
        }
    }

    /**
     * Test get job.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    @SuppressWarnings("resource")
    void testGetJob() throws IOException {
        var job = job();
        assertSame(job, new JsonWriter(job, file).getJob());
    }

    /**
     * Test get coveralls file.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    @SuppressWarnings("resource")
    void testGetCoverallsFile() throws IOException {
        var job = job();
        assertSame(file, new JsonWriter(job, file).getCoverallsFile());
    }

    /**
     * Write start and end.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ProcessingException
     *             the processing exception
     */
    @SuppressWarnings("rawtypes")
    @Test
    void writeStartAndEnd() throws IOException, ProcessingException {
        try (var writer = new JsonWriter(job(), file)) {
            writer.onBegin();
            writer.onComplete();
        }
        var content = TestIoUtil.readFileContent(file);
        var jsonMap = stringToJsonMap(content);
        assertEquals("service", jsonMap.get("service_name"));
        assertEquals("job123", jsonMap.get("service_job_id"));
        assertEquals("build5", jsonMap.get("service_number"));
        assertEquals("http://ci.com/build5", jsonMap.get("service_build_url"));
        assertEquals("foobar", ((Map) jsonMap.get("environment")).get("custom_property"));
        assertEquals("master", jsonMap.get("service_branch"));
        assertEquals("pull10", jsonMap.get("service_pull_request"));
        assertEquals(new SimpleDateFormat(JsonWriter.TIMESTAMP_FORMAT).format(new Date(TEST_TIME)),
                jsonMap.get("run_at"));
        assertEquals("af456fge34acd", ((Map) jsonMap.get("git")).get("branch"));
        assertEquals("aefg837fge", ((Map) ((Map) jsonMap.get("git")).get("head")).get("id"));
        assertEquals(0, ((Collection<?>) jsonMap.get("source_files")).size());
    }

    /**
     * Test on source.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ProcessingException
     *             the processing exception
     */
    @Test
    void testOnSource() throws IOException, ProcessingException {
        try (var writer = new JsonWriter(job(), file)) {
            writer.onBegin();
            writer.onSource(source());
            writer.onComplete();
        }
        var content = TestIoUtil.readFileContent(file);
        var jsonMap = stringToJsonMap(content);
        if (jsonMap.get("source_files") instanceof List) {
            jsonMap = ((List<Map<String, Object>>) jsonMap.get("source_files")).get(0);
        }
        assertEquals("Foo.java", jsonMap.get("name"));
        assertEquals("6E0F89B516198DC6AB743EA5FBFB3108", jsonMap.get("source_digest"));
        assertEquals(1, ((Collection<?>) jsonMap.get("coverage")).size());
    }

    /**
     * Job.
     *
     * @return the job
     */
    Job job() {
        var head = new Git.Head("aefg837fge", "john", "john@mail.com", "john", "john@mail.com", "test commit");
        var remote = new Git.Remote("origin", "git@git.com:foo.git");
        var environment = new Properties();
        environment.setProperty("custom_property", "foobar");
        return new Job().withServiceName("service").withServiceJobId("job123").withServiceBuildNumber("build5")
                .withServiceBuildUrl("http://ci.com/build5").withServiceEnvironment(environment).withBranch("master")
                .withPullRequest("pull10").withTimestamp(TEST_TIME)
                .withGit(new Git(null, head, "af456fge34acd", Arrays.asList(remote)));
    }

    /**
     * Source.
     *
     * @return the source
     */
    Source source() {
        return new Source("Foo.java", "public class Foo { }", "6E0F89B516198DC6AB743EA5FBFB3108");
    }

    /**
     * String to json map.
     *
     * @param content
     *            the content
     *
     * @return the map
     *
     * @throws JsonProcessingException
     *             the json processing exception
     */
    Map<String, Object> stringToJsonMap(final String content) throws JsonProcessingException {
        var mapper = new ObjectMapper();
        var type = mapper.getTypeFactory().constructMapType(HashMap.class, String.class, Object.class);
        return mapper.readValue(content, type);
    }
}
