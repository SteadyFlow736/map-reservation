import {useMutation} from "@tanstack/react-query";
import {login} from "@/api/auth";

function useLogin() {
    return useMutation({
        mutationFn: (param: { email: string, password: string }) => login(param.email, param.password),
    })
}

export default useLogin
