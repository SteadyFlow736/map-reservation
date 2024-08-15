import axios from "axios";
import Pageable from "@/types/Pageable";

const instance = axios.create({
    baseURL: "http://localhost:8080"
})

// 검색어 질의 API
async function fetchSearchResult(searchTerm: string, pageable: Pageable) {
    const {data} = await instance.get("/api/hairshop", {
        params: {
            searchTerm,
            ...pageable,
            sort: pageable.sort.join(',')
        }
    })
    return data;
}

export {fetchSearchResult}
