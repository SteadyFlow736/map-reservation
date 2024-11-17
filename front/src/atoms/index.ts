import {atom} from "jotai";

export const hairShopSearchResponseAtom = atom<HairShopSearchResponse>()
export const selectedHairShopAtom = atom<SelectedHairShopIdAtom>()
export const mapBoundsAtom = atom<MapBounds>()
export const naverMapAtom = atom<naver.maps.Map>()
export const emptySearchOnAtom = atom(true)

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
