import {QueryClient} from "@tanstack/query-core";

export const queryClient = new QueryClient()

type QueryKey = "shopDetail" | "reservationStatus" | "reservations"
export const QueryKeys: Record<QueryKey, QueryKey> = {
    shopDetail: "shopDetail",
    reservationStatus: "reservationStatus",
    reservations: "reservations"
};
