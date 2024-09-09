import React, {useState} from "react";
import {hairShopSearchResultAtom} from "@/atoms";
import {fetchSearchResult} from "@/api/hairShop";
import {useSetAtom} from "jotai";

function SearchBar() {
    const [searchTerm, setSearchTerm] = useState("");
    const setSearchResult = useSetAtom(hairShopSearchResultAtom)

    const handleOnKeyDown = async (event: React.KeyboardEvent<HTMLInputElement>) => {
        if (event.key !== 'Enter') return
        await search()
    }

    const search = async () => {
        const data = await fetchSearchResult(searchTerm)
        setSearchResult(data)
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
