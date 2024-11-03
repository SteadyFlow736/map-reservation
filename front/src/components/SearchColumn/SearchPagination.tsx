import ReactPaginate from "react-paginate";
import './SearchPagination.css'

function SearchPagination() {

    return (
        <div className="flex justify-center items-center h-10 border-t-2 text-sm">
            <ReactPaginate
                breakLabel="..."
                nextLabel=">"
                previousLabel="<"
                pageRangeDisplayed={5}
                pageCount={20}
                containerClassName={'pagination'}
                pageClassName={'page-item'}
            />
        </div>
    )
}

export default SearchPagination
