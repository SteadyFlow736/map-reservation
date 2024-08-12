import Search from "@/components/Search";
import SearchResultList from "@/components/SearchResultList";

function SearchColumn() {
    return (
        <div className="absolute z-20
        h-screen w-96
        grid grid-cols-1 grid-rows-[auto_1fr]
        bg-green-300"
        >
            <Search/>
            <SearchResultList/>
        </div>
    )
}

export default SearchColumn
