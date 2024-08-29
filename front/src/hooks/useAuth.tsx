import {useEffect, useState} from "react";
import {fetchSession} from "@/api";

type AuthState = {
    status: 'loading'
    user: null
} | {
    status: 'authenticated',
    user: CustomerInfo
} | {
    status: 'unauthenticated',
    user: null
}

/**
 * 인증 상태 관리 custom hook
 */
function useAuth() {
    const [authState, setAuthState] = useState<AuthState>({
        status: 'loading',
        user: null
    })

    useEffect(() => {
        fetchSession().then(response => {
            setAuthState({status: "authenticated", user: response})
        }).catch(_ => {
            setAuthState({status: 'unauthenticated', user: null})
        })
    }, [])

    return authState
}

export default useAuth
