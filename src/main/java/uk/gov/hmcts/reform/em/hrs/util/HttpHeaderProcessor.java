package uk.gov.hmcts.reform.em.hrs.util;

import javax.servlet.http.HttpServletRequest;

//non frontdoor environments (ie DEMO do not use front door, and may use TitleCase header names)
public class HttpHeaderProcessor {

    //front door environments use lowercase header names
    public static String getHttpHeaderByCaseSensitiveAndLowerCase(HttpServletRequest request,
                                                                  String headerNameGivenCase) {
        String headerName = request.getHeader(headerNameGivenCase);
        if (headerName == null) {
            headerName = request.getHeader(headerNameGivenCase.toLowerCase());
        }
        return headerName;
    }
}
