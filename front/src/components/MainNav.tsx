import {MapIcon, UserCircleIcon, UserIcon} from "@heroicons/react/24/outline";
import Link from "next/link";
import useAuth from "@/hooks/useAuth";

function Logo() {
    return (
        <Link href='/'>
            <MapIcon/>
        </Link>
    );
}

function Navs() {
    return null;
}

function Auth() {
    const {status} = useAuth()

    return (
        <Link href="/user">
            <UserCircleIcon className={`${status === 'authenticated' ? 'bg-green-100' : ''}`}/>
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
