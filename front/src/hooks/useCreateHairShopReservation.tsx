import {useMutation} from "@tanstack/react-query";
import {createHairShopReservation, CreateReservationParams} from "@/api/reservation";

function useCreateHairShopReservation() {
    return useMutation({
        mutationKey: ['CreateHairShopReservation'],
        mutationFn: (params: CreateReservationParams) => createHairShopReservation(params),
        onError: error => {
        },
        onSuccess: data => {
        }

    })
}

export default useCreateHairShopReservation
