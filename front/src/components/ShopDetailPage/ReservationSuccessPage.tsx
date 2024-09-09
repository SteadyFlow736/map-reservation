import {useSetAtom} from "jotai";
import {selectedHairShopAtom} from "@/atoms";
import {
    ShopMainPageContext,
    ShopSubPageContext,
    TimeSlotContext
} from "@/components/ShopDetailPage/ShopDetailWrapperPage";
import {useContext, useEffect, useState} from "react";
import {ArrowLeftIcon} from "@heroicons/react/24/outline";
import {CheckCircleIcon, XMarkIcon} from "@heroicons/react/16/solid";
import Time from "@/utils/Time";
import {useAtomValue} from "jotai/index";
import {fetchShopDetail} from "@/api/hairShop";
import ContainerLoader from "@/components/Loaders/ContainerLoader";
import useCancelHairShopReservation from "@/hooks/useCancelHairShopReservation";

interface ReservationSuccessPageProps {
    reservationId: number
}

/**
 * 예약 성공 페이지.
 * 예약 성공 했음을 사용자에게 보여준다.
 * TODO: 예약 아이디로 내용을 가져와야 한다.
 */
function ReservationSuccessPage({reservationId}: ReservationSuccessPageProps) {

    return (
        <div className="bg-white">
            <StickyNavBar/>
            <Banner/>
            <SuccessInfo/>
            <CancelButton reservationId={reservationId}/>
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
        setShopMainPage({_tag: 'ShopDetail'})
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

/**
 * 예약 확정 배너. 예약이 확정된 것을 사용자들이 충분히 인지할 수 있도록 한다.
 */
function Banner() {
    return (
        <div className="bg-blue-500 text-white p-3 flex items-center gap-2">
            <CheckCircleIcon className="h-4"/>
            <p className="">예약 확정</p>
        </div>
    );
}

/**
 * 예왁 확정 정보
 */
function SuccessInfo() {
    const selectedHairShop = useAtomValue(selectedHairShopAtom)
    const [selectShopDetail, setSelectShopDetail] = useState<HairShopDetail>()
    const {selectedTimeSlot} = useContext(TimeSlotContext)

    useEffect(() => {
        if (selectedHairShop?.shopId) {
            fetchShopDetail(selectedHairShop.shopId).then(shopDetail => setSelectShopDetail(shopDetail))
        }
    }, [selectedHairShop]);

    if (!selectedTimeSlot) return null
    if (!selectShopDetail) return <ContainerLoader/>

    return (
        <div className="p-3">
            <div className="rounded-xl border-2 border-black p-3 mt-3">
                <div>
                    <span className="mr-3 text-gray-300">헤어샵</span>
                    <span>{selectShopDetail.shopName}</span>
                </div>
                <div>
                    <span className="mr-3 text-gray-300">일정</span>
                    <span>{Time.formatDate(selectedTimeSlot.dateTime)}</span>
                </div>
            </div>
        </div>
    )
}

/**
 * 예약 취소 버튼
 */
function CancelButton({reservationId}: { reservationId: number }) {
    const selectedHairShop = useAtomValue(selectedHairShopAtom)
    const mutation = useCancelHairShopReservation()
    const {setShopMainPage} = useContext(ShopMainPageContext)

    const cancelReservation = () => {
        if (!selectedHairShop?.shopId) return
        // shopId가 아니고 reservationId를 넣어야 한다.
        mutation.mutate(reservationId, {
            onSuccess: () => {
                setShopMainPage({_tag: "ReservationCancelledPage"})
            },
            onError: () => {
            }
        })
    }

    return (
        <div className="p-3">
            <button
                className="btn w-full"
                onClick={cancelReservation}
            >
                예약 취소
            </button>
        </div>
    );
}

export default ReservationSuccessPage
