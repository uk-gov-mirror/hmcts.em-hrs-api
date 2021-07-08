package uk.gov.hmcts.reform.em.hrs.util;

import lombok.NoArgsConstructor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class CvpConnectionResolver {


    public static boolean isACvpEndpointUrl(String cvpConnectionString) {
        boolean isACvpEndpointUrl =
            cvpConnectionString.contains("cvprecordings") && !cvpConnectionString.contains("AccountName");
        return isACvpEndpointUrl;
    }


    /*
       url pattern is expected to be either
    https://cvprecordingsstgsa-secondary.blob.core.windows.net/
       or
    https://cvprecordingsstgsa.blob.core.windows.net/
    */
    static Pattern pattern = Pattern.compile("https://(.*?)(?:-secondary)?.blob.core.windows.net");

    static String extractAccountFromUrl(String cvpConnectionString) {

        Matcher matcher = pattern.matcher(cvpConnectionString);
        String accountName = null;

        if (matcher.find()) {
            accountName = matcher.group(1);
        }
        return accountName;
    }
}
