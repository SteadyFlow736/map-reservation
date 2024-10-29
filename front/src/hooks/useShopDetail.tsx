import {useQuery} from "@tanstack/react-query";
import {QueryKeys} from "@/config/queryClient";
import {fetchShopDetail} from "@/api/hairShop";

function useShopDetail(shopId: number | undefined) {
    return useQuery({
        queryKey: [QueryKeys.shopDetail, shopId],
        queryFn: () => fetchShopDetail(shopId!),
        enabled: !!shopId
    })
}

export default useShopDetail
