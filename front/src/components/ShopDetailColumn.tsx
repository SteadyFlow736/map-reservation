import {useAtom, useSetAtom} from "jotai";
import {selectedHairShopIdAtom} from "@/atoms";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faXmark} from "@fortawesome/free-solid-svg-icons";

function ShopDetailColumn() {
    const [selectedHairShopId] = useAtom(selectedHairShopIdAtom)

    const showOrHidden = selectedHairShopId ? "transform-x-0 opacity-100" : "-translate-x-full opacity-0"

    return (
        <div className={`
            max-h-screen w-96 z-10 my-3 rounded-2xl shadow-2xl
            bg-white
            ${showOrHidden}
            transition
            `}>
            <ShopHead/>
        </div>
    )
}

function ShopHead() {
    const setSelectedHairShopId = useSetAtom(selectedHairShopIdAtom)

    const close = () => {
        setSelectedHairShopId(undefined)
    }

    return (
        <div>
            <FontAwesomeIcon icon={faXmark} onClick={close} className="absolute right-3 text-2xl mt-5"/>
        </div>
    )
}

export default ShopDetailColumn
