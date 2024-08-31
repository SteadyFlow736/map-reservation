import {useMutation} from "@tanstack/react-query";
import {createHairShopReservation, CreateReservationParams} from "@/api";

function useCreateHairShopReservation() {
    return useMutation({
        mutationKey: ['CreateHairShopReservation'],
        mutationFn: (params: CreateReservationParams) => createHairShopReservation(params),
        onError: error => {
            console.log(error)
        },
        onSuccess: data => {
            console.log(data)
        }

    })
}

export default useCreateHairShopReservation
