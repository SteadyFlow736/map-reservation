'use client'

import {ChangeEvent, useState} from "react";
import Button from "@/components/Button";
import Link from "next/link";

function LoginPage() {
    const [id, setId] = useState<string>()
    const [password, setPassword] = useState<string>()

    const handleIdChange = (event: ChangeEvent<HTMLInputElement>) => {
        setId(event.target.value)
    }

    const handlePasswordChange = (event: ChangeEvent<HTMLInputElement>) => {
        setPassword(event.target.value)
    }

    return (
        <div className="flex justify-center items-center h-screen">
            <div>
                {/* 로그인 창 */}
                <div className="border border-gray-200 w-96 rounded-2xl">
                    {/* 브랜드 */}
                    <p className="text-center p-5 text-xl">map-reservation 로그인</p>

                    {/* id, password 입력 인풋*/}
                    <div className="grid grid-cols1 p-5">
                        <input
                            className="border border-b-0 p-3"
                            type="text" value={id} onChange={handleIdChange}
                            placeholder="아이디"
                        />
                        <input
                            className="border p-3"
                            type="text" value={password} onChange={handlePasswordChange}
                            placeholder="비밀번호"
                        />
                    </div>

                    {/* 로그인 버튼 */}
                    <div className="p-5">
                        <Button onClick={undefined} label="로그인"/>
                    </div>
                </div>

                {/* 회원 가입 */}
                <div className="my-5 flex justify-center text-gray-400">
                    <Link href="/register">회원 가입</Link>
                </div>
            </div>
        </div>
    )
}

export default LoginPage
