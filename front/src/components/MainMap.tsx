import {naver_map_client_id} from "@/config/map";
import Script from "next/script";
import {useAtomValue} from "jotai";
import {hairShopSearchResultAtom} from "@/atoms";
import {useEffect, useState} from "react";

function MainMap() {
    const [map, setMap] = useState<naver.maps.Map>()
    const [markers, setMarkers] = useState<naver.maps.Marker[]>([])
    const hairShopSearchResult = useAtomValue(hairShopSearchResultAtom);

    useEffect(() => {
        if (map && hairShopSearchResult) {
            markers.forEach(m => m.setMap(null)) // 기존 마커들을 맵에서 삭제
            const newMarkers: naver.maps.Marker[] = [] // 새로운 마커 리스트

            hairShopSearchResult?.content?.forEach(dto => {
                const latitude = parseFloat(dto.latitude)
                const longitude = parseFloat(dto.longitude)
                const marker = new naver.maps.Marker({
                    position: new naver.maps.LatLng(latitude, longitude),
                    map
                })
                newMarkers.push(marker)
            })
            setMarkers(newMarkers)
        }
    }, [map, hairShopSearchResult])

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
