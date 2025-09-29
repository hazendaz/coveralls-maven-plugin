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
package org.eluder.coveralls.maven.plugin.domain;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.eluder.coveralls.maven.plugin.domain.Git.Head;
import org.eluder.coveralls.maven.plugin.domain.Git.Remote;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * The Class JobTest.
 */
class JobTest {

    /**
     * Gets the branch with remote.
     */
    @Test
    void getBranchWithRemote() {
        final List<Remote> remotes = Arrays.asList(new Remote("origin", "git@github.com"));
        final var git = new Git(Path.of(".").toFile(), new Head(null, null, null, null, null, null), "master", remotes);
        final var job = new Job().withBranch("origin/master").withGit(git);
        Assertions.assertEquals(".", git.getBaseDir().getPath());
        Assertions.assertEquals("master", job.getBranch());
    }

    /**
     * Test get branch.
     */
    @Test
    void testGetBranch() {
        final var job = new Job().withBranch("master");
        Assertions.assertEquals("master", job.getBranch());
    }

}
