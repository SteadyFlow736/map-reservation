import Link from "next/link";
import {Menu, menus} from "@/properties/menu";
import {useSearchParams} from "next/navigation";

function ShopNav() {
    const searchParams = useSearchParams()
    const path = searchParams.get("path")

    return (
        <div className="flex flex-row gap-3 p-3 bg-white">
            {menus.map((m, index) => <NavButton key={index} menu={m} on={m.path === path}/>)}
        </div>
    )
}

function NavButton({menu, on}: { menu: Menu, on: boolean }) {
    const style = on ? 'text-black' : 'text-gray-400'

    return (
        <Link
            className={`
            hover:cursor-pointer ${style}
            `}
            href={`?path=${menu.path}`}
        >
            {menu.title}
        </Link>
    )
}


export default ShopNav
