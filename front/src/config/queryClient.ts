import {QueryClient} from "@tanstack/query-core";

export const queryClient = new QueryClient()

type QueryKey = "shopDetail" | "anotherKey"
export const QueryKeys: Record<QueryKey, QueryKey> = {
    shopDetail: "shopDetail",
    anotherKey: "anotherKey"
};
