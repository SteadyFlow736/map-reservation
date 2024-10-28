import {useMutation} from "@tanstack/react-query";
import {signup} from "@/api/auth";

function useSignUp() {
    return useMutation({
        mutationFn: (param: { email: string; password: string }) => signup(param.email, param.password)
    })
}

export default useSignUp
