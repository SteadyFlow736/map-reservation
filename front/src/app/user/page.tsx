'use client'

import {useRouter} from "next/navigation";
import {MapIcon} from "@heroicons/react/24/outline";
import Link from "next/link";
import PageLoader from "@/components/Loaders/PageLoader";
import useInfiniteHairShopReservations from "@/hooks/useInfiniteHairShopReservations";
import React from "react";
import Time from "@/utils/Time";
import InfiniteScroll from "react-infinite-scroller";
import Loader from "@/components/Loaders/Loader";
import useLogout from "@/hooks/useLogout";
import {AxiosError} from "axios";
import {toast} from "react-toastify";
import useAuthGuard from "@/hooks/useAuthGuard";

function UserPage() {
    const {status, user} = useAuthGuard()

    if (status === 'loading') return <PageLoader/>

    return (
        <div className="h-screen grid grid-rows-[auto_1fr]">
            <Bar user={user}/>
            <Body/>
        </div>
    )
}

function Bar({user}: { user: CustomerInfo }) {
    const router = useRouter()
    const logout = useLogout()

    const handleLogoutClick = async () => {
        logout.mutate(undefined, {
            // TODO: logout 이후 query cache invalidate 하기
            onSuccess: () => router.push('/'),
            onError: (e) => {
                if (e instanceof AxiosError) {
                    const customErrorResponse: CustomErrorResponse<any> = e.response?.data
                    toast.error(customErrorResponse.message, {position: "bottom-center"})
                }
            }
        })
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
            <p>{reservation.hairShopResponse.shopName}</p>
            <p>{Time.formatDate(new Date(reservation.reservationTime))}</p>
            {
                reservation.status === 'CANCELLED' ? <p>취소됨</p> : null
            }
        </div>
    );
}

export default UserPage
