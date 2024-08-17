import SearchBar from "@/components/SearchBar";
import SearchResultList from "@/components/SearchResultList";

function SearchColumn() {
    return (
        <div className="
        h-screen w-96
        grid grid-cols-1 grid-rows-[auto_1fr]
        bg-white"
        >
            <SearchBar/>
            <SearchResultList/>
        </div>
    )
}

export default SearchColumn
