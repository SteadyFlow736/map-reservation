import {useMutation} from "@tanstack/react-query";
import {createHairShopReservation, CreateReservationParams} from "@/api";

function useCreateHairShopReservation() {
    return useMutation({
        mutationFn: (params: CreateReservationParams) => createHairShopReservation(params)
    })
}

export default useCreateHairShopReservation
