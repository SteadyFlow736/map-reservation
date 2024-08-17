'use client'

import SearchColumn from "@/components/SearchColumn";
import MainMap from "@/components/MainMap";
import ShopDetailColumn from "@/components/ShopDetailColumn";

function Home() {
    return (
        <div>
            <div className="absolute flex gap-3">
                <SearchColumn/>
                <ShopDetailColumn/>
            </div>
            <MainMap/>
        </div>
    );
}

export default Home
