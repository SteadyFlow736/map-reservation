import {useAtom, useSetAtom} from "jotai";
import {selectedHairShopIdAtom} from "@/atoms";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faXmark} from "@fortawesome/free-solid-svg-icons";
import {useEffect, useState} from "react";
import {fetchShopDetail} from "@/api";

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
            <FontAwesomeIcon icon={faXmark} onClick={close} className="absolute right-3 text-2xl mt-5"/>
            <p>{shopDetail.shopName}</p>
        </div>
    )
}

export default ShopDetailColumn
