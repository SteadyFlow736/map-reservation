'use client'

import {useRouter} from "next/navigation";
import {useEffect} from "react";

/**
 * '/' 경로 페이지. 기본 페이지인 '/p'로 redirect 시킨다.
 */
export default function HomePage() {
    const router = useRouter()
    useEffect(() => {
        router.replace('p')
    }, [router]);

    return null
}
