'use client'

import SearchColumn from "@/components/SearchColumn";
import MainMap from "@/components/MainMap";
import ShopDetailColumn from "@/components/ShopDetailColumn";
import {QueryClientProvider,} from "@tanstack/react-query";
import {queryClient} from "@/config/queryClient";
import MainNav from "@/components/MainNav";

function DefaultPage() {
    return (
        <QueryClientProvider client={queryClient}>
            <div className="absolute flex h-screen">
                <MainNav/>
                <div className="flex gap-3">
                    <SearchColumn/>
                    <ShopDetailColumn/>
                </div>
            </div>
            <MainMap/>
        </QueryClientProvider>
    );
}

export default DefaultPage
