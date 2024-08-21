import ShopSubPageNav from "@/components/ShopSubPage/ShopSubPageNav";
import {useContext} from "react";
import ShopSubPageHome from "@/components/ShopSubPage/ShopSubPageHome";
import ShopSubPageReview from "@/components/ShopSubPage/ShopSubPageReview";
import ShopSubPageReservation from "@/components/ShopSubPage/ShopSubPageReservation";
import ShopSubPageNews from "@/components/ShopSubPage/ShopSubPageNews";
import {ShopSubPageContext} from "@/contexts";

function ShopSubPage() {
    const {shopSubPage} = useContext(ShopSubPageContext)

    let pageToRender
    switch (shopSubPage) {
        case "홈":
            pageToRender = <ShopSubPageHome/>
            break
        case "소식":
            pageToRender = <ShopSubPageNews/>
            break
        case "예약":
            pageToRender = <ShopSubPageReservation/>
            break
        case "리뷰":
            pageToRender = <ShopSubPageReview/>
            break
        default:
            pageToRender = <ShopSubPageHome/>
    }

    return (
        <div>
            <ShopSubPageNav/>
            {pageToRender}
        </div>
    )
}

export default ShopSubPage
