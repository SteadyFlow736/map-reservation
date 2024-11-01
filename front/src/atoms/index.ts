import {atom} from "jotai";

export const hairShopSearchResultAtom = atom<HairShopSearchResult>()
export const selectedHairShopAtom = atom<SelectedHairShopIdAtom>()
export const mapBoundsAtom = atom<MapBounds>()

export type SelectedHairShopIdAtom = {
    shopId: number,
    needPan: boolean
}

export type MapBounds = {
    minLongitude: number,
    maxLongitude: number,
    minLatitude: number,
    maxLatitude: number
}
