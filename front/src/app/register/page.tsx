'use client'

import Button from "@/components/Button";
import Link from "next/link";
import {SubmitHandler, useForm} from "react-hook-form";
import {signup} from "@/api";
import {useRouter} from "next/navigation";

type Inputs = {
    email: string
    password: string
    confirmPassword: string
}

function RegisterPage() {
    const {register, handleSubmit, watch, formState: {errors}} = useForm<Inputs>()
    console.log(watch('password'))
    const router = useRouter()

    const requestRegistry: SubmitHandler<Inputs> = async (data) => {
        try {
            await signup(data.email, data.password)
            router.push("/login?success=true")
        } catch (e) {
            console.log("가입이 실패했습니다.")
            console.log(e)
        }
    }

    return (
        <div className="flex justify-center items-center h-screen">
            <div>
                {/* 로그인 창 */}
                <form className="border border-gray-200 w-96 rounded-2xl" onSubmit={handleSubmit(requestRegistry)}>
                    {/* 브랜드 */}
                    <p className="text-center p-5 text-xl">map-reservation 회원가입</p>

                    {/* id, password 입력 인풋*/}
                    <div className="grid grid-cols1 p-5">
                        <input
                            {...register('email', {required: true})}
                            className="border border-b-0 p-3"
                            placeholder="이메일"
                        />
                        <input
                            {...register('password', {required: true})}
                            className="border border-b-0 p-3"
                            placeholder="비밀번호"
                        />
                        <input
                            {...register('confirmPassword', {required: true})}
                            className="border p-3"
                            placeholder="비밀번호 확인"
                        />
                    </div>

                    {/* 로그인 버튼 */}
                    <div className="p-5">
                        <Button onClick={undefined} label="회원가입"/>
                    </div>
                </form>

                {/* 회원 가입 */}
                <div className="my-5 flex justify-center text-gray-400">
                    <Link href="/login">로그인</Link>
                </div>
            </div>
        </div>
    )
}

export default RegisterPage
