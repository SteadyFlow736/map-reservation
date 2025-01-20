import StoreSummaryCard from "@/components/StoreSummaryCard";
import {hairShopSearchResponseAtom} from "@/atoms";
import {useAtomValue} from "jotai";

function NoSearchResult() {
    return (
        <div className="p-4">검색어와 일치하는 헤어샵이 없습니다.</div>
    );
}

function InitResult() {
    return (
        <div className="p-4"></div>
    )
}

function SearchResultList() {
    const hairShopSearchResponse = useAtomValue(hairShopSearchResponseAtom)
    const data = hairShopSearchResponse?.page.content

    return (
        <div className="overflow-y-auto divide-y">
            {data === undefined ?
                <InitResult/> : data.length === 0 ?
                    <NoSearchResult/> : data.map(s => <StoreSummaryCard key={s.shopId} storeSummary={s}/>)
            }
        </div>
    )
}

export default SearchResultList
