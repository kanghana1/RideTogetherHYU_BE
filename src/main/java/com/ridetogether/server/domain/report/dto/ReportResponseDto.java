package com.ridetogether.server.domain.report.dto;

import com.ridetogether.server.domain.image.domain.Image;
import com.ridetogether.server.domain.member.domain.Member;
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
        private Member reported;
        private String reportTitle;
        private String reportContent;
        private List<Image> images;
    }

    @Data
    @Builder
    public static class ReportDeleteResponseDto {
        private boolean isSuccess;
    }

}
