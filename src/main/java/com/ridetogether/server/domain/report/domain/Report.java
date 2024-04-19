package com.ridetogether.server.domain.report.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ridetogether.server.domain.image.domain.Image;
import com.ridetogether.server.domain.matching.domain.Matching;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.domain.report.Model.HandleStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
public class Report {

    @Id
    @Column(name = "report_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx")
    private Member reporter;

    @OneToMany(mappedBy = "report")
    @JsonIgnore
    private List<ReportStatus> reportStatus;

    private String reportedMemberId;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "matching_idx")
//    private Matching reportMatching;
    private String reportMatchingId;

    private String reportTitle;

    private String reportContent;

    @OneToMany(mappedBy = "report")
    @JsonIgnore
    private List<Image> images;

    // How to timestamp

    @Enumerated(EnumType.STRING)
    private HandleStatus handleStatus;

    public void setReportHandleStatus(HandleStatus handleStatus) {
        this.handleStatus = handleStatus;
    }

    public void setReportTitle(String title) {
        this.reportTitle = title;
    }
    public void setReportContent(String content) {
        this.reportContent = content;
    }

    public void setImages(List<Image> imges) {
        this.images = imges;
    }

    public void updateReport(Report report) {
        this.setReportTitle(report.getReportTitle());
        this.setReportContent(report.getReportContent());
        this.setImages(report.getImages());
    }

    public boolean isComplete() {
        return this.handleStatus == HandleStatus.COMPLETE;
    }

    public boolean isWaiting() {
        return this.handleStatus == HandleStatus.WAITING;
    }

    public boolean isCompanion() {
        return this.handleStatus == HandleStatus.COMPANION;
    }

}
