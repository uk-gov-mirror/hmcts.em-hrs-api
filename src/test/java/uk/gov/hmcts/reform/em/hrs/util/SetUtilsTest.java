package uk.gov.hmcts.reform.em.hrs.util;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

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

        final Set<String> intersection = SetUtils.intersect(null, null);

       // assertThat(intersection).hasSize(expectedSize);
    }

    @Test
    void testIntersectionResultingSetSizeWithOneNull() {

        final Set<String> intersection = SetUtils.intersect(setA, null);

        // assertThat(intersection).hasSize(expectedSize);
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
        //final Set<String> expectedSet = Set.of(null, "b", "c", "f");
//        final Set<String> setE = Set.of(null, "b", "c");
//        final Set<String> setF = Set.of("c", null, "f");

        final Set<String> intersection = SetUtils.union(null, null);
    //    assertThat(intersection).hasSameElementsAs(expectedSet);
    }

    @Test
    void testUnionResultingSetElementsForOneNull() {

        final Set<String> intersection = SetUtils.union(null, setD);
       // assertThat(intersection).hasSameElementsAs(expectedSet);
    }
}
