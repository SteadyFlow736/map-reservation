'use client'

import {ChangeEvent, useState} from "react";
import Button from "@/components/Button";
import Link from "next/link";
import {useRouter, useSearchParams} from "next/navigation";
import {login} from "@/api";

function LoginPage() {
    const [email, setEmail] = useState<string>('')
    const [password, setPassword] = useState<string>('')
    const successParam = useSearchParams().get('result')
    const router = useRouter()

    const handleEmailChange = (event: ChangeEvent<HTMLInputElement>) => {
        setEmail(event.target.value)
    }

    const handlePasswordChange = (event: ChangeEvent<HTMLInputElement>) => {
        setPassword(event.target.value)
    }

    const tryLogin = async () => {
        try {
            const result = await login(email, password)
            console.log("성공", result)
            router.push("/")
        } catch (e) {
            console.log("실패", e)
        }
    }

    return (
        <div className="flex justify-center items-center h-screen">
            <div>
                {
                    successParam === 'success' ?
                        <div className="text-center">회원가입에 성공했습니다🥂. 로그인 해 주세요.</div>
                        :
                        null
                }
                {/* 로그인 창 */}
                <div className="border border-gray-200 w-96 rounded-2xl">
                    {/* 브랜드 */}
                    <p className="text-center p-5 text-xl">map-reservation 로그인</p>

                    {/* email, password 입력 인풋*/}
                    <div className="grid grid-cols1 p-5">
                        <input
                            className="border border-b-0 p-3"
                            type="text" value={email} onChange={handleEmailChange}
                            placeholder="이메일"
                        />
                        <input
                            className="border p-3"
                            type="text" value={password} onChange={handlePasswordChange}
                            placeholder="비밀번호"
                        />
                    </div>

                    {/* 로그인 버튼 */}
                    <div className="p-5">
                        <Button onClick={tryLogin} label="로그인"/>
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

export default LoginPage
