/**
 * 헤어샵 예약 현황
 *
 * date - 기준 날짜. 예: "2024-08-25"
 * openingTime - 영업 시작 시간. 예: "10:00"
 * closingTime - 영업 종료 시간. 예: "20:00"
 * dateTimeAndStatuses - 예약이 잡힌 시간과 상태들.
 *  예: [{reservedDateTime: "11:30", status: "RESERVED"}, {reservedDateTime: "14:30", status: "CANCELLED"}]
 */
type HairShopReservationStatus = {
    date: string
    openingTime: string
    closingTime: string
    reservedTimes: string[]
}
