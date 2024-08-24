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
}

export default Time
