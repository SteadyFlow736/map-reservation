/**
 * 백엔드로부터 받은 날짜, 시간 문자열과 자바스크립트의 Date 객체간의 변환을 위한 유틸리티 클래스
 */
class Time {
    /**
     * Date 객체의 연, 월, 일을 yyyy-MM-dd 형식의 문자열로 변환
     *
     * @param dateTime 변환할 객체
     * @return yyyy-MM-dd 문자열
     */
    static dateTimeToDateString(dateTime: Date): string {
        const year = dateTime.getFullYear()
        const month = String(dateTime.getMonth() + 1).padStart(2, '0') // 월을 두 자리로 맞춤. 1 -> 01
        const day = String(dateTime.getDate()).padStart(2, '0') // 일을 두 자리로 맞춤. 3 -> 03
        return `${year}-${month}-${day}`
    }

    /**
     * date, time 문자열로부터 Date 객체를 만들어 리턴
     *
     * @param dateString yyyy-MM-dd 문자열
     * @param timeString HH:mm 문자열
     * @return Date 객체
     */
    static stringToDate(dateString: string, timeString: string): Date {
        const [year, month, day] = dateString.split('-').map(Number);
        const [hours, minutes] = timeString.split(':').map(Number);

        // Date 객체 생성 (월은 0부터 시작하므로 1을 빼줌)
        return new Date(year, month - 1, day, hours, minutes);
    }

    /**
     * Date 객체에 원하는 분을 더한 객체를 리턴
     *
     * @param dateTime 대상 Date 객체
     * @param minutes 더할 분
     * @return 결과 Date 객체
     */
    static addMinutesToDateTime(dateTime: Date, minutes: number): Date {
        const newTime = dateTime.getTime() + minutes * 60000; // 1분 = 60초 = 60,000 밀리초
        return new Date(newTime)
    }

    /**
     * Date 객체로부터 사람이 알아보기 쉬운 문자열을 리턴
     *
     * @param date 대상 Date 객체
     */
    static formatDate(date: Date) {
        const daysOfWeek = ['일', '월', '화', '수', '목', '금', '토'];

        const month = date.getMonth() + 1; // 월은 0부터 시작하므로 1을 더한다
        const day = date.getDate();
        const dayOfWeek = daysOfWeek[date.getDay()]; // 요일을 한글로 변환
        let hour = date.getHours();
        const minutes = date.getMinutes();

        // 오전/오후 처리
        const period = hour >= 12 ? '오후' : '오전';

        // 12시간제로 변환 (12시를 기준으로 나머지 계산)
        hour = hour % 12;
        hour = hour === 0 ? 12 : hour; // 0시일 때 12시로 표시

        // 분을 2자리 숫자로 표시
        const formattedMinutes = minutes < 10 ? `0${minutes}` : minutes;

        return `${month}.${day} (${dayOfWeek}) ${period} ${hour}:${formattedMinutes}`;
    }

    /**
     * Date 객체에서 로컬 타임을 ISO 형식으로(예:2024-12-03T10:15:30) 변경
     *
     * Date 객체는 로컬 타임과 timezone 정보를 가지고 있기에 toISOString 메서드를 호출하면 UTC(+0) 형식으로 출력된다.
     * 그것은 원하는 바가 아니다.
     * 예를들어, 로컬(UTC+9) 타임으로 2024-12-03T10:15:30인 Date 객체를
     * toISOString으로 출력하면 2024-12-03T01:15:30z 로 출력될 것이다.
     * 원하는 것은 로컬 타임 그대로 즉, "2024-12-03T10:15:30"로 출력되는 것이다.
     *
     * @param date 대상 Date 객체
     */
    static formatLocalDateToISO(date: Date): string {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0'); // 월은 0부터 시작하므로 +1 필요
        const day = String(date.getDate()).padStart(2, '0');

        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        const seconds = String(date.getSeconds()).padStart(2, '0');

        return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`;
    }

}

export default Time
