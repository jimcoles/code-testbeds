package org.jkcsoft.tests.javalang;

import org.jkcsoft.java.testing.BaseTestCase;
import org.jkcsoft.java.util.Strings;
import org.junit.Test;

import java.util.*;

/**
 * @author Jim Coles
 */
public class CollectionTests extends BaseTestCase {

    @Test
    public void testSortedSets() {
        SortedSet<Integer> mySet = new TreeSet<>();
        mySet.addAll(Set.of(4, 2, 1, 3, 7, 12, 9));
        out("sorted {0}", Strings.buildCommaDelList(mySet));

        out("last = {0}", mySet.last());

        int toElement = 9;
        SortedSet<Integer> headInts = mySet.headSet(toElement);
        out("headSet() (i.e., 'first exclusive') of {0} = {1}", toElement, Strings.buildCommaDelList(headInts));

        SortedSet<Integer> tailInts = mySet.tailSet(9);
        out("tailSet() (i.e., 'rest inclusive') of {0} = {1}", toElement, Strings.buildCommaDelList(tailInts));
    }

    @Test
    public void testSortedSetsOfObjects() {
        SortedSet<ValueWrapper> mySet = new TreeSet<>();
        ValueWrapper toElement = new ValueWrapper(9);
        mySet.addAll(Set.of(new ValueWrapper(4), new ValueWrapper(2), new ValueWrapper(1), new ValueWrapper(3),
                            new ValueWrapper(7), new ValueWrapper(12), toElement));
        out("sorted {0}", Strings.buildCommaDelList(mySet));

        SortedSet<ValueWrapper> headInts = mySet.headSet(toElement);
        out("heads (i.e., 'first exclusive') of {0} = {1}", toElement, Strings.buildCommaDelList(headInts));

        SortedSet<ValueWrapper> tailInts = mySet.tailSet(toElement);
        out("tails (i.e., 'rest inclusive') of {0} = {1}", toElement, Strings.buildCommaDelList(tailInts));

        mySet.remove(toElement);
        out("set after removal of {1} => {0}", Strings.buildCommaDelList(mySet), toElement);
    }

    public static class ValueWrapper implements Comparable<ValueWrapper> {
        private int value;

        public ValueWrapper(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "ValueWrapper{" + value + '}';
        }

        @Override
        public int compareTo(ValueWrapper otherValue) {
            return Integer.compare(this.value, otherValue.getValue());
        }
    }
}
