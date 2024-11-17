import React, {useEffect, useState} from "react";
import {emptySearchOnAtom, hairShopSearchResponseAtom, mapBoundsAtom, naverMapAtom} from "@/atoms";
import {fetchSearchResult} from "@/api/hairShop";
import {useAtomValue, useSetAtom} from "jotai";
import {useAtom} from "jotai/index";

function SearchBar() {
    const [searchTerm, setSearchTerm] = useState("");
    const map = useAtomValue(naverMapAtom)
    const setHairShopSearchResponse = useSetAtom(hairShopSearchResponseAtom)
    const [emptySearchOn, setEmptySearchOn] = useAtom(emptySearchOnAtom)

    const handleOnKeyDown = async (event: React.KeyboardEvent<HTMLInputElement>) => {
        if (event.key !== 'Enter') return
        await search()
    }

    const search = async () => {
        if (!map) return
        const bounds = map.getBounds()
        const searchCondition: HairShopSearchCondition = {
            searchTerm,
            minLongitude: bounds.minX().toString(),
            maxLongitude: bounds.maxX().toString(),
            minLatitude: bounds.minY().toString(),
            maxLatitude: bounds.maxY().toString()
        }
        const data = await fetchSearchResult(searchCondition)
        setHairShopSearchResponse(data)
    }

    useEffect(() => {
        if (map && emptySearchOn) {
            search()
            setEmptySearchOn(false)
        }
    }, [emptySearchOn, map, setEmptySearchOn]);

    return (
        <input
            className="m-4 p-3 border-2 border-lime-400 rounded"
            type="text"
            value={searchTerm}
            placeholder={"헤어샵 이름을 검색해 보세요."}
            onChange={e => setSearchTerm(e.target.value)}
            onKeyDown={handleOnKeyDown}
        />
    )
}

export default SearchBar
