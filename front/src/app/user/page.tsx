'use client'

import useAuth from "@/hooks/useAuth";
import {useRouter} from "next/navigation";

function UserPage() {
    const router = useRouter()
    const {status, user} = useAuth()
    if (status === 'loading') return <div>Loading</div>
    if (status === 'unauthenticated') {
        router.push('/login')
        return
    }

    return (
        <>
            <div>{user.username}</div>
        </>
    )
}

export default UserPage
