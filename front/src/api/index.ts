import axios from "axios";
import Pageable from "@/dto/Pageable";
import Time from "@/utils/Time";
import {api_base_url} from "@/envs";

const baseURL = api_base_url

const instance = axios.create({
    baseURL,
    // https://developer.mozilla.org/en-US/docs/Web/API/XMLHttpRequest/withCredentials
    // csrf token을 받아오려면 당연히 받아올 토근과 관련된 세션 아이디도 요청에 같이 포함되어야 한다.
    // cookie에 세션 아이디가 있으므로, withCredentials를 true로 하여 쿠키를 보내도록 한다.
    withCredentials: true
})

// 검색어 질의 API
async function fetchSearchResult(
    searchTerm: string,
    pageable: Pageable = {size: 10, page: 0, sort: ['name,asc']}):
    Promise<HairShopSearchResult> {

    const {data} = await instance.get("/api/hairshop", {
        params: {
            searchTerm,
            ...pageable,
            sort: pageable.sort.join(',')
        }
    })
    return data;
}

// 상점 상세 정보 질의 API
async function fetchShopDetail(id: number): Promise<HairShopDetail> {
    const {data} = await instance.get(`/api/hairshops/${id}`)
    return data
}

// 상점 예약 현황 질의 API
async function fetchReservationStatus(id: number, date: Date): Promise<HairShopReservationStatus> {
    const {data} = await instance
        .get(`/api/hairshops/${id}/reservations/status`, {
            params: {
                targetDate: Time.dateTimeToDateString(date)
            }
        })
    return data
}

// csrf token 질의 API
async function fetchCsrfToken(): Promise<CsrfToken> {
    const {data} = await instance
        .get<CsrfTokenResponse>(`/api/csrf-token`)
    return data.csrfToken
}

async function signup(email: string, password: string) {
    const csrfToken = await fetchCsrfToken()
    await instance.post(`/api/customers`,
        {
            email,
            password
        },
        {
            headers: {
                [csrfToken.headerName]: csrfToken.token
            },
        }
    )
}

// 로그인 API
async function login(username: string, password: string): Promise<CustomerInfo> {
    const csrfToken = await fetchCsrfToken()
    const form = new FormData
    form.append("username", username)
    form.append("password", password)
    const {data} = await instance.post('/api/login', form, {
        headers: {
            [csrfToken.headerName]: csrfToken.token
        }
    })
    return data
}

// 세션 정보 API
async function fetchSession(): Promise<CustomerInfo> {
    const {data} = await instance.get('/api/customers/me')
    return data
}

// 로그아웃 API
async function logout() {
    const csrfToken = await fetchCsrfToken()
    return await instance.post('/api/logout', null, {
        headers: {
            [csrfToken.headerName]: csrfToken.token
        }
    })
}

/**
 * 헤어샵 예약 API
 *
 * @param shopId 헤어샵 Id
 * @param reservationDateTime 예약 날짜, 시간
 */
async function createHairShopReservation({shopId, reservationDateTime}: CreateReservationParams): Promise<void> {
    if (!shopId || !reservationDateTime) return
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

export {
    fetchSearchResult,
    fetchShopDetail,
    fetchReservationStatus,
    signup,
    login,
    fetchSession,
    logout,
    createHairShopReservation
}
