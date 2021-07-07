package uk.gov.hmcts.reform.em.hrs.util;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class CvpConnectionResolver {


    public static boolean isACvpEndpointUrl(String cvpConnectionString) {
        boolean isACvpEndpointUrl =
            cvpConnectionString.contains("cvprecordings") && !cvpConnectionString.contains("AccountName");
        return isACvpEndpointUrl;
    }
}
