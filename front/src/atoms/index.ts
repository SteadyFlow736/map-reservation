import {atom} from "jotai";

export const hairShopSearchResultAtom = atom<HairShopSearchResult>()
export const selectedHairShopAtom = atom<SelectedHairShopIdAtom>()

type SelectedHairShopIdAtom = {
    shopId: number,
    needPan: boolean
}
