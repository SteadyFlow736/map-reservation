import {naver_map_client_id} from "@/config/map";
import Script from "next/script";
import {useAtomValue} from "jotai";
import {hairShopSearchResultAtom} from "@/atoms";
import {useState} from "react";

const markers: naver.maps.Marker[] = []

function MainMap() {
    const [map, setMap] = useState<naver.maps.Map>()
    const hairShopSearchResult = useAtomValue(hairShopSearchResultAtom);

    // marker 표시
    markers.forEach(m => m.setMap(null))
    markers.length = 0
    if (map && hairShopSearchResult) {
        hairShopSearchResult.content.forEach(dto => {
            const latitude = parseFloat(dto.latitude)
            const longitude = parseFloat(dto.longitude)
            const marker = new naver.maps.Marker({
                position: new naver.maps.LatLng(latitude, longitude),
                map
            })
            markers.push(marker)
        })
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
