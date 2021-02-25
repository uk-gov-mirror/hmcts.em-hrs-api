package uk.gov.hmcts.reform.em.hrs.utils;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SetUtils {
    public static <T> Set<T> intersect(final Set<T> setA, final Set<T> setB) {
        return setA.stream()
            .filter(setB::contains)
            .collect(Collectors.toSet());
    }

    public static <T> Set<T> union(final Set<T> setA, final Set<T> setB) {
        return Stream.concat(setA.stream(), setB.stream())
            .collect(Collectors.toSet());
    }
}
