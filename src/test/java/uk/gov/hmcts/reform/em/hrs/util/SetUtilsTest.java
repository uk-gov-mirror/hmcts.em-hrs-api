package uk.gov.hmcts.reform.em.hrs.util;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SetUtilsTest {
    private final Set<String> setA = Set.of("a", "b", "c", "d");
    private final Set<String> setB = Set.of("c", "d", "e", "f");

    @Test
    void testIntersectionResultingSetSize() {
        final int expectedSize = 2;

        final Set<String> intersection = SetUtils.intersect(setA, setB);

        assertThat(intersection).hasSize(expectedSize);
    }

    @Test
    void testIntersectionResultingSetElements() {
        final Set<String> expectedSet = Set.of("c", "d");

        final Set<String> intersection = SetUtils.intersect(setA, setB);

        assertThat(intersection).hasSameElementsAs(expectedSet);
    }

    @Test
    void testUnionResultingSetSize() {
        final int expectedSize = 6;

        final Set<String> intersection = SetUtils.union(setA, setB);

        assertThat(intersection).hasSize(expectedSize);
    }

    @Test
    void testUnionResultingSetElements() {
        final Set<String> expectedSet = Set.of("a", "b", "c", "d", "e", "f");

        final Set<String> intersection = SetUtils.union(setA, setB);

        assertThat(intersection).hasSameElementsAs(expectedSet);
    }
}
