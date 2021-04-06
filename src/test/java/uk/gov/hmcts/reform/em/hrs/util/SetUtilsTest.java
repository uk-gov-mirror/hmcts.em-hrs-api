package uk.gov.hmcts.reform.em.hrs.util;


import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SetUtilsTest {
    private final Set<String> setA = Set.of("a", "b", "c", "d");
    private final Set<String> setB = Set.of("c", "d", "e", "f");
    private final Set<String> setC = Set.of("", "b", "c");
    private final Set<String> setD = Set.of("c", "", "f");


    @Test
    void testIntersectionResultingSetSize() {
        final int expectedSize = 2;

        final Set<String> intersection = SetUtils.intersect(setA, setB);

        assertThat(intersection).hasSize(expectedSize);

    }

    @Test
    void testIntersectionResultingSetSizeWithEmptyString() {
        final int expectedSize = 2;

        final Set<String> intersection = SetUtils.intersect(setC, setD);

        assertThat(intersection).hasSize(expectedSize);
    }

    @Test
    void testIntersectionResultingSetSizeWithNull() {

        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> { final Set<String> intersection = SetUtils.intersect(null, null);; }
        );

        assertEquals("setA is marked non-null but is null", exception.getMessage());
    }

    @Test
    void testIntersectionResultingSetSizeWithOneNull() {

        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> { final Set<String> intersection = SetUtils.intersect(setA, null);; }
        );

        assertEquals("setB is marked non-null but is null", exception.getMessage());
    }

    @Test
    void testIntersectionResultingSetElements() {
        final Set<String> expectedSet = Set.of("c", "d");

        final Set<String> intersection = SetUtils.intersect(setA, setB);

        assertThat(intersection).hasSameElementsAs(expectedSet);
    }

    @Test
    void testIntersectionResultingSetElementsWithEmptyString() {
        final Set<String> expectedSet = Set.of("c", "");

        final Set<String> intersection = SetUtils.intersect(setC, setD);

        assertThat(intersection).hasSameElementsAs(expectedSet);
    }

    @Test
    void testUnionResultingSetSize() {
        final int expectedSize = 6;

        final Set<String> intersection = SetUtils.union(setA, setB);

        assertThat(intersection).hasSize(expectedSize);
    }

    @Test
    void testUnionResultingSetSizeForEmptyString() {
        final int expectedSize = 4;

        final Set<String> intersection = SetUtils.union(setC, setD);

        assertThat(intersection).hasSize(expectedSize);
    }


    @Test
    void testUnionResultingSetElements() {
        final Set<String> expectedSet = Set.of("a", "b", "c", "d", "e", "f");

        final Set<String> intersection = SetUtils.union(setA, setB);

        assertThat(intersection).hasSameElementsAs(expectedSet);
    }

    @Test
    void testUnionResultingSetElementsForEmptyString() {
        final Set<String> expectedSet = Set.of("", "b", "c", "f");

        final Set<String> intersection = SetUtils.union(setC, setD);

        assertThat(intersection).hasSameElementsAs(expectedSet);
    }

    @Test
    void testUnionResultingSetElementsForNull() {

        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> { final Set<String> union = SetUtils.union(null, null);; }
        );

        assertEquals("setA is marked non-null but is null", exception.getMessage());
    }


    @Test
    void testUnionResultingSetElementsForOneNull() {

        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> { final Set<String> union = SetUtils.union(setD,null);; }
        );

        assertEquals("setB is marked non-null but is null", exception.getMessage());

    }
}
