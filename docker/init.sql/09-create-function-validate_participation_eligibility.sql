CREATE OR REPLACE FUNCTION validate_participation_eligibility()
    RETURNS TRIGGER AS $$
DECLARE
v_event_type VARCHAR(30);
    v_parent_event INTEGER;
    v_user_status VARCHAR(20);
    v_has_parent_approval BOOLEAN := FALSE;
BEGIN
    -- 이벤트 정보 조회
SELECT event_type, parent_event INTO v_event_type, v_parent_event
FROM events WHERE id = NEW.event_id;

-- 사용자 상태 확인
SELECT status INTO v_user_status
FROM users WHERE id = NEW.user_id;

-- 활성 사용자만 참가 가능
IF v_user_status != 'ACTIVE' THEN
        RAISE EXCEPTION 'Only active users can participate in events';
END IF;

    -- 하위 이벤트인 경우 상위 이벤트 참가 승인 확인
    IF v_parent_event IS NOT NULL AND v_event_type != 'EVENT_APPLICATION' THEN
SELECT EXISTS(
    SELECT 1 FROM event_participants ep
                      JOIN events e ON ep.event_id = e.id
    WHERE ep.user_id = NEW.user_id
      AND e.parent_event = (
        SELECT COALESCE(root_event, id) FROM events WHERE id = NEW.event_id
    )
      AND e.event_type = 'EVENT_APPLICATION'
) INTO v_has_parent_approval;

IF NOT v_has_parent_approval THEN
            RAISE EXCEPTION 'User must be approved for parent event before applying to sub-events';
END IF;
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 트리거 생성
CREATE TRIGGER tr_validate_participation_eligibility
    BEFORE INSERT ON event_participants
    FOR EACH ROW EXECUTE FUNCTION validate_participation_eligibility();