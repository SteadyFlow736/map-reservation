import {createContext, Dispatch, SetStateAction, useContext, useEffect, useState} from "react";
import {useAtom} from "jotai/index";
import {selectedHairShopIdAtom} from "@/atoms";
import {fetchShopDetail} from "@/api";
import ShopDetailPage from "@/components/ShopDetailPage/ShopDetailPage";
import ReservationVerifyPage from "@/components/ShopDetailPage/ReservationVerifyPage";

export type MainPage = 'ShopDetail' | 'ReservationVerify'
export type SubPage = '홈' | '소식' | '예약' | '리뷰'
export type TimeSlot = {
    dateTime: Date
    reserved: boolean
}

type ShopMainPageContextType = { shopMainPage: MainPage, setShopMainPage: Dispatch<SetStateAction<MainPage>> }
export const ShopMainPageContext = createContext<ShopMainPageContextType>({
    shopMainPage: 'ShopDetail', setShopMainPage: () => {
    }
})

type ShopSubPageContextType = { shopSubPage: SubPage, setShopSubPage: Dispatch<SetStateAction<SubPage>> }
export const ShopSubPageContext = createContext<ShopSubPageContextType>({
    shopSubPage: '홈', setShopSubPage: () => {
    }
})

type TimeSlotContextType = {
    selectedTimeSlot: TimeSlot | undefined,
    setSelectedTimeSlot: Dispatch<SetStateAction<TimeSlot | undefined>>
}
export const TimeSlotContext = createContext<TimeSlotContextType>({
    selectedTimeSlot: undefined, setSelectedTimeSlot: () => {
    }
})

function ShopDetailWrapperPage() {
    const [shopMainPage, setShopMainPage] = useState<MainPage>('ShopDetail')
    const [shopSubPage, setShopSubPage] = useState<SubPage>('홈')
    const [selectedHairShopId] = useAtom(selectedHairShopIdAtom)
    const [shopDetail, setShopDetail] = useState<HairShopDetail>()
    const [selectedTimeSlot, setSelectedTimeSlot] = useState<TimeSlot>()

    useEffect(() => {
        if (!selectedHairShopId) return
        fetchShopDetail(selectedHairShopId.shopId).then(response => setShopDetail(response))
    }, [selectedHairShopId])

    let mainPageToRender
    switch (shopMainPage) {
        case 'ShopDetail':
            mainPageToRender = <ShopDetailPage shopDetail={shopDetail}/>
            break
        case 'ReservationVerify':
            mainPageToRender = <ReservationVerifyPage/>
            break
        default:
            mainPageToRender = <ShopDetailPage shopDetail={shopDetail}/>
    }

    return (
        <>
            <ShopMainPageContext.Provider value={{shopMainPage, setShopMainPage}}>
                <ShopSubPageContext.Provider value={{shopSubPage, setShopSubPage}}>
                    <TimeSlotContext.Provider value={{selectedTimeSlot, setSelectedTimeSlot}}>
                        {mainPageToRender}
                    </TimeSlotContext.Provider>
                </ShopSubPageContext.Provider>
            </ShopMainPageContext.Provider>
        </>
    )
}

export default ShopDetailWrapperPage
