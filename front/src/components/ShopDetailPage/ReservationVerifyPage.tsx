import {useSetAtom} from "jotai/index";
import {selectedHairShopIdAtom} from "@/atoms";
import {XMarkIcon} from "@heroicons/react/16/solid";
import {ArrowLeftIcon} from "@heroicons/react/24/outline";
import {useContext} from "react";
import {ShopMainPageContext, TimeSlotContext} from "@/components/ShopDetailPage/ShopDetailWrapperPage";
import Time from "@/utils/Time";

function ReservationVerifyPage() {
    return (
        <div className="bg-white">
            <StickyNavBar/>
            <ReservationInfo/>
            <Buttons/>
        </div>
    )
}

function StickyNavBar() {
    const setSelectedHairShopId = useSetAtom(selectedHairShopIdAtom)
    const {setShopMainPage} = useContext(ShopMainPageContext)

    const back = () => {
        setShopMainPage('ShopDetail')
    }

    const close = () => {
        setSelectedHairShopId(undefined)
    }

    return (
        <div className="sticky top-0 bg-neutral-300 p-3 flex justify-between">
            <ArrowLeftIcon onClick={back} className="text-white size-6 hover:cursor-pointer"/>
            <XMarkIcon onClick={close} className="text-white size-6 hover:cursor-pointer"/>
        </div>
    )
}

function ReservationInfo() {
    const {selectedTimeSlot, setSelectedTimeSlot} = useContext(TimeSlotContext)
    if (!selectedTimeSlot) return null

    return (
        <div className="p-3">
            <p>다음 내용이 맞는지 확인해 주세요</p>
            <div className="rounded-xl border-2 border-black p-3 mt-3">
                <span className="mr-3 text-gray-300">일정</span>
                <span>{Time.formatDate(selectedTimeSlot.dateTime)}</span>
            </div>
        </div>
    );
}

function Buttons() {
    const {setShopMainPage} = useContext(ShopMainPageContext)

    const goPrevious = () => {
        setShopMainPage('ShopDetail')
    }

    return (
        <div className="border-t flex gap-1 p-3">
            <button onClick={goPrevious} className="btn flex-[1]">이전</button>
            <button className="btn flex-[2] bg-green-400 text-white">동의하고 예약하기</button>
        </div>
    );
}

export default ReservationVerifyPage
