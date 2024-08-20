import {useAtomValue} from "jotai";
import {selectedHairShopIdAtom} from "@/atoms";
import {useQuery} from "@tanstack/react-query";
import {QueryKeys} from "@/config/queryClient";
import {fetchShopDetail} from "@/api";

function ShopSubPageHome() {
    const selectHairShopId = useAtomValue(selectedHairShopIdAtom)?.shopId
    const {data, isSuccess, isFetching} = useQuery({
        queryKey: [QueryKeys.shopDetail, [selectHairShopId]],
        queryFn: () => fetchShopDetail(selectHairShopId!),
        enabled: !!selectHairShopId
    })

    return (
        <div className="bg-white p-3">
            {/* 주소 */}
            <div>{data?.roadAddress}</div>

            {/* 영업 시간 */}

            {/* 전화번호 */}

        </div>
    )
}

export default ShopSubPageHome
