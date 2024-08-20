import ShopSubPageNav from "@/components/ShopSubPage/ShopSubPageNav";
import {useState} from "react";
import {SubPage} from "@/properties/SubPage";
import ShopSubPageHome from "@/components/ShopSubPage/ShopSubPageHome";
import ShopSubPageReview from "@/components/ShopSubPage/ShopSubPageReview";
import ShopSubPageReservation from "@/components/ShopSubPage/ShopSubPageReservation";
import ShopSubPageNews from "@/components/ShopSubPage/ShopSubPageNews";

function ShopSubPage() {
    const [subPage, setSubPage] = useState<SubPage>('홈')

    let pageToRender
    switch (subPage) {
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
            <ShopSubPageNav subPage={subPage} setSubPage={setSubPage}/>
            {pageToRender}
        </div>

    )
}

export default ShopSubPage
