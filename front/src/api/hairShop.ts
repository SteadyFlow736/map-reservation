import Pageable from "@/dto/page/Pageable";
import {instance} from "@/config/axios";

/**
 * 헤어샵 검색 질의 API
 *
 * @param searchTerm 검색어
 * @param pageable 페이지 설정
 */
export async function fetchSearchResult(
    searchTerm: string,
    pageable: Pageable = {size: 10, page: 0, sort: ['name,asc']}):
    Promise<HairShopSearchResult> {

    const {data} = await instance.get("/api/hairshop", {
        params: {
            searchTerm,
            ...pageable,
        }
    })
    return data;
}

/**
 * 헤어샵 상세 정보 질의 API
 *
 * @param shopId 헤어샵 id
 */
export async function fetchShopDetail(shopId: number): Promise<HairShopDetail> {
    const {data} = await instance.get(`/api/hairshops/${shopId}`)
    return data
}
