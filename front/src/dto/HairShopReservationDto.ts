type HairShopReservationDto = {
    reservationId: number
    username: string
    hairShopDto: HairShopDto
    reservationTime: string
    status: Status
}

type Status = "RESERVED" | "CANCELLED"
