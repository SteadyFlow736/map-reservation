import {skipToken, useQuery} from "@tanstack/react-query";
import {QueryKeys} from "@/config/queryClient";
import {fetchReservationStatus} from "@/api/reservation";

function useReservationSlots(hairShopId: number | undefined, targetDate: Date | undefined) {
    return useQuery({
            queryKey: [QueryKeys.reservationSlots, hairShopId, targetDate],
            queryFn: hairShopId && targetDate ? () => fetchReservationStatus(hairShopId, targetDate) : skipToken
        }
    )
}

export default useReservationSlots
