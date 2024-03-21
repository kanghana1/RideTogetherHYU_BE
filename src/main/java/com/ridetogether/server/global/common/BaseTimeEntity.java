package com.ridetogether.server.global.common;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDate;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseTimeEntity {

	@CreatedDate
	@Column(updatable = false)
	private LocalDate createdAt;

	@LastModifiedDate
	@Column(updatable = true)
	private LocalDate lastModifiedAt;

}
