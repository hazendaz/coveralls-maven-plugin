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
package org.eluder.coveralls.maven.plugin.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.logging.Log;
import org.eluder.coveralls.maven.plugin.validation.ValidationError.Level;

/**
 * The Class ValidationErrors.
 */
public class ValidationErrors extends ArrayList<ValidationError> {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Throw or inform.
     *
     * @param log
     *            the log
     */
    public void throwOrInform(final Log log) {
        final var errors = this.filter(Level.ERROR);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors.get(0).getMessage());
        }
        for (final ValidationError error : this.filter(Level.WARN)) {
            log.warn(error.getMessage());
        }
    }

    /**
     * Filter.
     *
     * @param level
     *            the level
     *
     * @return the list
     */
    private List<ValidationError> filter(final Level level) {
        final List<ValidationError> filtered = new ArrayList<>();
        for (final ValidationError error : this) {
            if (level.equals(error.getLevel())) {
                filtered.add(error);
            }
        }
        return filtered;
    }
}
