'use client'

import SearchColumn from "@/components/SearchColumn/SearchColumn";
import MainMap from "@/components/MainMap";
import ShopDetailColumn from "@/components/ShopDetailColumn";
import MainNav from "@/components/MainNav";

function DefaultPage() {
    return (
        <>
            <div className="absolute flex h-screen">
                <MainNav/>
                <div className="flex gap-3">
                    <SearchColumn/>
                    <ShopDetailColumn/>
                </div>
            </div>
            <MainMap/>
        </>
    );
}

export default DefaultPage
