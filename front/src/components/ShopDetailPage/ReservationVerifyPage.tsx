import {useSetAtom} from "jotai/index";
import {selectedHairShopAtom} from "@/atoms";
import {XMarkIcon} from "@heroicons/react/16/solid";
import {ArrowLeftIcon} from "@heroicons/react/24/outline";
import {createContext, useContext, useEffect, useState} from "react";
import {ShopMainPageContext, TimeSlotContext} from "@/components/ShopDetailPage/ShopDetailWrapperPage";
import Time from "@/utils/Time";
import useCreateHairShopReservation from "@/hooks/useCreateHairShopReservation";
import {useAtomValue} from "jotai";
import {isAxiosError} from "axios";
import ContainerLoader from "@/components/Loaders/ContainerLoader";
import useShopDetail from "@/hooks/useShopDetail";
import CustomError from "@/components/Loaders/CustomError";
import useAuthGuard from "@/hooks/useAuthGuard";

const CreateReservationCallContext = createContext<Function | undefined>(undefined)

/**
 * 고객이 예약 하려는 내용을 확인하고 확정하는 페이지
 */
function ReservationVerifyPage() {
    const {status} = useAuthGuard()
    const mutation = useCreateHairShopReservation()
    const selectedHairShop = useAtomValue(selectedHairShopAtom)
    const {selectedTimeSlot} = useContext(TimeSlotContext)
    const {setShopMainPage} = useContext(ShopMainPageContext)
    const [errorMessage, setErrorMessage] = useState<string>()

    // 예약 생성 callback
    const createReservation = () => {
        setErrorMessage(undefined)
        mutation.mutate(
            {
                shopId: selectedHairShop?.shopId,
                reservationDateTime: selectedTimeSlot?.dateTime
            },
            {
                onSuccess: (response) => {
                    setShopMainPage({_tag: 'ReservationSuccess', reservationId: response.reservationId})
                },
                onError: (error: Error) => {
                    if (isAxiosError(error)) {
                        const {message} = error.response?.data as CustomErrorResponse<any>;
                        setErrorMessage(message)
                    } else {
                        setErrorMessage(error.message)
                    }
                }
            })
    }

    if (status === 'loading') return <ContainerLoader/>
    if (mutation.isPending) return <ContainerLoader/>

    return (
        <div className="bg-white">
            <CreateReservationCallContext.Provider value={createReservation}>
                <StickyNavBar/>
                <ReservationInfo/>
                <Buttons/>
                <ErrorMessage message={errorMessage}/>
            </CreateReservationCallContext.Provider>
        </div>
    )
}

/**
 * 상단 고정 nav 바
 */
function StickyNavBar() {
    const setSelectedHairShop = useSetAtom(selectedHairShopAtom)
    const {setShopMainPage} = useContext(ShopMainPageContext)

    const back = () => {
        setShopMainPage({_tag: 'ShopDetail'})
    }

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
 * 예약 날짜, 시간 등 예약 하려는 정보 표시
 */
function ReservationInfo() {
    const selectedHairShop = useAtomValue(selectedHairShopAtom)
    const {selectedTimeSlot} = useContext(TimeSlotContext)
    const {data, status} = useShopDetail(selectedHairShop?.shopId)

    if (!selectedTimeSlot) return null
    if (status == 'pending') return <ContainerLoader/>
    if (status == 'error') return <CustomError/>

    return (
        <div className="p-3">
            <p>다음 내용이 맞는지 확인해 주세요</p>
            <div className="rounded-xl border-2 border-black p-3 mt-3">
                <div>
                    <span className="mr-3 text-gray-300">헤어샵</span>
                    <span>{data.shopName}</span>
                </div>
                <div>
                    <span className="mr-3 text-gray-300">일정</span>
                    <span>{Time.formatDate(selectedTimeSlot.dateTime)}</span>
                </div>
            </div>
        </div>
    );
}

/**
 * 이전 페이지로 돌아가는 버튼, 예약 확정 버튼 그룹 및 스타일링
 */
function Buttons() {
    return (
        <div className="border-t flex gap-1 p-3">
            <div className="flex-[1]">
                <BackButton/>
            </div>
            <div className="flex-[2]">
                <ConfirmButton/>
            </div>
        </div>
    );
}

/**
 * 이전 페이지로 돌아가는 버튼
 */
function BackButton() {
    const {setShopMainPage} = useContext(ShopMainPageContext)

    const goPrevious = () => {
        setShopMainPage({_tag: 'ShopDetail'})
    }
    return <button onClick={goPrevious} className="btn w-full">이전</button>
}

/**
 * 예약 확정 버튼
 */
function ConfirmButton() {
    const call = useContext(CreateReservationCallContext)

    return (
        <button
            className="btn w-full bg-green-400 text-white"
            onClick={call ? () => call() : undefined}
        >
            동의하고 예약하기
        </button>
    )
}

/**
 * 에러 메시지
 */
function ErrorMessage({message}: { message: string | undefined }) {
    if (!message) return null
    return (
        <div className="p-3">
            <p>{message}</p>
        </div>
    )
}

export default ReservationVerifyPage
