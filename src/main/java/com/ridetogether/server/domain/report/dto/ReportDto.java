package com.ridetogether.server.domain.report.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ridetogether.server.domain.image.domain.Image;
import com.ridetogether.server.domain.member.domain.Member;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

public class ReportDto {

    @AllArgsConstructor
    @Data
    @Builder
    public static class ReportSaveDto {
        private Member reporter;
        private Member reported;
        private String reportTitle;
        private String reportContent;
//        private Post reportMatching;
        private List<Image> images;
    }
}
