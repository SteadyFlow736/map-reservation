import {useAtom, useSetAtom} from "jotai";
import {selectedHairShopIdAtom} from "@/atoms";
import {useEffect, useState} from "react";
import {fetchShopDetail} from "@/api";
import {XMarkIcon} from "@heroicons/react/16/solid";

function ShopDetailColumn() {
    const [selectedHairShopId] = useAtom(selectedHairShopIdAtom)
    const [shopDetail, setShopDetail] = useState<HairShopDetail>()

    const showOrHidden = selectedHairShopId ? "transform-x-0 opacity-100" : "-translate-x-full opacity-0"

    useEffect(() => {
        if (!selectedHairShopId) return
        fetchShopDetail(selectedHairShopId).then(response => setShopDetail(response))
    }, [selectedHairShopId])

    if (!shopDetail) return <div>Loading</div>

    return (
        <div className={`
            max-h-screen w-96 z-10 my-3 rounded-2xl shadow-2xl
            bg-white
            ${showOrHidden}
            transition
            `}>
            <ShopHead shopDetail={shopDetail}/>
        </div>
    )
}

function ShopHead({shopDetail}: { shopDetail: HairShopDetail }) {
    const setSelectedHairShopId = useSetAtom(selectedHairShopIdAtom)

    const close = () => {
        setSelectedHairShopId(undefined)
    }

    return (
        <div>
            <XMarkIcon onClick={close} className="absolute right-3 mt-5 size-6 hover:cursor-pointer"/>
            <p>{shopDetail.shopName}</p>
        </div>
    )
}

export default ShopDetailColumn
