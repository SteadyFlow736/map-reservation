import axios from "axios";
import Pageable from "@/dto/Pageable";
import Time from "@/utils/Time";

const instance = axios.create({
    baseURL: "http://localhost:8080"
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

export {fetchSearchResult, fetchShopDetail, fetchReservationStatus}
