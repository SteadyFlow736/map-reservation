import {SubPage} from "@/properties/SubPage";
import {Dispatch, SetStateAction} from "react";

const subPages: SubPage[] = ['홈', '소식', '예약', '리뷰'];

function ShopSubPageNav(
    {subPage, setSubPage}: { subPage: SubPage, setSubPage: Dispatch<SetStateAction<SubPage>> }) {

    return (
        <div className="flex flex-row gap-3 px-3 bg-white border-b border-b-gray-200">
            {subPages.map((page, index) =>
                <NavButton key={index} subPage={page} setSubPage={setSubPage} on={subPage == page}/>)}
        </div>
    )
}

function NavButton({subPage, setSubPage, on}: {
    subPage: SubPage,
    setSubPage: Dispatch<SetStateAction<SubPage>>,
    on: boolean
}) {
    const style = on ? 'text-black border-b-2 border-b-black' : 'text-gray-400'
    const changeSubPage = () => {
        setSubPage(subPage)
    }

    return (
        <div
            className={`text-lg hover:cursor-pointer py-3 ${style}`}
            onClick={changeSubPage}
        >
            {subPage}
        </div>
    )
}


export default ShopSubPageNav
