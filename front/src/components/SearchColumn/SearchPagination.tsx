import ReactPaginate from "react-paginate";
import './SearchPagination.css'
import {useAtom} from "jotai/index";
import {hairShopSearchResponseAtom} from "@/atoms";
import {fetchSearchResult} from "@/api/hairShop";
import Pageable from "@/dto/page/Pageable";

function SearchPagination() {
    const [hairShopSearchResponse, setHairShopSearchResponse] = useAtom(hairShopSearchResponseAtom)

    const pageChangeHandler = async (selectedItem: { selected: number }) => {
        if (!hairShopSearchResponse) return

        const pageable: Pageable = {
            page: selectedItem.selected,
            size: hairShopSearchResponse.page.size,
            sort: [] //TODO
        }

        const searchCondition: HairShopSearchCondition = {
            searchTerm: hairShopSearchResponse.searchCondition.searchTerm,
            minLongitude: hairShopSearchResponse.searchCondition.minLongitude?.toString(),
            maxLongitude: hairShopSearchResponse.searchCondition.maxLongitude?.toString(),
            minLatitude: hairShopSearchResponse.searchCondition.minLatitude?.toString(),
            maxLatitude: hairShopSearchResponse.searchCondition.maxLatitude?.toString()
        }

        const data = await fetchSearchResult(searchCondition, pageable)
        setHairShopSearchResponse(data)
    }

    if (!hairShopSearchResponse) {
        return null
    }

    return (
        <div className="flex justify-center items-center h-12 border-t-2 text-sm">
            <ReactPaginate
                breakLabel="..."
                nextLabel=">"
                previousLabel="<"
                pageRangeDisplayed={5}
                pageCount={hairShopSearchResponse.page.totalPages}
                onPageChange={pageChangeHandler}
                containerClassName={'pagination'}
                pageClassName={'page-item'}
                activeClassName={'active'}
            />
        </div>
    )
}

export default SearchPagination
