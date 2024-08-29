import {MapIcon, UserCircleIcon} from "@heroicons/react/24/outline";
import Link from "next/link";

function Logo() {
    return (
        <MapIcon/>
    );
}

function Navs() {
    return null;
}

function Auth() {
    return (
        <Link href="/user">
            <UserCircleIcon/>
        </Link>
    );
}

function MainNav() {
    return (
        <div className="w-14 z-10 bg-white border border-r-gray-300">
            <div className="flex flex-col justify-between h-full pb-4">
                <Logo/>
                <Navs/>
                <Auth/>
            </div>
        </div>
    )
}

export default MainNav
