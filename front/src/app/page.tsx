'use client'

import SearchColumn from "@/components/SearchColumn";
import MainMap from "@/components/MainMap";
import ShopDetailColumn from "@/components/ShopDetailColumn";

function Home() {
    return (
        <div>
            <div className="absolute z-20 flex">
                <SearchColumn/>
                <ShopDetailColumn/>
            </div>
            <MainMap/>
        </div>
    );
}

export default Home
