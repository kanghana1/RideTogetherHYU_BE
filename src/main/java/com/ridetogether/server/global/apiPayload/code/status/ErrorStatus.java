package com.ridetogether.server.global.apiPayload.code.status;

import com.ridetogether.server.global.apiPayload.code.BaseErrorCode;
import com.ridetogether.server.global.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),
    _LOGIN_FAILURE(HttpStatus.BAD_REQUEST, "COMMON400", "Login Fail"),

    //유저 응답
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4001", "유저가 존재하지 않습니다."),
    MEMBER_PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "MEMBER4002", "비밀번호가 일치하지 않습니다."),
    MEMBER_NOT_PUBLIC(HttpStatus.BAD_REQUEST, "MEMBER4003", "친구 요청을 보내려는 상대가 비공개 상태입니다."),
    MEMBER_NOT_TOKEN(HttpStatus.BAD_REQUEST, "MEMBER4004", "서버에 저장된 해당 유저의 FirebaseToken이 존재하지 않습니다."),
    NOT_MATCHING_MEMBER_TOKEN(HttpStatus.BAD_REQUEST, "MEMBER4005", "토큰과 로그인 된 멤버가 일치하지 않습니다."),

    //앨범 응답
    ALBUM_NOT_FOUND(HttpStatus.BAD_REQUEST, "ALBUM4001", "앨범이 존재하지 않습니다."),

    //사진 응답
    PHOTO_NOT_FOUND(HttpStatus.BAD_REQUEST, "PHOTO4001", "사진이 존재하지 않습니다."),

    //피드 응답
    FEED_NOT_FOUND(HttpStatus.BAD_REQUEST, "FEED4001", "피드가 존재하지 않습니다."),

    //푸시알림 응답
    ALARM_SEND_FAIL(HttpStatus.BAD_REQUEST,"ALARM4001", "알림 보내기를 실패하였습니다.");
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
            .message(message)
            .code(code)
            .isSuccess(false)
            .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
            .message(message)
            .code(code)
            .isSuccess(false)
            .httpStatus(httpStatus)
            .build();
    }
}