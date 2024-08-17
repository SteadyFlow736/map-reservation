'use client'

import SearchColumn from "@/components/SearchColumn";
import MainMap from "@/components/MainMap";
import ShopDetailColumn from "@/components/ShopDetailColumn";
import {QueryClientProvider,} from "@tanstack/react-query";
import {queryClient} from "@/config/queryClient";

function Home() {
    return (
        <QueryClientProvider client={queryClient}>
            <div className="absolute flex gap-3">
                <SearchColumn/>
                <ShopDetailColumn/>
            </div>
            <MainMap/>
        </QueryClientProvider>
    );
}

export default Home
