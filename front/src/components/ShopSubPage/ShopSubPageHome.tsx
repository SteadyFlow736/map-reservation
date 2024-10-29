import {useAtomValue} from "jotai";
import {selectedHairShopAtom} from "@/atoms";
import useShopDetail from "@/hooks/useShopDetail";
import ContainerLoader from "@/components/Loaders/ContainerLoader";
import CustomError from "@/components/Loaders/CustomError";

function ShopSubPageHome() {
    const selectHairShopId = useAtomValue(selectedHairShopAtom)?.shopId
    const {data, status} = useShopDetail(selectHairShopId)

    if (status == 'pending') return <ContainerLoader/>
    if (status == 'error') return <CustomError/>

    return (
        <div className="bg-white p-3">
            {/* 주소 */}
            <div>{data.roadAddress}</div>

            {/* 영업 시간 */}

            {/* 전화번호 */}

        </div>
    )
}

export default ShopSubPageHome
