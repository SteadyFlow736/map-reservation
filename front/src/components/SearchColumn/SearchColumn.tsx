import SearchBar from "@/components/SearchColumn/SearchBar";
import SearchResultList from "@/components/SearchColumn/SearchResultList";
import SearchPagination from "@/components/SearchColumn/SearchPagination";

function SearchColumn() {
    return (
        <div className="
        w-96 z-20
        grid grid-cols-1 grid-rows-[auto_1fr_auto]
        bg-white"
        >
            <SearchBar/>
            <SearchResultList/>
            <SearchPagination/>
        </div>
    )
}

export default SearchColumn
