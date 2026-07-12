package com.reserveflow.member.repository;

import com.reserveflow.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 회원 Entity 조회와 저장을 담당하는 Repository.
 *
 * 인증 식별자 기반 중복 확인과 활성 회원 조회에 사용한다.
 */
public interface MemberRepository extends JpaRepository<Member, Long> {

	/**
	 * 같은 인증 식별자로 가입된 사용자가 있는지 확인한다.
	 */
	boolean existsByAuthSubject(String authSubject);

	/**
	 * 인증 식별자와 상태가 일치하는 회원을 조회한다.
	 */
	Optional<Member> findByAuthSubjectAndStatus(String authSubject, String status);
}
