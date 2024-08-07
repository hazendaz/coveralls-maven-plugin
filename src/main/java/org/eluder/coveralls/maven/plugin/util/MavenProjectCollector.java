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
package org.eluder.coveralls.maven.plugin.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.maven.project.MavenProject;

public class MavenProjectCollector {

    private final MavenProject root;

    public MavenProjectCollector(final MavenProject root) {
        this.root = root;
    }

    public List<MavenProject> collect() {
        List<MavenProject> projects = new ArrayList<>();
        collect(root, projects);
        return Collections.unmodifiableList(projects);
    }

    private void collect(final MavenProject project, final List<MavenProject> projects) {
        projects.add(project);
        for (MavenProject child : project.getCollectedProjects()) {
            collect(child, projects);
        }
    }
}
