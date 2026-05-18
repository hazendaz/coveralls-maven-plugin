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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * The Class Git.
 */
public class Git implements JsonObject {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The base dir. */
    @JsonIgnore
    private final File baseDir;

    /** The head. */
    @JsonProperty("head")
    private final Head head;

    /** The branch. */
    @JsonProperty("branch")
    private final String branch;

    /** The remotes. */
    @JsonProperty("remotes")
    private final List<Remote> remotes;

    /**
     * Instantiates a new git.
     *
     * @param baseDir
     *            the base dir
     * @param head
     *            the head
     * @param branch
     *            the branch
     * @param remotes
     *            the remotes
     */
    public Git(final File baseDir, final Head head, final String branch, final List<Remote> remotes) {
        this.baseDir = baseDir;
        this.head = head;
        this.branch = branch;
        this.remotes = remotes;
    }

    /**
     * Gets the base dir.
     *
     * @return the base dir
     */
    public File getBaseDir() {
        return this.baseDir;
    }

    /**
     * Gets the head.
     *
     * @return the head
     */
    public Head getHead() {
        return this.head;
    }

    /**
     * Gets the branch.
     *
     * @return the branch
     */
    public String getBranch() {
        return this.branch;
    }

    /**
     * Gets the remotes.
     *
     * @return the remotes
     */
    public List<Remote> getRemotes() {
        return this.remotes;
    }

    /**
     * The Class Head.
     */
    public static class Head implements Serializable {

        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 1L;

        /** The id. */
        @JsonProperty("id")
        private final String id;

        /** The author name. */
        @JsonProperty("author_name")
        private final String authorName;

        /** The author email. */
        @JsonProperty("author_email")
        private final String authorEmail;

        /** The committer name. */
        @JsonProperty("committer_name")
        private final String committerName;

        /** The committer email. */
        @JsonProperty("committer_email")
        private final String committerEmail;

        /** The message. */
        @JsonProperty("message")
        private final String message;

        /**
         * Instantiates a new head.
         *
         * @param id
         *            the id
         * @param authorName
         *            the author name
         * @param authorEmail
         *            the author email
         * @param committerName
         *            the committer name
         * @param committerEmail
         *            the committer email
         * @param message
         *            the message
         */
        public Head(final String id, final String authorName, final String authorEmail, final String committerName,
                final String committerEmail, final String message) {
            this.id = id;
            this.authorName = authorName;
            this.authorEmail = authorEmail;
            this.committerName = committerName;
            this.committerEmail = committerEmail;
            this.message = message;
        }

        /**
         * Gets the id.
         *
         * @return the id
         */
        public String getId() {
            return this.id;
        }

        /**
         * Gets the author name.
         *
         * @return the author name
         */
        public String getAuthorName() {
            return this.authorName;
        }

        /**
         * Gets the author email.
         *
         * @return the author email
         */
        public String getAuthorEmail() {
            return this.authorEmail;
        }

        /**
         * Gets the committer name.
         *
         * @return the committer name
         */
        public String getCommitterName() {
            return this.committerName;
        }

        /**
         * Gets the committer email.
         *
         * @return the committer email
         */
        public String getCommitterEmail() {
            return this.committerEmail;
        }

        /**
         * Gets the message.
         *
         * @return the message
         */
        public String getMessage() {
            return this.message;
        }
    }

    /**
     * The Class Remote.
     */
    public static class Remote implements Serializable {

        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 1L;

        /** The name. */
        @JsonProperty("name")
        private final String name;

        /** The url. */
        @JsonProperty("url")
        private final String url;

        /**
         * Instantiates a new remote.
         *
         * @param name
         *            the name
         * @param url
         *            the url
         */
        public Remote(final String name, final String url) {
            this.name = name;
            this.url = url;
        }

        /**
         * Gets the name.
         *
         * @return the name
         */
        public String getName() {
            return this.name;
        }

        /**
         * Gets the url.
         *
         * @return the url
         */
        public String getUrl() {
            return this.url;
        }
    }
}
