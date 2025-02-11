package uk.gov.hmcts.reform.em.hrs.domain;

public enum AuditActions {




    PERSIST_FILE_STORE_OK,
    PERSIST_FILE_STORE_FAIL,

    PERSIST_CCD_CREATE_CASE_FOUND_EXISTING,
    PERSIST_CCD_CREATE_CASE_OK,
    PERSIST_CCD_CREATE_CASE_FAIL,

    PERSIST_CCD_ATTACH_HEARING_RECORDING_FOUND_EXISTING,
    PERSIST_CCD_ATTACH_HEARING_RECORDING_OK,
    PERSIST_CCD_ATTACH_HEARING_RECORDING_FAIL,

    ORCHESTRATION_OK,
    ORCHESTRATION_FAIL,
    ORCHESTRATION_RETRIED,
    ORCHESTRATION_TOO_MANY_FAILURES,
    ORCHESTRATION_IGNORE,

    SHARE_GRANT_OK,
    SHARE_GRANT_FAIL,

    SHARE_REVOKE_OK,
    SHARE_REVOKE_FAIL,

    NOTIFY_OK,
    NOTIFY_FAIL,

    USER_DOWNLOAD_OK,
    USER_DOWNLOAD_UNAUTHORIZED,
    USER_DOWNLOAD_FAIL,


    DATA_LIFECYCLE_CREATED,
    DATA_LIFECYCLE_UPDATED,//TODO note audio files CANNOT be updated, however some meta fields can, perhaps
    // ccd_attachment_id?
    DATA_LIFECYCLE_DELETED,
    DATA_LIFECYCLE_HARD_DELETED,


    TTL_UPDATED_OK,
    TTL_UPDATED_FAIL;
}
