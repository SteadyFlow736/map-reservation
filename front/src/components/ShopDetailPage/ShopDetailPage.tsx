import ShopSubPage from "@/components/ShopSubPage/ShopSubPage";
import {useSetAtom} from "jotai/index";
import {selectedHairShopAtom} from "@/atoms";
import {XMarkIcon} from "@heroicons/react/16/solid";
import Image from "next/image";
import {useContext, useEffect, useState} from "react";
import Button from "@/components/Button";
import {useAtomValue} from "jotai";
import {ShopSubPageContext, SubPage} from "@/components/ShopDetailPage/ShopDetailWrapperPage";
import ContainerLoader from "@/components/Loaders/ContainerLoader";
import useShopDetail from "@/hooks/useShopDetail";
import CustomError from "@/components/Loaders/CustomError";

/**
 * 샵의 상세 정보를 보여주는 컴포넌트
 */
function ShopDetailPage() {
    const selectedHairShop = useAtomValue(selectedHairShopAtom)
    const {data, status} = useShopDetail(selectedHairShop?.shopId)

    if (status == 'pending') return <ContainerLoader/>
    if (status == 'error') return <CustomError/>

    return (
        <>
            <StickyNavBar/>
            <ShopHead shopDetail={data}/>
            {/* key를 전달함으로써 shopId가 변경될 때마다(다른 샵을 선택) ShopSubPage의 서브 페이지 상태를 리셋(홈)하도록 했다.
            https://react.dev/learn/managing-state#preserving-and-resetting-state */}
            <ShopSubPage key={selectedHairShop?.shopId}/>
        </>
    )
}

function StickyNavBar() {
    const setSelectedHairShop = useSetAtom(selectedHairShopAtom)

    const close = () => {
        setSelectedHairShop(undefined)
    }

    return (
        <div className="sticky top-0 bg-neutral-300 p-3">
            <XMarkIcon onClick={close} className="text-white size-6 hover:cursor-pointer"/>
        </div>
    )
}

function ShopHead({
                      shopDetail
                  }: {
    shopDetail: HairShopDetail
}) {
    const {setShopSubPage} = useContext(ShopSubPageContext)

    const navigateTo = (subPage: SubPage) => {
        setShopSubPage(subPage)
    }

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
            <p
                className="px-3 text-sm text-gray-800 hover:cursor-pointer"
                onClick={() => navigateTo("리뷰")}
            >
                방문자 리뷰 100건
            </p>

            {/* 예약 버튼 */}
            <div className="p-3">
                <Button onClick={() => navigateTo("예약")} label="예약"/>
            </div>
        </div>
    )
}

export default ShopDetailPage
