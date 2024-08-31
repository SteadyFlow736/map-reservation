import {useAtom} from "jotai";
import {selectedHairShopAtom} from "@/atoms";
import ShopDetailWrapperPage from "@/components/ShopDetailPage/ShopDetailWrapperPage";

function ShopDetailColumn() {
    const [selectedHairShop] = useAtom(selectedHairShopAtom)
    const showOrHidden = selectedHairShop ? "transform-x-0 opacity-100" : "-translate-x-full opacity-0"

    return (
        <div className={`
            w-96 z-10 my-3 rounded-2xl shadow-2xl
            bg-gray-100
            ${showOrHidden}
            transition
            overflow-hidden
            overflow-y-auto
            `}
        >
            {selectedHairShop && <ShopDetailWrapperPage key={selectedHairShop.shopId}/>}
        </div>
    )
}

export default ShopDetailColumn
