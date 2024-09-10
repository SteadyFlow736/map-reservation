import {QueryClient} from "@tanstack/query-core";

// query client
export const queryClient = new QueryClient()

// query keys
type QueryKey = "shopDetail" | "reservationSlots" | "reservations"
export const QueryKeys: Record<QueryKey, QueryKey> = {
    shopDetail: "shopDetail",
    reservationSlots: "reservationSlots",
    reservations: "reservations"
};

// mutation keys
type MutationKey = "cancelHairShopReservation"
export const MutationKeys: Record<MutationKey, MutationKey> = {
    cancelHairShopReservation: "cancelHairShopReservation"
}
