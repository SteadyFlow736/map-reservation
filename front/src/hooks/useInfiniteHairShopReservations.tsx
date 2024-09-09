import {fetchHairShopReservations} from "@/api/reservation";
import {useInfiniteQuery, useQuery} from "@tanstack/react-query";
import {QueryKeys} from "@/config/queryClient";

/**
 * 고객의 예얄 목록을 slice 형태로 가져온다.
 *
 * @param size 페이지 크기
 */
function useInfiniteHairShopReservations(size: number) {
    const initPageParam = 0 // 0 페이지부터 시작
    return useInfiniteQuery({
        queryKey: [QueryKeys.reservations],
        queryFn: ({pageParam}) => fetchHairShopReservations(size, pageParam),
        initialPageParam: initPageParam,
        getNextPageParam: (lastPage, _allPages, lastPageParam) => {
            return lastPage.last ? undefined : lastPageParam + 1
        }
    })
}

export default useInfiniteHairShopReservations
