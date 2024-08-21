import {createContext, Dispatch, SetStateAction} from "react";
import {SubPage} from "@/properties/SubPage";

type ShopSubPageContextType = { shopSubPage: SubPage, setShopSubPage: Dispatch<SetStateAction<SubPage>> }
const defaultValue: ShopSubPageContextType = {
    shopSubPage: '홈', setShopSubPage: () => {
    }
}
export const ShopSubPageContext = createContext<ShopSubPageContextType>(defaultValue)
