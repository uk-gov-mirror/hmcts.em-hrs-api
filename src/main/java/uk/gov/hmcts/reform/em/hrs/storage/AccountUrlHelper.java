package uk.gov.hmcts.reform.em.hrs.storage;

import org.apache.commons.lang3.StringUtils;

class AccountUrlHelper {
    private static int HTTPS_PREFIX_LENGTH = "https://".length();

    /*
       url pattern is expected to be either
    https://cvprecordingsstgsa-secondary.blob.core.windows.net/
       or
    https://cvprecordingsstgsa.blob.core.windows.net/
    */

    static String extractAccountFromUrl(String cvpConnectionString) {

        int hyphenMarkerPos = StringUtils.indexOf(cvpConnectionString,"-");
        int endMarkerPos = hyphenMarkerPos==-1 ? StringUtils.indexOf(cvpConnectionString,".") : hyphenMarkerPos;

        String accountName = cvpConnectionString.substring(HTTPS_PREFIX_LENGTH, endMarkerPos);

        return accountName;
    }
}
