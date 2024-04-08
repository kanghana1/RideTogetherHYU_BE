package com.ridetogether.server.domain.report.application;

import com.ridetogether.server.domain.report.Model.HandleStatus;
import com.ridetogether.server.domain.report.dao.ReportRepository;
import com.ridetogether.server.domain.report.domain.Report;
import com.ridetogether.server.domain.report.dto.ReportDto;
import com.ridetogether.server.global.apiPayload.code.status.ErrorStatus;
import com.ridetogether.server.global.apiPayload.exception.handler.ErrorHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ridetogether.server.domain.report.dto.ReportDto.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserReportService {

    private final ReportRepository reportRepository;

    /**
     * 신고 등록
     * 본인이 한 신고 내역 조회
     * 신고 내역 상세 조회
     * 신고 수정
     * 신고 삭제
     *
     */

    public Report saveReport(ReportSaveDto reportSaveDto) {
        if (reportSaveDto.getReportTitle().isEmpty()) {
            throw new ErrorHandler(ErrorStatus.REPORT_TITLE_NULL);
        }
        if (reportSaveDto.getReportContent().isEmpty()) {
            throw new ErrorHandler(ErrorStatus.REPORT_CONTENT_NULL);
        }
        // 여기도 나중에 매칭 넣기
        Report report = Report.builder()
                .reporter(reportSaveDto.getReporter())
                .reported(reportSaveDto.getReported())
                .reportTitle(reportSaveDto.getReportTitle())
                .reportContent(reportSaveDto.getReportContent())
                .images(reportSaveDto.getImages())
                .build();


        report.setReportHandleStatus(HandleStatus.WAITING);
        reportRepository.save(report);
        return report;
    }


}
