CREATE OR REPLACE FUNCTION generate_timeslot_schedule(
    p_timeslot_id INTEGER,
    p_start_date DATE DEFAULT NULL,
    p_end_date DATE DEFAULT NULL
)
    RETURNS TABLE(
                     scheduled_date DATE,
                     start_datetime TIMESTAMP WITH TIME ZONE,
                     end_datetime TIMESTAMP WITH TIME ZONE
                 ) AS $$
DECLARE
v_timeslot RECORD;
    v_event RECORD;
    v_current_date DATE;
    v_excluded_date TEXT;
    v_is_excluded BOOLEAN;
BEGIN
    -- 시간표 정보 조회
SELECT t.*, EXTRACT(DOW FROM CAST('2000-01-01' AS DATE) +
                             CASE t.day_of_week
                                 WHEN 'SUNDAY' THEN 0
                                 WHEN 'MONDAY' THEN 1
                                 WHEN 'TUESDAY' THEN 2
                                 WHEN 'WEDNESDAY' THEN 3
                                 WHEN 'THURSDAY' THEN 4
                                 WHEN 'FRIDAY' THEN 5
                                 WHEN 'SATURDAY' THEN 6
                                 END) as dow_numeric
INTO v_timeslot
FROM timeslots t
WHERE t.id = p_timeslot_id AND t.is_active = TRUE;

IF NOT FOUND THEN
        RETURN;
END IF;

    -- 이벤트 기간 조회
SELECT e.start_datetime::date, e.end_datetime::date
INTO v_event
FROM events e
WHERE e.id = v_timeslot.event_id;

-- 날짜 범위 설정 (파라미터가 있으면 우선 사용)
v_current_date := COALESCE(p_start_date, v_event.start_datetime);

    -- 해당 요일의 첫 번째 날짜 찾기
    WHILE EXTRACT(DOW FROM v_current_date) != v_timeslot.dow_numeric LOOP
            v_current_date := v_current_date + INTERVAL '1 day';
END LOOP;

    -- 이벤트 종료일까지 반복
    WHILE v_current_date <= COALESCE(p_end_date, v_event.end_datetime) LOOP
            -- 제외 날짜 체크
            v_is_excluded := FALSE;

FOR v_excluded_date IN
SELECT jsonb_array_elements_text(v_timeslot.excluded_dates)
           LOOP
    IF v_current_date = v_excluded_date::date THEN
                        v_is_excluded := TRUE;
EXIT;
END IF;
END LOOP;

            -- 제외되지 않은 날짜면 반환
            IF NOT v_is_excluded THEN
                scheduled_date := v_current_date;
                start_datetime := v_current_date + v_timeslot.start_time;
                end_datetime := v_current_date + v_timeslot.end_time;
                RETURN NEXT;
END IF;

            -- 다음 주 같은 요일로
            v_current_date := v_current_date + INTERVAL '7 days';
END LOOP;
END;
$$ LANGUAGE plpgsql;
