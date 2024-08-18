import {atom} from "jotai";

export const hairShopSearchResultAtom = atom<HairShopSearchResult>()
export const selectedHairShopIdAtom = atom<SelectedHairShopIdAtom>()

type SelectedHairShopIdAtom = {
    shopId: number,
    needPan: boolean
}
