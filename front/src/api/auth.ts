import {instance} from "@/config/axios";

/**
 * CSRF token 질의 API
 */
export async function fetchCsrfToken(): Promise<CsrfToken> {
    const {data} = await instance
        .get<CsrfTokenResponse>(`/api/csrf-token`)
    return data.csrfToken
}

/**
 * 회원가입 API
 *
 * @param email 이메일
 * @param password 비밀번호
 */
export async function signup(email: string, password: string) {
    const csrfToken = await fetchCsrfToken()
    await instance.post(`/api/customers`,
        {
            email,
            password
        },
        {
            headers: {
                [csrfToken.headerName]: csrfToken.token
            },
        }
    )
}

/**
 * 로그인 API
 *
 * @param email 이메일
 * @param password 비밀번호
 */
export async function login(email: string, password: string): Promise<CustomerInfo> {
    const csrfToken = await fetchCsrfToken()
    const form = new FormData
    form.append("username", email)
    form.append("password", password)
    const {data} = await instance.post('/api/login', form, {
        headers: {
            [csrfToken.headerName]: csrfToken.token
        }
    })
    return data
}

/**
 * 로그아웃 API
 */
export async function logout() {
    const csrfToken = await fetchCsrfToken()
    return await instance.post('/api/logout', null, {
        headers: {
            [csrfToken.headerName]: csrfToken.token
        }
    })
}

/**
 * 세션 정보 질의 API
 */
export async function fetchSession(): Promise<CustomerInfo> {
    const {data} = await instance.get('/api/customers/me')
    return data
}
