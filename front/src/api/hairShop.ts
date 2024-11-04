import Pageable from "@/dto/page/Pageable";
import {instance} from "@/config/axios";

/**
 * 헤어샵 검색 질의 API
 *
 * @param searchCondition 검색 조건
 * @param pageable 페이지 설정
 */
export async function fetchSearchResult(
    searchCondition: HairShopSearchCondition,
    pageable: Pageable = {size: 100, page: 0, sort: ['name,asc']}):
    Promise<HairShopSearchResponse> {

    const {data} = await instance.get("/api/hairshop", {
        params: {
            ...searchCondition,
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
