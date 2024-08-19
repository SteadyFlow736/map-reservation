import {useAtom, useSetAtom} from "jotai";
import {selectedHairShopIdAtom} from "@/atoms";
import {useEffect, useState} from "react";
import {fetchShopDetail} from "@/api";
import {XMarkIcon} from "@heroicons/react/16/solid";
import Image from "next/image";
import {useRouter} from "next/navigation";
import ShopMenu from "@/components/ShopMenu";

function ShopDetailColumn() {
    const [selectedHairShopId] = useAtom(selectedHairShopIdAtom)
    const [shopDetail, setShopDetail] = useState<HairShopDetail>()

    const showOrHidden = selectedHairShopId ? "transform-x-0 opacity-100" : "-translate-x-full opacity-0"

    useEffect(() => {
        if (!selectedHairShopId) return
        fetchShopDetail(selectedHairShopId.shopId).then(response => setShopDetail(response))
    }, [selectedHairShopId])

    if (!shopDetail) return <div>Loading</div>

    return (
        <div className={`
            max-h-screen w-96 z-10 my-3 rounded-2xl shadow-2xl
            bg-gray-100
            ${showOrHidden}
            transition
            overflow-hidden
            `}
        >
            <StickyNavBar/>
            <ShopHead shopDetail={shopDetail}/>
            <ShopMenu/>
        </div>
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
            <p>{shopDetail.shopName}</p>

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

export default ShopDetailColumn
