/**
 * 헤어샵 예약 요청 DTO
 */
type HairShopReservationCreateRequest = {
    reservationTime: string // 예약 날짜와 시간. 타임존 없는 ISO-8601 포맷. 예: 2007-12-03T10:15:30.
}
