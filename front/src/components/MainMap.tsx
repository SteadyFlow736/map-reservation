'use client'

import Script from "next/script";
import {useState} from "react";

function MainMap() {

    return (
        <>
            <div id="map" style={{width: '100%', height: '400px'}}/>
            <Script
                type="text/javascript"
                src={`https://oapi.map.naver.com/openapi/v3/maps.js?ncpClientId=${process.env.NEXT_PUBLIC_NAVER_MAP_CLIENT_ID}`}
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
                }}
            />
        </>
    )
}

export default MainMap
