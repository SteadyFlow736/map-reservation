import useAuth, {AuthState} from "@/hooks/useAuth";
import {useEffect} from "react";
import {useRouter} from "next/navigation";

type AuthGuardState = Exclude<AuthState, {
    status: 'unauthenticated',
    user: null
}>

/**
 * 인증이 필요한 페이지 보호 hook
 *
 * 인증 안 됐다면 로그인 페이지로 이동
 * 인증 됐다면 사용자 정보 제공
 */
function useAuthGuard(): AuthGuardState {
    const authState = useAuth()
    const router = useRouter()

    useEffect(() => {
        if (authState.status === 'unauthenticated') {
            // 페이지 히스토리 최상단을 login으로 변경하고 login으로 이동시키기 위해 replace 사용.
            // 브라우저의 뒤로 가기 눌렀을 때 다시 이 페이지로 돌아오게 하지 않기 위해서.
            router.replace('/login')
            return
        }
    }, [router, authState]);

    return authState.status === 'authenticated' ?
        authState
        :
        {
            status: 'loading',
            user: null
        }
}

export default useAuthGuard
