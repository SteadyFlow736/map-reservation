import Script from "next/script";
import {useAtomValue} from "jotai";
import {hairShopSearchResultAtom, selectedHairShopAtom} from "@/atoms";
import {useEffect, useState} from "react";
import {useAtom} from "jotai/index";
import {naver_map_client_id} from "@/envs";
import ShopDetailColumn from "@/components/ShopDetailColumn";
import SearchColumn from "@/components/SearchColumn";

// 보존될 필요가 있는 상태지만, 그 변화가 화면 렌더링을 트리거 하지 않음
const markerMap = new Map<number, naver.maps.Marker>()
let prevMarker: naver.maps.Marker | undefined = undefined

// Coord를 x, y 픽셀만큼 옮긴 Coord를 리턴
function getCoordMovedBy(position: naver.maps.Coord, xPixel: number, yPixel: number, map: naver.maps.Map) {
    const offset = map.getProjection().fromCoordToOffset(position)
    const newPoint = new naver.maps.Point(offset.x + xPixel, offset.y + yPixel)
    return map.getProjection().fromOffsetToCoord(newPoint)
}

function MainMap() {
    const [map, setMap] = useState<naver.maps.Map>()
    const hairShopSearchResult = useAtomValue(hairShopSearchResultAtom);
    const [selectedHairShop, setSelectedHairShop] = useAtom(selectedHairShopAtom)

    /**
     * shop의 마커 html 리턴
     * svg 출처: https://www.svgrepo.com/svg/476893/marker
     * @param shopName 표시될 shop 이름
     */
    const getMarker = (shopName: string) => `
    <svg width="50px" height="50px" viewBox="0 0 1024 1024" class="icon"  version="1.1" xmlns="http://www.w3.org/2000/svg">
        <path d="M512 85.333333c-164.949333 0-298.666667 133.738667-298.666667 298.666667 0 164.949333 298.666667 554.666667 298.666667 554.666667s298.666667-389.717333 298.666667-554.666667c0-164.928-133.717333-298.666667-298.666667-298.666667z m0 448a149.333333 149.333333 0 1 1 0-298.666666 149.333333 149.333333 0 0 1 0 298.666666z" fill="#FF3D00" />
    </svg>
    <p class="">${shopName}</p>
    `

    // marker 생성을 위한 useEffect: 검색된 헤어샵 리스트의 marker 생성
    useEffect(() => {
        markerMap.forEach(m => m.setMap(null))
        markerMap.clear()
        if (map && hairShopSearchResult) {
            hairShopSearchResult.content.forEach(dto => {
                const latitude = parseFloat(dto.latitude)
                const longitude = parseFloat(dto.longitude)
                const marker = new naver.maps.Marker({
                    position: new naver.maps.LatLng(latitude, longitude),
                    clickable: true,
                    map,
                    icon: {
                        content: getMarker(dto.shopName)
                    }
                })
                naver.maps.Event.addListener(marker, 'click', (_) => {
                    setSelectedHairShop({
                        shopId: dto.shopId,
                        needPan: false
                    })
                })
                markerMap.set(dto.shopId, marker)
            })
        }
    }, [map, hairShopSearchResult, setSelectedHairShop])

    // 지도 중심 변경을 위한 useEffect: 선택된 marker로 지도 중심 변경
    useEffect(() => {
        if (selectedHairShop?.needPan) {
            const marker = markerMap.get(selectedHairShop.shopId)
            if (marker && map) {
                const position = marker.getPosition()
                const xPixelsFromLeftToCenter = window.innerWidth / 2
                // "app/p/page.tsx"에서 아래 pixel 확인 가능
                const widthShopDetailColumnInPixel = 384
                const widthSearchColumnInPixel = 384
                const widthGapInPixel = 12
                const xPixelsFromLeftToRemainingCenter =
                    (window.innerWidth - (widthSearchColumnInPixel + widthShopDetailColumnInPixel + widthGapInPixel)) / 2
                const xDiff = xPixelsFromLeftToRemainingCenter - xPixelsFromLeftToCenter
                const newPosition = getCoordMovedBy(position, xDiff, 0, map)
                map.panTo(newPosition)
                //const widthPixel = window.innerWidth
            }
        }
    }, [selectedHairShop, map]);

    // marker animation 전환을 위한 useEffect: 선택된 marker animation on, 이전 marker animation off
    useEffect(() => {
        prevMarker?.setAnimation(null)
        if (selectedHairShop) {
            const marker = markerMap.get(selectedHairShop.shopId)
            marker?.setAnimation(1)
            prevMarker = marker
        }
    }, [selectedHairShop]);

    return (
        <>
            <div id="map" className="w-full h-screen"/>
            <Script
                type="text/javascript"
                src={`https://oapi.map.naver.com/openapi/v3/maps.js?ncpClientId=${naver_map_client_id}`}
                onReady={() => {
                    const location = new naver.maps.LatLng(37.5656, 126.9769);
                    const mapOptions: naver.maps.MapOptions = {
                        center: location,
                        zoom: 17,
                        zoomControl: true,
                        zoomControlOptions: {
                            position: naver.maps.Position.TOP_RIGHT,
                        },
                    };
                    const map = new naver.maps.Map("map", mapOptions)
                    setMap(map)
                }}
            />
        </>
    )
}

export default MainMap
