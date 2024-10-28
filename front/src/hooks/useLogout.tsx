import {useMutation} from "@tanstack/react-query";
import {logout} from "@/api/auth";

function useLogout() {
    return useMutation({
        mutationFn: () => logout()
    })
}

export default useLogout
