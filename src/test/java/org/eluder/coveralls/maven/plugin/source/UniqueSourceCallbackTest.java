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
package org.eluder.coveralls.maven.plugin.source;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.eluder.coveralls.maven.plugin.ProcessingException;
import org.eluder.coveralls.maven.plugin.domain.Source;
import org.eluder.coveralls.maven.plugin.util.TestIoUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UniqueSourceCallbackTest {

    @Mock
    private SourceCallback sourceCallbackMock;

    @Test
    void onSourceWithUniqueFiles() throws NoSuchAlgorithmException, ProcessingException, IOException {
        var s1 = createSource("Foo.java", "{\n  void();\n}\n", 2);
        var s2 = createSource("Bar.java", "{\n  bar();\n}\n", 2);

        var cb = createUniqueSourceCallback();
        cb.onBegin();
        cb.onSource(s1);
        cb.onSource(s2);
        cb.onComplete();
        verify(sourceCallbackMock).onBegin();
        verify(sourceCallbackMock, times(2)).onSource(ArgumentMatchers.any(Source.class));
        verify(sourceCallbackMock).onComplete();
    }

    @Test
    void onSourceWithDuplicateSources() throws NoSuchAlgorithmException, ProcessingException, IOException {
        var s1 = createSource("Foo.java", "{\n  void();\n}\n", 2);
        var s2 = createSource("Foo.java", "{\n  void();\n}\n", 2);

        var cb = createUniqueSourceCallback();
        cb.onBegin();
        cb.onSource(s1);
        cb.onSource(s2);
        cb.onComplete();
        verify(sourceCallbackMock).onBegin();
        verify(sourceCallbackMock, times(1)).onSource(ArgumentMatchers.any(Source.class));
        verify(sourceCallbackMock).onComplete();
    }

    @Test
    void onSourceWithUniqueSources() throws NoSuchAlgorithmException, ProcessingException, IOException {
        var s1 = createSource("Foo.java", "{\n  void();\n}\n", 2);
        var s2 = createSource("Foo.java", "{\n  void();\n  func();\n}\n", 2, 3);

        var cb = createUniqueSourceCallback();
        cb.onBegin();
        cb.onSource(s1);
        cb.onSource(s2);
        cb.onComplete();
        verify(sourceCallbackMock).onBegin();
        verify(sourceCallbackMock, times(2)).onSource(ArgumentMatchers.any(Source.class));
        verify(sourceCallbackMock).onComplete();
    }

    UniqueSourceCallback createUniqueSourceCallback() {
        return new UniqueSourceCallback(sourceCallbackMock);
    }

    Source createSource(final String name, final String source, final int... relevant) throws NoSuchAlgorithmException {
        var s = new Source(name, source, TestIoUtil.getSha512DigestHex(source));
        for (int i : relevant) {
            s.addCoverage(i, 1);
        }
        return s;
    }
}
