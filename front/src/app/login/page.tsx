'use client'

import React, {ChangeEvent, Suspense, useState} from "react";
import Link from "next/link";
import {useRouter, useSearchParams} from "next/navigation";
import {MapPinIcon} from "@heroicons/react/16/solid";
import useLogin from "@/hooks/useLogin";
import {AxiosError} from "axios";
import {toast} from "react-toastify";

function LoginPage() {
    const [email, setEmail] = useState<string>('')
    const [password, setPassword] = useState<string>('')
    const router = useRouter()
    const login = useLogin()

    const handleEmailChange = (event: ChangeEvent<HTMLInputElement>) => {
        setEmail(event.target.value)
    }

    const handlePasswordChange = (event: ChangeEvent<HTMLInputElement>) => {
        setPassword(event.target.value)
    }

    const tryLogin = (email: string, password: string) => {
        login.mutate({email, password}, {
            onSuccess: () => {
                toast.success("로그인 성공", {position: "bottom-center", autoClose: 1000})
                router.push("/")
            },
            onError: (e) => {
                if (e instanceof AxiosError) {
                    const error: CustomErrorResponse<any> = e.response?.data
                    toast.error(error.message, {position: "bottom-center"})
                }
            }
        })
    }

    const handleOnKeyDown = (event: React.KeyboardEvent<HTMLInputElement>) => {
        if (event.key !== 'Enter') return
        tryLogin(email, password)
    }

    return (
        <div className="flex justify-center items-center h-screen">
            <div>
                <Suspense>
                    <SignupResult/>
                </Suspense>

                {/* 로고 및 맵 화면 돌아가기 링크 */}
                <Link className="flex items-center justify-center pb-3" href="/">
                    <MapPinIcon className="h-6"/>
                    <p className="text-xl">map reservation</p>
                </Link>

                {/* 로그인 창 */}
                <div className="border border-gray-200 w-96 rounded-2xl">
                    {/* 브랜드 */}
                    <p className="text-center p-5 text-xl">로그인</p>

                    {/* email, password 입력 인풋*/}
                    <div className="grid grid-cols1 p-5">
                        <input
                            className="border border-b-0 p-3"
                            type="text" value={email} onChange={handleEmailChange}
                            placeholder="이메일"
                        />
                        <input
                            className="border p-3"
                            type="password" value={password} onChange={handlePasswordChange}
                            placeholder="비밀번호"
                            onKeyDown={handleOnKeyDown}
                        />
                    </div>

                    <div className="p-5">
                        {/* 로그인 버튼 */}
                        <button className="btn w-full" onClick={() => tryLogin(email, password)}>로그인</button>
                        {/* 데모 로그인 버튼 */}
                        <button className="btn w-full mt-2" onClick={() => tryLogin("abc@gmail.com", "Password1!")}>데모
                            계정으로 로그인
                        </button>
                    </div>
                </div>

                {/* 회원 가입 링크 */}
                <div className="my-5 flex justify-center text-gray-400">
                    <Link href="/register">회원 가입</Link>
                </div>


            </div>
        </div>
    )
}

/**
 * 회원가입 결과 표시 컴포넌트.
 *
 * 이 컴포넌트를 굳이 분리한 이유가 있다. 분리하지 않고 'build' 하면 오류가 발생한다.
 * 오류의 자세한 내용은 다음의 링크 참조: https://nextjs.org/docs/messages/missing-suspense-with-csr-bailout
 */
function SignupResult() {
    const successParam = useSearchParams().get('result')
    return (
        successParam === 'success' ?
            <div className="text-center">회원가입에 성공했습니다🥂. 로그인 해 주세요.</div>
            :
            null
    )
}


export default LoginPage
