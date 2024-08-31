import {createContext, Dispatch, SetStateAction, useContext, useEffect, useState} from "react";
import {useAtom} from "jotai/index";
import {selectedHairShopAtom} from "@/atoms";
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
    const [selectedHairShop] = useAtom(selectedHairShopAtom)
    const [shopDetail, setShopDetail] = useState<HairShopDetail>()
    const [selectedTimeSlot, setSelectedTimeSlot] = useState<TimeSlot>()

    useEffect(() => {
        if (!selectedHairShop) return
        fetchShopDetail(selectedHairShop.shopId).then(response => setShopDetail(response))
    }, [selectedHairShop])

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
