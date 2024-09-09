'use client'

import useAuth from "@/hooks/useAuth";
import {useRouter} from "next/navigation";
import {logout} from "@/api/auth";
import {useEffect} from "react";
import {MapIcon} from "@heroicons/react/24/outline";
import Link from "next/link";
import PageLoader from "@/components/Loaders/PageLoader";
import useInfiniteHairShopReservations from "@/hooks/useInfiniteHairShopReservations";
import React from "react";
import Time from "@/utils/Time";
import InfiniteScroll from "react-infinite-scroller";
import Loader from "@/components/Loaders/Loader";

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
        <div className="h-screen grid grid-rows-[auto_1fr]">
            <Bar user={user}/>
            <Body/>
        </div>
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
        <div className="sticky top-0 h-14 bg-gray-200 w-full flex justify-between p-3">
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
    return (
        <InfiniteReservations/>
    )
}

function InfiniteReservations() {
    const pageSize = 5;
    const {data, error, fetchNextPage, hasNextPage, isFetching, isLoading}
        = useInfiniteHairShopReservations(pageSize)

    if (isLoading) return <PageLoader/>

    return (
        <div className="p-3 h-full overflow-y-auto">
            <p className="text-2xl mb-3">예약 리스트</p>
            <InfiniteScroll hasMore={hasNextPage} loadMore={() => {
                if (!isFetching) {
                    fetchNextPage()
                }
            }}>
                <div className="grid gap-3">
                    {data?.pages.map((group, i) => (
                        <React.Fragment key={i}>
                            {group.content.map(r => <ReservationCard key={r.reservationId} reservation={r}/>)}
                        </React.Fragment>
                    ))}
                    <div className="mt-5">
                        {isFetching && <Loader/>}
                    </div>
                </div>
            </InfiniteScroll>
        </div>
    )
}

function ReservationCard({reservation}: { reservation: HairShopReservationDto }) {
    return (
        <div className="border rounded p-3">
            <p>{reservation.hairShopDto.shopName}</p>
            <p>{Time.formatDate(new Date(reservation.reservationTime))}</p>
        </div>
    );
}

export default UserPage
