import {useAtom} from "jotai";
import {selectedHairShopIdAtom} from "@/atoms";
import ShopDetailWrapperPage from "@/components/ShopDetailPage/ShopDetailWrapperPage";

function ShopDetailColumn() {
    const [selectedHairShopId] = useAtom(selectedHairShopIdAtom)
    const showOrHidden = selectedHairShopId ? "transform-x-0 opacity-100" : "-translate-x-full opacity-0"

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
            {selectedHairShopId && <ShopDetailWrapperPage key={selectedHairShopId.shopId}/>}
        </div>
    )
}

export default ShopDetailColumn
