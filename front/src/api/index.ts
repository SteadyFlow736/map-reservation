import axios from "axios";

const instance = axios.create({
    baseURL: "http://localhost:8080"
})

// 검색어 질의 API
async function fetchSearchResult(keyword: string) {
    const {data} = await instance.get("/api/search", {
        params: {
            keyword
        }
    })
    return data;
}

export {fetchSearchResult}
