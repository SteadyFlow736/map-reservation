type HairShopReservationDto = {
    reservationId: number
    username: string
    hairShopResponse: HairShopResponse
    reservationTime: string
    status: Status
}

type Status = "RESERVED" | "CANCELLED"
