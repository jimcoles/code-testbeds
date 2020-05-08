package org.jkcsoft.codetests.hackerrank.arraymanip;

import javafx.util.converter.CharacterStringConverter;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.*;
import java.util.stream.*;

/**
 * @author Jim Coles
 */
public class Solution {

    public static final String INTEGRAL_FMT = "%,d";
    public static final String EOL = System.getProperty("line.separator");
    private static final Scanner scanIoStream = new Scanner(System.in);
    // Complete the arrayManipulation function below.
    private static long initMillis;

    public static void main(String[] args) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

//        for (int i = 0; i < numOpers; i++) {
//            String[] queriesRowItems = scanner.nextLine().split(" ");
//            scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");
//
//            for (int j = 0; j < 3; j++) {
//                int queriesItem = Integer.parseInt(queriesRowItems[j]);
//            }
//        }

        MapReduceSolution solution = new MapReduceSolution();
        long result = solution.solve(System.in);

        bufferedWriter.write(String.valueOf(result));
        bufferedWriter.newLine();

        bufferedWriter.close();

        scanIoStream.close();
    }

    public static class MapReduceSolution {

        public long solve(InputStream in) throws IOException {
//            Stream<String> stringStream = Stream.of("", "");
//            Stream<AddValueOper> hcOpers = Stream.of(new AddValueOper(1, 2, 3));

            // state managed by m/r pipeline
            Map<Integer, ValueSlot> valueSlotTable = new HashMap<>();
            long maxResult = 0;
//            ValueSlot maxSlot = null;

            // -------------------------------------
            // map and reduce operators (functions)
            // -------------------------------------

            // Scan line (as String) into a class-defined struct
            Function<? super String, AddValueOper> unmarshallAddOper = (line) -> {
                AddValueOper addValueOper = null;
                if (line != null) {
                    Scanner scanner = new Scanner(line).useDelimiter(" ");
                    addValueOper = new AddValueOper(scanner.nextInt(), scanner.nextInt(), scanner.nextInt());
                }
                return addValueOper;
            };

            // Create or lookup value slots
            Function<? super AddValueOper, Stream<ValueSlot>> getOrCreateValSlots = (addValueOper) -> {
                IntStream slotIds =
                    IntStream.range(addValueOper.intRange.getNdx1(), addValueOper.intRange.getNdx2() + 1);
                Set<ValueSlot> slots = new HashSet<>();
                slotIds.forEach(slotId -> {
                                    ValueSlot slot = null;
                                    if (valueSlotTable.containsKey(slotId)) {
                                        slot = valueSlotTable.get(slotId);
                                    }
                                    else {
                                        slot = new ValueSlot(slotId, 0);
                                        valueSlotTable.put(slotId, slot);
                                    }
                                    slot.incValue(addValueOper.getValue());
                                    slots.add(slot);
                                }
                );
                return slots.stream();
            };

/*
            ReadableByteChannel readableByteChannel = Channels.newChannel(new DataInputStream(System.in));
            readableByteChannel.
            Stream.generate(new Supplier<Character>() {
                @Override
                public Character get() {
                    return null;
                }
            });
*/

            // build the graph ...
            Stream<String> recordStream;
            {
                CharInputStream charObjStreamIn = toStream(System.in);
                recordStream = toStream(charObjStreamIn.getUtilStream());
            }
            {
                IntStream sysinIntStream = IntStream.generate(new IoIsSupplier(System.in));
                // recordStream = {TODO: parse from IntStream}
            }
            Supplier<String> scannerSupplier = new IsScannerSupplier(System.in);
            {
                // Scanner approach
                recordStream = Stream.generate(scannerSupplier);
            }
            // eat the first line
            String argLine = scannerSupplier.get();

            // The final step: Evaluate the stream pipeline
            maxResult = recordStream
                .map(unmarshallAddOper)
                .flatMap(getOrCreateValSlots)
                .map(ValueSlot::getValue)
                .max(Long::compareTo)
                .orElse(-1L)
            ;
            return maxResult;
        }

        private static class IoIsSupplier implements IntSupplier {

            private final DataInputStream dis;

            public IoIsSupplier(InputStream is) {
                dis = new DataInputStream(is);
            }

            @Override
            public int getAsInt() {
                int charAsInt = -1;
                try {
                    charAsInt = dis.readChar();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                return charAsInt;
            }
        }

        private class IsScannerSupplier implements Supplier<String> {

            private Scanner recordScanner;

            public IsScannerSupplier(InputStream is) {
                recordScanner = new Scanner((InputStream) is);
            }

            @Override
            public String get() {
                return recordScanner.hasNext() ? recordScanner.nextLine() : null;
            }

        }

/*
            // Read char stream into lines
            Function<? super Character, String> collectLine = (inChar) -> {
                String line = null;
                if (inChar.toString().equals(EOL)) {
                    line = lineBuffer.toString();
                    lineBuffer.clear();
                }
                else {
                    lineBuffer.append(inChar);
                }
                return line;
            };

        // Keeping this Collector here but I don't think we want a Collector because
        // we need to break one stream in to another stream, not collect into one thing.
        // i.e., we need a spliterator

        Collector<Character, CharBuffer, String> nextLineCollector =
            new Collector<Character, CharBuffer, String>() {
                @Override
                public Supplier<CharBuffer> supplier() {
                    return () -> CharBuffer.allocate(20);
                }

                @Override
                public BiConsumer<CharBuffer, Character> accumulator() {
                    BiConsumer<CharBuffer, Character> retConsumer = (charBuffer, inChar) -> {
                        if (inChar.toString().equals(EOL)) {
                        }
                        else
                            charBuffer.append(inChar);
                    };
                    return retConsumer;
                }

                @Override
                public BinaryOperator<CharBuffer> combiner() {
                    return null;
                }

                @Override
                public Function<CharBuffer, String> finisher() {
                    return CharBuffer::toString;
                }

                @Override
                public Set<Characteristics> characteristics() {
                    return null;
                }
            };
*/

    }

    private static String fmt(long l) {
        return String.format(INTEGRAL_FMT, l);
    }

    private static String fmt(int i) {
        return String.format(INTEGRAL_FMT, i);
    }

    private static void log(String msg) {
        System.out.println(fmtMillisAsFloat() + " - " + msg);
    }

    private static String fmtMillisAsFloat() {
        return String.format("%.3f", (System.currentTimeMillis() - initMillis) / 1000.0d);
    }

    private static CharInputStream toStream(InputStream source) {
        DataInputStream dis = new DataInputStream(source);
        Spliterator<Character> charSpl8r = Spliterators.spliteratorUnknownSize(
            new Iterator<Character>() {
                private Character next;
                private boolean read = false;

                @Override
                public boolean hasNext() {
                    if (!read) {
                        try {
                            next = dis.readChar();
                        }
                        catch (EOFException e) {
                            next = null;
                        }
                        catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                        read = true;
                    }
                    return next != null;
                }

                @Override
                public Character next() {
                    read = false;
                    return next;
                }
            },
            Spliterator.IMMUTABLE | Spliterator.ORDERED | Spliterator.NONNULL
        );
        Stream<Character> charUtilStream = StreamSupport.stream(charSpl8r, false);
        //
        return new CharInputStream(dis, charUtilStream);
    }

    /** Stream
     * @return*/
    private static Stream<String> toStream(Stream<Character> source) {
        Spliterator<String> lineSpl8r = Spliterators.spliteratorUnknownSize(
            new Iterator<String>() {

                //                private CharBuffer nextBuffer = CharBuffer.allocate(30);
                private String next;
                private boolean read = false;

                @Override
                public boolean hasNext() {
                    if (!read) {
                        Character[] bigChars = source.filter((nextChar) -> nextChar.toString().equals(EOL))
                                                     .toArray(Character[]::new);
                        char[] littleChars = new char[bigChars.length];
                        for (int idx = 0; idx < bigChars.length; idx++) {
                            littleChars[idx] = bigChars[idx];
                        }
                        next = String.valueOf(littleChars);
//                        optNextChar.ifPresent(character -> nextBuffer.append(character));
                        read = true;
                    }
                    return next != null;
                }

                @Override
                public String next() {
                    read = false;
                    return next;
                }
            },
            Spliterator.IMMUTABLE | Spliterator.ORDERED | Spliterator.NONNULL
        );
        return StreamSupport.stream(lineSpl8r, false);
    }

    public static <T> Stream<T> toStream(final ObjectInputStream ois, final Class<T> cls) {
        Spliterator<T> spliterator = Spliterators.spliteratorUnknownSize(
            new Iterator<T>() {
                private T next;
                private boolean read = false;

                @Override
                public boolean hasNext() {
                    if (!read) {
                        try {
                            next = cls.cast(ois.readUnshared());
                        }
                        catch (EOFException e) {
                            next = null;
                        }
                        catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                        catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                        read = true;
                    }
                    return next != null;
                }

                @Override
                public T next() {
                    read = false;
                    return next;
                }
            },
            Spliterator.DISTINCT | Spliterator.IMMUTABLE | Spliterator.ORDERED | Spliterator.NONNULL
        );

        Stream<T> oStream = StreamSupport.stream(spliterator, false).onClose(() -> {
            try {
                ois.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        });
        return oStream;
    }

    public static class AddValueOper {
        private Rejects.RangeTrackingSolution.IntRange intRange;
        private int value;

        public AddValueOper(int ndx1, int ndx2, int value) {
            this.intRange = new Rejects.RangeTrackingSolution.IntRange(ndx1, ndx2);
            this.value = value;
        }

        public Rejects.RangeTrackingSolution.IntRange getIntRange() {
            return intRange;
        }

        public int getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "Mod: add " + value + " " + intRange;
        }
    }

    private static class ValueSlot {
        private int slotIndex;
        private long value;

        public ValueSlot(int slotIndex, long value) {
            this.slotIndex = slotIndex;
            this.value = value;
        }

        public int getSlotIndex() {
            return slotIndex;
        }

        public long getValue() {
            return value;
        }

        public void incValue(long add) {
            value += add;
        }
    }

    /** Holds the element stream and it's source io stream. */
    public static class CharInputStream {
        private DataInputStream ioInputStream;
        private Stream<Character> utilStream;

        public CharInputStream(DataInputStream ioInputStream, Stream<Character> utilStream) {
            this.ioInputStream = ioInputStream;
            this.utilStream = utilStream;
        }

        public DataInputStream getIoInputStream() {
            return ioInputStream;
        }

        public Stream<Character> getUtilStream() {
            return utilStream;
        }
    }
}
