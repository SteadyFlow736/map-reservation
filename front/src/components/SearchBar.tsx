import {useState} from "react";

function SearchBar() {
    const [searchTerm, setSearchTerm] = useState("");

    return (
        <input
            className="m-4 p-3 border-2 border-lime-400 rounded"
            type="text"
            value={searchTerm}
            onChange={e => setSearchTerm(e.target.value)}
        />
    )
}

export default SearchBar
