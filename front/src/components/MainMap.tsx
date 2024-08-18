import {naver_map_client_id} from "@/config/map";
import Script from "next/script";
import {useAtomValue} from "jotai";
import {hairShopSearchResultAtom, selectedHairShopIdAtom} from "@/atoms";
import {useEffect, useState} from "react";
import {useAtom} from "jotai/index";

// 보존될 필요가 있는 상태지만, 그 변화가 화면 렌더링을 트리거 하지 않음
const markerMap = new Map<number, naver.maps.Marker>()
let prevMarker: naver.maps.Marker | undefined = undefined

function MainMap() {
    const [map, setMap] = useState<naver.maps.Map>()
    const hairShopSearchResult = useAtomValue(hairShopSearchResultAtom);
    const [selectedHairShopId, setSelectedHairShopId] = useAtom(selectedHairShopIdAtom)

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
                    setSelectedHairShopId({
                        shopId: dto.shopId,
                        needPan: false
                    })
                })
                markerMap.set(dto.shopId, marker)
            })
        }
    }, [map, hairShopSearchResult, setSelectedHairShopId])

    // 지도 중심 변경을 위한 useEffect: 선택된 marker로 지도 중심 변경
    useEffect(() => {
        if (selectedHairShopId?.needPan) {
            const marker = markerMap.get(selectedHairShopId.shopId)
            if (marker && map) {
                const position = marker.getPosition()
                map.panTo(position)
                //const widthPixel = window.innerWidth
                //const point = map.getProjection().fromCoordToPoint(position)
            }
        }
    }, [selectedHairShopId, map]);

    // marker animation 전환을 위한 useEffect: 선택된 marker animation on, 이전 marker animation off
    useEffect(() => {
        prevMarker?.setAnimation(null)
        if (selectedHairShopId) {
            const marker = markerMap.get(selectedHairShopId.shopId)
            marker?.setAnimation(1)
            prevMarker = marker
        }
    }, [selectedHairShopId]);

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
