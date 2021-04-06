package uk.gov.hmcts.reform.em.hrs.util;

import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor()
public final class SetUtils {

    public static <T> Set<T> intersect(@NotNull final Set<T> setA, @NotNull final Set<T> setB) {
        Set<T> a = new HashSet<>(setA);
        Set<T> b = new HashSet<>(setB);

        return a.retainAll(b) ? Collections.unmodifiableSet(a) : setA;
    }

    public static <T> Set<T> union(@NotNull final Set<T> setA, @NotNull final Set<T> setB) {
        return Stream.concat(setA.stream(), setB.stream())
            .collect(Collectors.toSet());
    }
}
