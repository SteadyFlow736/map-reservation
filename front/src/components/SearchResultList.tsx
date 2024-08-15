import StoreSummaryCard from "@/components/StoreSummaryCard";
import {hairShopSearchResultAtom} from "@/atoms";
import {useAtomValue} from "jotai";

function SearchResultList() {
    const searchResult = useAtomValue(hairShopSearchResultAtom)
    const data = searchResult?.content

    return (
        <div className="overflow-y-auto divide-y">
            {data?.map(s => <StoreSummaryCard key={s.shopName} storeSummary={s}/>)}
        </div>
    )
}

export default SearchResultList
