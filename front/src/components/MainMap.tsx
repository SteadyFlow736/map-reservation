import {naver_map_client_id} from "@/config/map";
import Script from "next/script";
import {useAtomValue} from "jotai";
import {hairShopSearchResultAtom, selectedHairShopIdAtom} from "@/atoms";
import {useState} from "react";
import {useAtom} from "jotai/index";

const markerMap = new Map<number, naver.maps.Marker>()

function MainMap() {
    const [map, setMap] = useState<naver.maps.Map>()
    const hairShopSearchResult = useAtomValue(hairShopSearchResultAtom);
    const [selectedHairShopId] = useAtom(selectedHairShopIdAtom)

    // marker 표시
    markerMap.forEach(m => m.setMap(null))
    markerMap.clear()
    if (map && hairShopSearchResult) {
        hairShopSearchResult.content.forEach(dto => {
            const latitude = parseFloat(dto.latitude)
            const longitude = parseFloat(dto.longitude)
            const marker = new naver.maps.Marker({
                position: new naver.maps.LatLng(latitude, longitude),
                clickable: true,
                map
            })
            markerMap.set(dto.shopId, marker)
        })
    }

    // 선택된 marker로 지도 중심 변경
    if (selectedHairShopId) {
        const marker = markerMap.get(selectedHairShopId)
        if (marker && map) {
            const position = marker.getPosition()
            map.panTo(position)
            //const widthPixel = window.innerWidth
            //const point = map.getProjection().fromCoordToPoint(position)
        }
    }

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
