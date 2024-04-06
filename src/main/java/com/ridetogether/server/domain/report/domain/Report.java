package com.ridetogether.server.domain.report.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ridetogether.server.domain.image.domain.Image;
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
@Table(name = "Report")
public class Report {

    @Id
    @Column(name = "report_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY) // ManyToMany? ManyToOne?
    @JoinColumn(name = "member_idx")
    private Member reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx")
    private Member reported;


//    private Post reportMatching;

    private String reportTitle;

    private String reportContent;

    @OneToMany(mappedBy = "member")
    @JsonIgnore
    private List<Image> images;

    // How to timestamp

    @Enumerated(EnumType.STRING)
    private HandleStatus handleStatus;

    public void setReportTitle(String title) {
        this.reportTitle = title;
    }

    public void setReportContent(String content) {
        this.reportContent = content;
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
