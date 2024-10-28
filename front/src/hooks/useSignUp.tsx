import {useMutation} from "@tanstack/react-query";
import {signup} from "@/api/auth";

function useSignUp() {
    return useMutation({
        mutationFn: (arg: { email: string; password: string }) => signup(arg.email, arg.password),
        onError: error => {
        },
        onSuccess: data => {
        }
    })
}

export default useSignUp
