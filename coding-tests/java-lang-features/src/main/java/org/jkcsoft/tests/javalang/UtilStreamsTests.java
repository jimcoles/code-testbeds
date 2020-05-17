/*
 * Copyright (c) Jim Coles (jameskcoles@gmail.com) 2020 through present.
 *
 * Licensed under the following license agreement:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Also see the LICENSE file in the repository root directory.
 */

package org.jkcsoft.tests.javalang;

import org.jkcsoft.java.testing.BaseTestCase;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Jim Coles
 */
public class UtilStreamsTests extends BaseTestCase {

    @Test
    public void testSimpleMath() {
        Stream<Integer> integerStream = Stream.of(1, 2, 3, 12);
        out("sum: {0}", integerStream.reduce(Integer::sum).get());
    }

    @Test
    public void testRanges() {
        IntStream.range(1,2).forEach((val) -> System.out.print(val + ","));
    }

    @Test
    public void testMutipleReductions() {
//        Stream<String> recordStream = Stream.of("1 2 3", "add", "mult", "div");
        Stream<String> recordStream = Stream.of("1 2 3");
//        handleFirstRestSimple(recordStream);
        out("count: {0}", recordStream.count());

//        out("2nd: {0}", recordStream.findFirst().orElse("?"));
    }

    private void handleFirstRestSimple(Stream<String> recordStream) {
        AtomicBoolean readFirst = new AtomicBoolean(false);
        AtomicReference<String> argLine = new AtomicReference<>(null);
        Consumer<String> argAction = (line) -> {
            out("arg line {0}", line);
            argLine.set(line);
        };
        Consumer<String> operAction = (line) -> {
            out("oper {0} for args {1}", line, argLine);

        };

        recordStream.forEach(
            record -> {
                recordStream.count();
                if (!readFirst.get()) {
                    argAction.accept(record);
                    readFirst.set(true);
                }
                else
                    operAction.accept(record);
            }
        );
    }

    public static class Args {

    }

    public static class OperQuery {

    }
}
