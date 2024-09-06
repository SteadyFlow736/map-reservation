'use client'

import useAuth from "@/hooks/useAuth";
import {useRouter} from "next/navigation";
import {logout} from "@/api";
import {useEffect} from "react";
import {MapIcon} from "@heroicons/react/24/outline";
import Link from "next/link";
import PageLoader from "@/components/Loaders/PageLoader";
import useInfiniteHairShopReservations from "@/hooks/useInfiniteHairShopReservations";
import React from "react";

function UserPage() {
    const router = useRouter()
    const {status, user} = useAuth()

    useEffect(() => {
        if (status === 'unauthenticated') {
            // 페이지 히스토리 최상단을 user에서 login으로 변경하고 login으로 이동.
            // login 페이지에서 로그인 포기하고 뒤로 가기 누르면 user 페이지가 아니라 전 화면(대표적으로 메인 화면)으로 돌아가기 위해서임
            router.replace('/login')
            return
        }
    }, [router, status]);

    if (status === 'loading' || status === 'unauthenticated') return <PageLoader/>

    return (
        <>
            <Bar user={user}/>
            <Body/>
        </>
    )
}

function Bar({user}: { user: CustomerInfo }) {
    const router = useRouter()

    const handleLogoutClick = async () => {
        try {
            // TODO: logout 이후 query cache invalidate 하기
            await logout()
            router.push('/')
        } catch (e) {
            console.log(e)
            console.log('error')
        }
    }

    return (
        <div className="absolute top-0 h-14 bg-gray-200 w-full flex justify-between p-3">
            <Link href='/'>
                <MapIcon className="h-8 w-8"/>
            </Link>
            <p className="flex justify-center items-center">{user.username}님 환영합니다.</p>
            <div
                className="flex justify-center items-center bg-white rounded p-3 hover:cursor-pointer"
                onClick={handleLogoutClick}
            >
                로그 아웃
            </div>
        </div>
    )
}

function Body() {
    const pageSize = 10;
    const {data, error, fetchNextPage, hasNextPage, isFetching, status}
        = useInfiniteHairShopReservations(pageSize)

    return (
        <div className="mt-14">
            {data?.pages.map((group, i) => (
                <React.Fragment key={i}>
                    {group.content.map(r => (
                        <div key={r.reservationId}>
                            <p>{r.hairShopDto.shopName}</p>
                            <p>{r.reservationTime}</p>
                        </div>
                    ))}
                </React.Fragment>
            ))}
        </div>
    )
}

export default UserPage
