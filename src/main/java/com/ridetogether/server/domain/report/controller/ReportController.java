package com.ridetogether.server.domain.report.controller;

import com.ridetogether.server.domain.report.application.AdminReportService;
import com.ridetogether.server.domain.report.application.UserReportService;
import com.ridetogether.server.domain.report.converter.ReportDtoConverter;
import com.ridetogether.server.domain.report.dto.ReportDto;
import com.ridetogether.server.domain.report.dto.ReportRequestDto;
import com.ridetogether.server.domain.report.dto.ReportResponseDto;
import com.ridetogether.server.global.apiPayload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.ridetogether.server.domain.report.dto.ReportDto.*;
import static com.ridetogether.server.domain.report.dto.ReportRequestDto.*;
import static com.ridetogether.server.domain.report.dto.ReportResponseDto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/report")
public class ReportController {

    private final AdminReportService adminReportService;
    private final UserReportService userReportService;

    /**
     * -유저-
     * 유저 신고
     * 자신이 한 신고 조회 (전체조회)
     * 자신이 한 신고 조회 (세부내용)
     * 신고 수정
     * 신고 삭제
     * -어드민-
     * 신고를 받은 사람한테 제재 주기
     * 대기중인 신고 조회
     * 처리완료된 신고 조회
     *
     */

    public ApiResponse<ReportDetailInfoResponseDto> reportUser(@Valid @RequestBody ReportUpdateRequestDto report) throws Exception {
        ReportSaveDto reportSaveDto = ReportDtoConverter.convertRequestToSaveDto(report);
        return ApiResponse.onSuccess(userReportService.saveReport(reportSaveDto));
    }


}
