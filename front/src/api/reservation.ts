import {instance} from "@/config/axios";
import Time from "@/utils/Time";
import {fetchCsrfToken} from "@/api/auth";

/**
 * 헤어샵 예약 API
 *
 * @param shopId 헤어샵 Id
 * @param reservationDateTime 예약 날짜, 시간
 */
export async function createHairShopReservation({shopId, reservationDateTime}: CreateReservationParams): Promise<{
    reservationId: number
}> {
    if (!shopId || !reservationDateTime) throw new Error("헤어샵 시술 예약을 하려면 헤어샵 id와 예약 시간을 지정해야 합니다.")
    const request: HairShopReservationCreateRequest = {
        reservationTime: Time.formatLocalDateToISO(reservationDateTime)
    }
    const csrfToken = await fetchCsrfToken()
    const {data} = await instance.post(`/api/hairshops/${shopId}/reservations`, request, {
        headers: {
            [csrfToken.headerName]: csrfToken.token
        }
    })
    return data
}

type HairShopReservationCreateRequest = {
    reservationTime: string // 예약 날짜와 시간. 타임존 적용하지 않은 로컬 타임의 ISO-8601 포맷. 예: 2024-12-03T10:15:30.
}

export type CreateReservationParams = {
    shopId: number | undefined
    reservationDateTime: Date | undefined
}

/**
 * 고객 헤어샵 예약 리스트 조회 API (Slice)
 *
 * @param size 페이지 크기
 * @param page 페이지 번호
 * @param sort 정렬 옵션
 */
export async function fetchHairShopReservations(
    size: number = 10,
    page: number = 0,
    sort: string[] = ['reservationTime,desc']
): Promise<Slice<HairShopReservationDto>> {
    const {data} = await instance.get(`/api/reservations`, {
        params: {
            size,
            page,
            sort
        }
    })
    return data
}

/**
 * 헤어샵 예약 현황 질의 API
 *
 * @param shopId 헤어샵 id
 * @param date 대상 날짜
 */
export async function fetchReservationStatus(shopId: number, date: Date): Promise<HairShopReservationStatus> {
    const {data} = await instance
        .get(`/api/hairshops/${shopId}/reservations/status`, {
            params: {
                targetDate: Time.dateTimeToDateString(date)
            }
        })
    return data
}

/**
 * 헤어샵 예약 취소 API
 *
 * @param reservationId 예약 id
 */
export async function cancelHairShopReservation(reservationId: number): Promise<void> {
    const csrfToken = await fetchCsrfToken()
    const {data} = await instance.post(`/api/reservations/${reservationId}`, null, {
        headers: {
            [csrfToken.headerName]: csrfToken.token
        }
    });
    return data
}
