package uk.gov.hmcts.reform.em.hrs.testutil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.em.test.idam.IdamHelper;

import javax.inject.Named;

@Named
public class FolderAndFileNamesHelper {

    @Autowired
    private IdamHelper idamHelper;

    @Qualifier("authTokenGenerator")
    @Autowired
    private AuthTokenGenerator authTokenGenerator;

}
