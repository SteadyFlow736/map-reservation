import {ArrowLeftIcon} from "@heroicons/react/24/outline";
import {XMarkIcon} from "@heroicons/react/16/solid";
import {useSetAtom} from "jotai";
import {selectedHairShopAtom} from "@/atoms";
import {useContext} from "react";
import {
    ShopMainPageContext,
    ShopSubPageContext,
    TimeSlotContext
} from "@/components/ShopDetailPage/ShopDetailWrapperPage";


function ReservationCancelledPage() {
    return (
        <div className="bg-white">
            <StickyNavBar/>
            <ConfirmCancelled/>
        </div>
    )
}

/**
 * 상단 고정 nav 바
 */
function StickyNavBar() {
    const setSelectedHairShop = useSetAtom(selectedHairShopAtom)
    const {setShopMainPage} = useContext(ShopMainPageContext)
    const {setShopSubPage} = useContext(ShopSubPageContext)
    const {setSelectedTimeSlot} = useContext(TimeSlotContext)

    /**
     * 현재 상점의 상세 페이지로 돌아간다.
     * 서브 페이지도 홈으로 되돌리고 선택했던 시간도 초기화한다.
     */
    const back = () => {
        setShopSubPage('홈')
        setShopMainPage('ShopDetail')
        setSelectedTimeSlot(undefined)
    }

    /**
     * 상점 페이지를 닫는다.
     */
    const close = () => {
        setSelectedHairShop(undefined)
    }

    return (
        <div className="sticky top-0 bg-neutral-300 p-3 flex justify-between">
            <ArrowLeftIcon onClick={back} className="text-white size-6 hover:cursor-pointer"/>
            <XMarkIcon onClick={close} className="text-white size-6 hover:cursor-pointer"/>
        </div>
    )
}

function ConfirmCancelled() {
    return (
        <div className="p-3">
            예약이 취소 되었습니다.
        </div>
    );
}

export default ReservationCancelledPage
