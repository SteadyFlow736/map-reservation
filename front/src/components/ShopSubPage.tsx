import ShopSubPageNav from "@/components/ShopSubPageNav";
import {useState} from "react";
import {SubPage} from "@/properties/SubPage";

function ShopSubPage() {
    const [subPage, setSubPage] = useState<SubPage>('홈')

    return (
        <ShopSubPageNav subPage={subPage} setSubPage={setSubPage}/>
    )
}

export default ShopSubPage
