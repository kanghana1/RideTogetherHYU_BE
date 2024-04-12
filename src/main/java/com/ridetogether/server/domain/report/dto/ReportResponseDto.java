package com.ridetogether.server.domain.report.dto;

import com.ridetogether.server.domain.image.domain.Image;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.domain.member.model.Role;
import com.ridetogether.server.domain.report.Model.IsReporter;
import lombok.Builder;
import lombok.Data;

import java.util.List;

public class ReportResponseDto {

    @Data
    @Builder
    public static class ReportInfoResponseDto {
        private Long idx;
        private String reportTitle;
        private String reportContent;
        private List<Image> images;
    }
    @Data
    @Builder
    public static class ReportDetailInfoResponseDto {
        private Long idx;
        private Member reporter;
        private String reportedId;
        private String reportTitle;
        private String reportContent;
        private List<Image> images;
    }

    @Data
    @Builder
    public static class ReportDeleteResponseDto {
        private boolean isSuccess;
    }

    @Data
    @Builder
    public static class ReporterResponseDto {
        private Long idx;
        private String memberId;
        private String name;
        private Role role;
        private IsReporter isReporter;

    }
    @Data
    @Builder
    public static class ReportedResponseDto {
        private Long idx;
        private String memberId;
        private String name;
        private Role role;
        private IsReporter isReporter;
    }

}
