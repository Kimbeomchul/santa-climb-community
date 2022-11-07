package com.mozzi.santa.dto;

import lombok.Data;

@Data
public class BugReport {

    private Long bugReportNum;
    private String socialId;
    private String errorPage;
    private String errorDetail;
    private String errorDate;
    private String errorResult;
}
