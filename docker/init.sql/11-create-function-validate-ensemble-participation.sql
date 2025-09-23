CREATE OR REPLACE FUNCTION validate_ensemble_participation()
    RETURNS TRIGGER AS $$
DECLARE
v_user_vocalable BOOLEAN;
    v_user_status VARCHAR(20);
    v_has_event_approval BOOLEAN := FALSE;
    v_filled_sessions INTEGER;
BEGIN
    -- 사용자 정보 확인
SELECT status, is_vocalable INTO v_user_status, v_user_vocalable
FROM users WHERE id = NEW.user_id;

-- 활성 사용자만 참가 가능
IF v_user_status != 'ACTIVE' THEN
        RAISE EXCEPTION 'Only active users can participate in events';
END IF;

    -- VOCAL 세션 신청 시 vocalable 체크
    IF NEW.session_type = 'VOCAL' AND NOT v_user_vocalable THEN
        RAISE EXCEPTION 'User is not authorized to participate as VOCAL. Contact administrator to enable vocal permission.';
END IF;

    -- 이벤트 참가 승인 여부 확인
SELECT EXISTS(
    SELECT 1 FROM event_participants ep
                      JOIN events e ON ep.event_id = e.id
    WHERE ep.user_id = NEW.user_id
      AND ep.application_status = 'APPROVED'
      AND (e.id = (SELECT event_id FROM ensembles WHERE id = NEW.ensemble_id) OR
           e.parent_event = (SELECT event_id FROM ensembles WHERE id = NEW.ensemble_id))
) INTO v_has_event_approval;

IF NOT v_has_event_approval THEN
        RAISE EXCEPTION 'User must be approved for the event before joining ensemble';
END IF;

    -- 합주 완성도 체크 (7개 세션이 모두 찼는지)
SELECT COUNT(DISTINCT session_type) INTO v_filled_sessions
FROM ensemble_participants
WHERE ensemble_id = NEW.ensemble_id;

IF v_filled_sessions >= 7 THEN
        RAISE EXCEPTION 'All sessions are already filled for this ensemble. Cannot add more participants.';
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 트리거 생성
CREATE TRIGGER tr_validate_ensemble_participation
    BEFORE INSERT ON ensemble_participants
    FOR EACH ROW EXECUTE FUNCTION validate_ensemble_participation();

-- 같은 ensemble이 같은 요일에 겹치는 시간표를 가질 수 없도록 배타 제약조건
CREATE EXTENSION IF NOT EXISTS btree_gist;