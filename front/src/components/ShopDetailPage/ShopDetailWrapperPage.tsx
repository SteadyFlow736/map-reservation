import {createContext, Dispatch, SetStateAction, useContext, useEffect, useState} from "react";
import {useAtom} from "jotai/index";
import {selectedHairShopIdAtom} from "@/atoms";
import {fetchShopDetail} from "@/api";
import ShopDetailPage from "@/components/ShopDetailPage/ShopDetailPage";
import ReservationVerifyPage from "@/components/ShopDetailPage/ReservationVerifyPage";

export type MainPage = 'ShopDetail' | 'ReservationVerify'
export type SubPage = '홈' | '소식' | '예약' | '리뷰'

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

function ShopDetailWrapperPage() {
    const [mainPage, setMainPage] = useState<MainPage>('ShopDetail')
    const [subPage, setSubPage] = useState<SubPage>('홈')
    const [selectedHairShopId] = useAtom(selectedHairShopIdAtom)
    const [shopDetail, setShopDetail] = useState<HairShopDetail>()

    useEffect(() => {
        if (!selectedHairShopId) return
        fetchShopDetail(selectedHairShopId.shopId).then(response => setShopDetail(response))
    }, [selectedHairShopId])

    let mainPageToRender
    switch (mainPage) {
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
            <ShopMainPageContext.Provider value={{shopMainPage: mainPage, setShopMainPage: setMainPage}}>
                <ShopSubPageContext.Provider value={{shopSubPage: subPage, setShopSubPage: setSubPage}}>
                    {mainPageToRender}
                </ShopSubPageContext.Provider>
            </ShopMainPageContext.Provider>
        </>
    )
}

export default ShopDetailWrapperPage
