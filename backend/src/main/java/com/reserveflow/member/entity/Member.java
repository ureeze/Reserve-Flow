package com.reserveflow.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ReserveFlow 회원 Entity.
 *
 * 내부 DB 식별자, 외부 노출용 public ID, 인증 식별자,
 * 비밀번호 해시, 표시 이름, 상태와 생성/수정 시각을 members 테이블에 저장한다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "members")
public class Member {

	/**
	 * DB 내부 조인과 영속성 식별에 사용하는 기본키.
	 *
	 * 외부 API와 JWT에는 노출하지 않는다.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 클라이언트와 JWT에 노출하는 회원 식별자.
	 *
	 * 순차 ID 노출을 피하기 위해 UUID를 사용한다.
	 */
	@Column(name = "public_id", nullable = false, unique = true)
	private UUID publicId;

	/**
	 * 로그인과 JWT subject로 사용하는 인증 식별자.
	 *
	 * 현재 MVP에서는 이메일 또는 이메일 형태의 문자열을 주로 사용한다.
	 */
	@Column(name = "auth_subject", nullable = false, unique = true)
	private String authSubject;

	/**
	 * BCrypt로 해시한 회원 비밀번호.
	 *
	 * 원문 비밀번호는 저장하지 않는다.
	 */
	@Column(name = "password_hash", nullable = false)
	private String passwordHash;

	/**
	 * 화면에 표시할 회원 이름.
	 */
	@Column(name = "display_name")
	private String displayName;

	/**
	 * 회원 계정 상태.
	 *
	 * 현재 허용 값은 ACTIVE, INACTIVE, ANONYMIZED이다.
	 */
	@Column(nullable = false)
	private String status = "ACTIVE";

	/**
	 * 회원 생성 시각.
	 */
	@Column(name = "created_at", nullable = false)
	private Instant createdAt = Instant.now();

	/**
	 * 회원 정보 마지막 수정 시각.
	 */
	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt = Instant.now();

	/**
	 * 회원가입 요청으로 Member Entity를 생성한다.
	 *
	 * 내부 PK는 DB가 생성하고, API와 JWT에 노출할 public ID는 애플리케이션에서 생성한다.
	 * 비밀번호는 원문이 아니라 BCrypt로 해시한 값을 전달받아 저장한다.
	 */
	public Member(String authSubject, String passwordHash, String displayName) {
		this.publicId = UUID.randomUUID();
		this.authSubject = authSubject;
		this.passwordHash = passwordHash;
		this.displayName = displayName;
	}

	/**
	 * Entity 수정 시 updated_at 값을 현재 시각으로 갱신한다.
	 */
	@PreUpdate
	void updateTimestamp() {
		this.updatedAt = Instant.now();
	}
}
