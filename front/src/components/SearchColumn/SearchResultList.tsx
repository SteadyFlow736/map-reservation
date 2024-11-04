import StoreSummaryCard from "@/components/StoreSummaryCard";
import {hairShopSearchResponseAtom} from "@/atoms";
import {useAtomValue} from "jotai";

function SearchResultList() {
    const hairShopSearchResponse = useAtomValue(hairShopSearchResponseAtom)
    const data = hairShopSearchResponse?.page.content

    return (
        <div className="overflow-y-auto divide-y">
            {data?.map(s => <StoreSummaryCard key={s.shopId} storeSummary={s}/>)}
        </div>
    )
}

export default SearchResultList
