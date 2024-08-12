import {useState} from "react";

function Search() {
    const [searchTerm, setSearchTerm] = useState("");

    return (
        <input
            className="m-4 p-3"
            type="text"
            value={searchTerm}
            onChange={e => setSearchTerm(e.target.value)}
        />
    )
}

export default Search
