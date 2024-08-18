import {useSetAtom} from "jotai";
import {selectedHairShopIdAtom} from "@/atoms";

function StoreSummaryCard({storeSummary}: { storeSummary: HairShopDto }) {
    const setSelectedHairShopId = useSetAtom(selectedHairShopIdAtom)

    const selectShop = () => {
        setSelectedHairShopId({
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
