import {useSetAtom} from "jotai";
import {selectedHairShopAtom} from "@/atoms";

function StoreSummaryCard({storeSummary}: { storeSummary: HairShopDto }) {
    const setSelectedHairShop = useSetAtom(selectedHairShopAtom)

    const selectShop = () => {
        setSelectedHairShop({
            shopId: storeSummary.shopId,
            needPan: true
        })
    }

    return (
        <div
            className="p-4 hover:cursor-pointer hover:bg-gray-200"
            onClick={selectShop}
        >
            <p>{storeSummary.shopName}</p>
        </div>
    )
}

export default StoreSummaryCard
