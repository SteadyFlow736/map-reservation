'use client'

import SearchColumn from "@/components/SearchColumn/SearchColumn";
import MainMap from "@/components/MainMap";
import ShopDetailColumn from "@/components/ShopDetailColumn";
import MainNav from "@/components/MainNav";
import {useSetAtom} from "jotai";
import {emptySearchOnAtom} from "@/atoms";
import {useEffect} from "react";

function DefaultPage() {
    const setEmptySearchOn = useSetAtom(emptySearchOnAtom)

    useEffect(() => {
        setEmptySearchOn(true)
    }, [setEmptySearchOn]);

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
