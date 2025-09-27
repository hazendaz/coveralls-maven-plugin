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
package org.eluder.coveralls.maven.plugin.domain;

import java.util.Properties;

import org.eluder.coveralls.maven.plugin.domain.Git.Remote;
import org.eluder.coveralls.maven.plugin.validation.JobValidator;
import org.eluder.coveralls.maven.plugin.validation.ValidationErrors;

/**
 * The Class Job.
 */
public class Job {

    /** The repo token. */
    private String repoToken;

    /** The service name. */
    private String serviceName;

    /** The service job id. */
    private String serviceJobId;

    /** The service build number. */
    private String serviceBuildNumber;

    /** The service build url. */
    private String serviceBuildUrl;

    /** The parallel. */
    private boolean parallel;

    /** The service environment. */
    private Properties serviceEnvironment;

    /** The timestamp. */
    private Long timestamp;

    /** The dry run. */
    private boolean dryRun;

    /** The branch. */
    private String branch;

    /** The pull request. */
    private String pullRequest;

    /** The git. */
    private Git git;

    /**
     * Instantiates a new job.
     */
    public Job() {
        // noop
    }

    /**
     * With repo token.
     *
     * @param repoToken
     *            the repo token
     *
     * @return the job
     */
    public Job withRepoToken(final String repoToken) {
        this.repoToken = repoToken;
        return this;
    }

    /**
     * With service name.
     *
     * @param serviceName
     *            the service name
     *
     * @return the job
     */
    public Job withServiceName(final String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    /**
     * With service job id.
     *
     * @param serviceJobId
     *            the service job id
     *
     * @return the job
     */
    public Job withServiceJobId(final String serviceJobId) {
        this.serviceJobId = serviceJobId;
        return this;
    }

    /**
     * With service build number.
     *
     * @param serviceBuildNumber
     *            the service build number
     *
     * @return the job
     */
    public Job withServiceBuildNumber(final String serviceBuildNumber) {
        this.serviceBuildNumber = serviceBuildNumber;
        return this;
    }

    /**
     * With service build url.
     *
     * @param serviceBuildUrl
     *            the service build url
     *
     * @return the job
     */
    public Job withServiceBuildUrl(final String serviceBuildUrl) {
        this.serviceBuildUrl = serviceBuildUrl;
        return this;
    }

    /**
     * With parallel.
     *
     * @param parallel
     *            the parallel
     *
     * @return the job
     */
    public Job withParallel(final boolean parallel) {
        this.parallel = parallel;
        return this;
    }

    /**
     * With service environment.
     *
     * @param serviceEnvironment
     *            the service environment
     *
     * @return the job
     */
    public Job withServiceEnvironment(final Properties serviceEnvironment) {
        this.serviceEnvironment = serviceEnvironment;
        return this;
    }

    /**
     * With timestamp.
     *
     * @param timestamp
     *            the timestamp
     *
     * @return the job
     */
    public Job withTimestamp(final Long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    /**
     * With dry run.
     *
     * @param dryRun
     *            the dry run
     *
     * @return the job
     */
    public Job withDryRun(final boolean dryRun) {
        this.dryRun = dryRun;
        return this;
    }

    /**
     * With branch.
     *
     * @param branch
     *            the branch
     *
     * @return the job
     */
    public Job withBranch(final String branch) {
        this.branch = branch;
        return this;
    }

    /**
     * With pull request.
     *
     * @param pullRequest
     *            the pull request
     *
     * @return the job
     */
    public Job withPullRequest(final String pullRequest) {
        this.pullRequest = pullRequest;
        return this;
    }

    /**
     * With git.
     *
     * @param git
     *            the git
     *
     * @return the job
     */
    public Job withGit(final Git git) {
        this.git = git;
        return this;
    }

    /**
     * Gets the repo token.
     *
     * @return the repo token
     */
    public String getRepoToken() {
        return repoToken;
    }

    /**
     * Gets the service name.
     *
     * @return the service name
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * Gets the service job id.
     *
     * @return the service job id
     */
    public String getServiceJobId() {
        return serviceJobId;
    }

    /**
     * Gets the service build number.
     *
     * @return the service build number
     */
    public String getServiceBuildNumber() {
        return serviceBuildNumber;
    }

    /**
     * Gets the service build url.
     *
     * @return the service build url
     */
    public String getServiceBuildUrl() {
        return serviceBuildUrl;
    }

    /**
     * Gets the service environment.
     *
     * @return the service environment
     */
    public Properties getServiceEnvironment() {
        return serviceEnvironment;
    }

    /**
     * Gets the timestamp.
     *
     * @return the timestamp
     */
    public Long getTimestamp() {
        return timestamp;
    }

    /**
     * Checks if is parallel.
     *
     * @return true, if is parallel
     */
    public boolean isParallel() {
        return parallel;
    }

    /**
     * Checks if is dry run.
     *
     * @return true, if is dry run
     */
    public boolean isDryRun() {
        return dryRun;
    }

    /**
     * Gets the branch.
     *
     * @return the branch
     */
    public String getBranch() {
        if (branch != null && getGit() != null && getGit().getRemotes() != null) {
            for (Remote remote : getGit().getRemotes()) {
                if (branch.startsWith(remote.getName() + "/")) {
                    return branch.substring(remote.getName().length() + 1);
                }
            }
        }
        return branch;
    }

    /**
     * Gets the pull request.
     *
     * @return the pull request
     */
    public String getPullRequest() {
        return pullRequest;
    }

    /**
     * Gets the git.
     *
     * @return the git
     */
    public Git getGit() {
        return git;
    }

    /**
     * Validate.
     *
     * @return the validation errors
     */
    public ValidationErrors validate() {
        JobValidator validator = new JobValidator(this);
        return validator.validate();
    }
}
