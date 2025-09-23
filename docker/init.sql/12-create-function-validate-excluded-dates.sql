-- excluded_dates 날짜 형식 검증 함수
CREATE OR REPLACE FUNCTION validate_excluded_dates(dates jsonb)
    RETURNS BOOLEAN AS $$
DECLARE
date_elem jsonb;
BEGIN
    -- 빈 배열은 허용
    IF jsonb_array_length(dates) = 0 THEN
        RETURN TRUE;
END IF;

    -- 각 요소가 유효한 날짜 문자열인지 검증
FOR date_elem IN SELECT jsonb_array_elements_text(dates)
                            LOOP
BEGIN
                PERFORM date_elem::text::date;  -- 날짜로 변환 시도
EXCEPTION WHEN OTHERS THEN
                RETURN FALSE;
END;
END LOOP;

RETURN TRUE;
END;
$$ LANGUAGE plpgsql IMMUTABLE;

-- 제약조건 추가
ALTER TABLE timeslots
    ADD CONSTRAINT chk_excluded_dates_valid
        CHECK (validate_excluded_dates(excluded_dates));
