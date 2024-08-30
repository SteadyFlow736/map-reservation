import {useContext} from "react";
import {ShopSubPageContext, SubPage} from "@/components/ShopDetailPage/ShopDetailWrapperPage";

const subPages: SubPage[] = ['홈', '소식', '예약', '리뷰'];

function ShopSubPageNav() {
    return (
        <div className="flex flex-row gap-3 px-3 bg-white border-b border-b-gray-200">
            {subPages.map((page, index) =>
                <NavButton subPage={page} key={index}/>)}
        </div>
    )
}

function NavButton({subPage}: { subPage: SubPage }) {
    const {shopSubPage, setShopSubPage} = useContext(ShopSubPageContext)
    const style = shopSubPage === subPage ? 'text-black border-b-2 border-b-black' : 'text-gray-400'

    const changeSubPage = () => {
        setShopSubPage(subPage)
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
