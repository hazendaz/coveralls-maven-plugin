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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collections;

import org.apache.maven.plugin.logging.Log;
import org.eluder.coveralls.maven.plugin.validation.ValidationError.Level;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ValidationErrorsTest {

    @Mock
    private Log logMock;

    @Test
    void throwOrInformWithError() {
        assertThrows(ValidationException.class, () -> {
            createValidationErrors(new ValidationError(Level.ERROR, "message")).throwOrInform(logMock);
        });
    }

    @Test
    void throwOrInformWithWarnings() {
        createValidationErrors(new ValidationError(Level.WARN, "error1"), new ValidationError(Level.WARN, "error2"))
                .throwOrInform(logMock);
        verify(logMock, times(2)).warn(any(CharSequence.class));
    }

    private ValidationErrors createValidationErrors(final ValidationError... errors) {
        var validationErrors = new ValidationErrors();
        Collections.addAll(validationErrors, errors);
        return validationErrors;
    }
}
