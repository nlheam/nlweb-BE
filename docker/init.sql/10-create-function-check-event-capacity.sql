-- 정원 초과 방지 함수
CREATE OR REPLACE FUNCTION check_event_capacity()
    RETURNS TRIGGER AS $$
DECLARE
v_max_participants INTEGER;
    v_current_approved INTEGER;
BEGIN
    -- 승인 상태로 변경하는 경우에만 체크
    IF NEW.application_status = 'APPROVED' AND
       (TG_OP = 'INSERT' OR OLD.application_status != 'APPROVED') THEN

        -- 이벤트 최대 정원 조회
SELECT max_participants INTO v_max_participants
FROM events WHERE id = NEW.event_id;

-- 정원 제한이 있는 경우에만 체크
IF v_max_participants IS NOT NULL THEN
            -- 현재 승인된 참가자 수 조회
SELECT COUNT(*) INTO v_current_approved
FROM event_participants
WHERE event_id = NEW.event_id
  AND application_status = 'APPROVED'
  AND id != COALESCE(NEW.id, -1);  -- 업데이트인 경우 자신 제외

-- 정원 초과 체크
IF v_current_approved >= v_max_participants THEN
                RAISE EXCEPTION 'Event capacity exceeded. Max participants: %, Current: %',
                    v_max_participants, v_current_approved;
END IF;
END IF;
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 트리거 생성
CREATE TRIGGER tr_check_event_capacity
    BEFORE INSERT OR UPDATE ON event_participants
    FOR EACH ROW EXECUTE FUNCTION check_event_capacity();