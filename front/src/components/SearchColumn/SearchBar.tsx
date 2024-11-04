import React, {useState} from "react";
import {hairShopSearchResponseAtom, mapBoundsAtom} from "@/atoms";
import {fetchSearchResult} from "@/api/hairShop";
import {useAtomValue, useSetAtom} from "jotai";

function SearchBar() {
    const [searchTerm, setSearchTerm] = useState("");
    const setHairShopSearchResponse = useSetAtom(hairShopSearchResponseAtom)
    const mapBounds = useAtomValue(mapBoundsAtom)

    const handleOnKeyDown = async (event: React.KeyboardEvent<HTMLInputElement>) => {
        if (event.key !== 'Enter') return
        await search()
    }

    const search = async () => {
        const searchCondition: HairShopSearchCondition = {
            searchTerm,
            minLongitude: mapBounds?.minLongitude.toString(),
            maxLongitude: mapBounds?.maxLongitude.toString(),
            minLatitude: mapBounds?.minLatitude.toString(),
            maxLatitude: mapBounds?.maxLatitude.toString()
        }
        const data = await fetchSearchResult(searchCondition)
        setHairShopSearchResponse(data)
    }

    return (
        <input
            className="m-4 p-3 border-2 border-lime-400 rounded"
            type="text"
            value={searchTerm}
            onChange={e => setSearchTerm(e.target.value)}
            onKeyDown={handleOnKeyDown}
        />
    )
}

export default SearchBar
