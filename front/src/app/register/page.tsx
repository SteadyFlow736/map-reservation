'use client'

import Button from "@/components/Button";
import Link from "next/link";
import {SubmitHandler, useForm} from "react-hook-form";
import {useRouter} from "next/navigation";
import {MapPinIcon} from "@heroicons/react/16/solid";
import useSignUp from "@/hooks/useSignUp";
import {AxiosError} from "axios";
import {toast} from "react-toastify";

type Inputs = {
    email: string
    password: string
    confirmPassword: string
}

function RegisterPage() {
    const {register, handleSubmit, watch, formState: {errors}} = useForm<Inputs>({mode: 'onTouched'})
    const router = useRouter()
    const mutation = useSignUp()

    const requestRegistry: SubmitHandler<Inputs> = (data) => {
        mutation.mutate({email: data.email, password: data.password}, {
            onSuccess: () => {
                router.push("/login?result=success")
            },
            onError: (e) => {
                if (e instanceof AxiosError) {
                    const customErrorResponse: CustomErrorResponse<any> = e.response?.data
                    toast.error(customErrorResponse.message, {position: "bottom-center"})
                }
            }
        })
    }

    return (
        <div className="flex justify-center items-center h-screen">
            <div>
                {/* 로고 및 맵 화면 돌아가기 링크 */}
                <Link className="flex items-center justify-center pb-3" href="/">
                    <MapPinIcon className="h-6"/>
                    <p className="text-xl">map reservation</p>
                </Link>

                {/* 회원가입 창 */}
                <form className="border border-gray-200 w-96 rounded-2xl" onSubmit={handleSubmit(requestRegistry)}>
                    {/* 브랜드 */}
                    <p className="text-center p-5 text-xl">회원가입</p>

                    {/* email, password 입력 인풋*/}
                    <div className="grid grid-cols1 p-5">
                        <input
                            {...register('email', {
                                required: "이메일을 입력해 주세요.",
                                pattern: {
                                    value: /^[\w.-]+@[a-zA-Z\d.-]+\.[a-zA-Z]{2,}$/,
                                    message: "올바른 형식의 이메일을 입력해 주세요."
                                }
                            })}
                            className="border border-b-0 p-3"
                            placeholder="이메일"
                        />
                        {errors.email && <p className="text-red-600 mb-2">{errors.email.message}</p>}

                        <input
                            {...register('password', {
                                required: "비밀번호를 입력해 주세요.",
                                pattern: {
                                    value: /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,16}$/,
                                    message: "비밀번호: 8~16자의 영문 대/소문자, 숫자, 특수문자를 사용해 주세요."
                                }
                            })}
                            className="border border-b-0 p-3"
                            placeholder="비밀번호"
                            type="password"
                        />
                        {errors.password && <p className="text-red-600 mb-2">{errors.password.message}</p>}
                        <input
                            {...register('confirmPassword', {
                                required: '비밀번호와 동일하게 입력해 주세요.',
                                validate: {
                                    matching: value =>
                                        value === watch('password') || '비밀번호와 동일하게 입력해 주세요.',
                                },
                            })}
                            className="border p-3"
                            placeholder="비밀번호 확인"
                            type="password"
                        />
                        {errors.confirmPassword && <p className="text-red-600">{errors.confirmPassword.message}</p>}
                    </div>

                    {/* 회원가입 버튼 */}
                    <div className="p-5">
                        <Button onClick={undefined} label="회원가입"/>
                    </div>
                </form>

                {/* 로그인 페이지 링크 */}
                <div className="my-5 flex justify-center text-gray-400">
                    <Link href="/login">로그인</Link>
                </div>
            </div>
        </div>
    )
}

export default RegisterPage
