import ShopSubPage from "@/components/ShopSubPage/ShopSubPage";
import {useAtom, useSetAtom} from "jotai/index";
import {selectedHairShopIdAtom} from "@/atoms";
import {XMarkIcon} from "@heroicons/react/16/solid";
import Image from "next/image";
import {useEffect, useState} from "react";
import {fetchShopDetail} from "@/api";

function ShopDetailPage() {
    const [selectedHairShopId] = useAtom(selectedHairShopIdAtom)
    const [shopDetail, setShopDetail] = useState<HairShopDetail>()

    useEffect(() => {
        if (!selectedHairShopId) return
        fetchShopDetail(selectedHairShopId.shopId).then(response => setShopDetail(response))
    }, [selectedHairShopId])

    if (!shopDetail) return <div>Loading</div>

    return (
        <>
            <StickyNavBar/>
            <ShopHead shopDetail={shopDetail}/>
            {/* key를 전달함으로써 shopId가 변경될 때마다(다른 샵을 선택) ShopSubPage의 서브 페이지 상태를 리셋(홈)하도록 했다.
            https://react.dev/learn/managing-state#preserving-and-resetting-state */}
            <ShopSubPage key={selectedHairShopId?.shopId}/>
        </>
    )
}

function StickyNavBar() {
    const setSelectedHairShopId = useSetAtom(selectedHairShopIdAtom)

    const close = () => {
        setSelectedHairShopId(undefined)
    }

    return (
        <div className="sticky top-0 bg-neutral-300 p-3">
            <XMarkIcon onClick={close} className="text-white size-6 hover:cursor-pointer"/>
        </div>
    )
}

function ShopHead({shopDetail}: { shopDetail: HairShopDetail }) {
    return (
        <div className="bg-white mb-2">
            {/* 대표 이미지 */}
            <div className="flex flex-row">
                {shopDetail.imageUrls.map((url, index) =>
                    <Image
                        className="flex-auto"
                        key={index + url}
                        src={url}
                        alt={'haircut image'}
                        width={10}
                        height={10}
                    />
                )}
            </div>

            {/* 상점 이름 */}
            <p className="p-3 pb-1 text-xl">{shopDetail.shopName}</p>

            {/* 방문자 리뷰 */}
            <p className="px-3 text-sm text-gray-800">방문자 리뷰 100건</p>

            {/* 예약 버튼 */}
            <div className="p-3">
                <div
                    className="p-2 border-2 border-gray-100 rounded
                    flex justify-center
                    hover:cursor-pointer
                    "
                >
                    예약
                </div>
            </div>
        </div>
    )
}

export default ShopDetailPage
