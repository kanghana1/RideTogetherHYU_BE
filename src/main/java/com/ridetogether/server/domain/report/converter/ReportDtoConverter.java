package com.ridetogether.server.domain.report.converter;

import com.ridetogether.server.domain.member.dao.MemberRepository;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.domain.report.Model.IsReporter;
import com.ridetogether.server.domain.report.domain.Report;
import com.ridetogether.server.domain.report.dto.ReportDto;
import com.ridetogether.server.domain.report.dto.ReportRequestDto;
import com.ridetogether.server.global.apiPayload.code.status.ErrorStatus;
import com.ridetogether.server.global.apiPayload.exception.handler.ErrorHandler;

import static com.ridetogether.server.domain.report.dto.ReportDto.*;
import static com.ridetogether.server.domain.report.dto.ReportRequestDto.*;
import static com.ridetogether.server.domain.report.dto.ReportResponseDto.*;

public class ReportDtoConverter {


    public static ReporterResponseDto reporterConverter(Member reporter) {
        return ReporterResponseDto.builder()
                .idx(reporter.getIdx())
                .name(reporter.getName())
                .memberId(reporter.getMemberId())
                .role(reporter.getRole())
                .isReporter(IsReporter.REPORTER)
                .build();
    }

//    public static ReportedResponseDto ReportedConverter(String reportedMemberId) {
//        Member reported = memberRepository.findByMemberId(reportedMemberId).orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
//        return ReportedResponseDto.builder()
//                .idx(reported.getIdx())
//                .name(reported.getName())
//                .memberId(reported.getMemberId())
//                .role(reported.getRole())
//                .isReporter(IsReporter.REPORTED)
//                .build();
//    }
    public static ReportSaveDto convertRequestToSaveDto(ReportUpdateRequestDto saveReport) {
        return ReportSaveDto.builder()
                .reporter(saveReport.getReporter())
                .reportedMemberId(saveReport.getReported())
                .reportTitle(saveReport.getReportTitle())
                .reportContent(saveReport.getReportContent())
                .images(saveReport.getImages())
                .build();
    }

    public static ReportDetailInfoResponseDto convertReportToDetailInfoDto(Report report) {
        return ReportDetailInfoResponseDto.builder()
                .idx(report.getIdx())
                .reporter(report.getReporter())
                .reportedId(report.getReportedMemberId())
                .reportTitle(report.getReportTitle())
                .reportContent(report.getReportContent())
                .images(report.getImages())
                .build();
    }

}
