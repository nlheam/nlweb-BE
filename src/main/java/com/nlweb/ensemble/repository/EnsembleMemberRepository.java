package com.nlweb.ensemble.repository;

import com.nlweb.ensemble.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface EnsembleMemberRepository extends JpaRepository<EnsembleMember, Long> {

    /** 특정 사용자가 특정 앙상블의 멤버인지 확인 */
    Optional<EnsembleMember> findByUserIdAndEnsembleId(Long userId, Long ensembleId);

    /** 특정 앙상블의 모든 멤버 조회 */
    List<EnsembleMember> findByEnsembleId(Long ensembleId);

    /** 특정 사용자가 속한 모든 합주 멤버 조회 */
    List<EnsembleMember> findByUserId(Long userId);

    /** 특정 앙상블의 멤버 수 조회 */
    Long countByEnsembleId(Long ensembleId);

}
