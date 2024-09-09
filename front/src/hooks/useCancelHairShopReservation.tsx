import {useMutation} from "@tanstack/react-query";
import {MutationKeys} from "@/config/queryClient";
import {cancelHairShopReservation} from "@/api/reservation";

function useCancelHairShopReservation() {
    return useMutation({
        mutationKey: [MutationKeys.cancelHairShopReservation],
        mutationFn: (reservationId: number) => cancelHairShopReservation(reservationId)
    })
}

export default useCancelHairShopReservation
