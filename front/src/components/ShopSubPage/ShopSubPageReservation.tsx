import Calendar from "react-calendar";
import {useState} from "react";
import 'react-calendar/dist/Calendar.css';
import './react-calendar-custom.css'
import {skipToken, useQuery} from "@tanstack/react-query";
import {QueryKeys} from "@/config/queryClient";
import {fetchReservationStatus} from "@/api";
import {useAtomValue} from "jotai";
import {selectedHairShopIdAtom} from "@/atoms";
import Button from "@/components/Button";
import Time from "@/utils/Time";

type ValuePiece = Date | null
type Value = ValuePiece | [ValuePiece, ValuePiece]

function ShopSubPageReservation() {
    const today = new Date()
    const [value, onChange] = useState<Value>(today) // 자동으로 달력에서 오늘 선택
    const minDate = today // 달력에서 오늘부터 선택가능
    const selectedHairShopId = useAtomValue(selectedHairShopIdAtom)

    const {data, status} = useQuery({
        queryKey: [QueryKeys.reservationStatus, value],
        queryFn: selectedHairShopId && value && value instanceof Date ?
            () => fetchReservationStatus(selectedHairShopId.shopId, value) : skipToken
    })

    return (
        <div className="bg-white p-3">
            <p className="text-xl my-3">날짜와 시간을 선택해 주세요</p>

            {/* 달력 */}
            <Calendar defaultView="month" minDate={minDate}
                      onChange={onChange} value={value}
                      formatDay={(_, date) => date.getDate().toString()}
            />

            {/* divider */}
            <div className="border border-b-gray-100 my-3"></div>

            {/* 예약 시간 선택 버튼 */}
            {status == 'pending' ? <div>Loading</div> : null}
            {status == 'error' ? <div>에러가 발생했습니다. 잠시 후 다시 시도해 주세요.</div> : null}
            {status == 'success' ? <ReservationSelectButtons reservationStatus={data}/> : null}

            {/* 다음 단계 버튼 */}
            <div className="mt-3">
                <Button onClick={undefined} label="다음 단계"/>
            </div>
        </div>
    )
}

/**
 * 예약 시간 선택 버튼 그룹 component
 */
function ReservationSelectButtons(
    {reservationStatus}: { reservationStatus: HairShopReservationStatus }) {

    const {date, openingTime, closingTime, reservedTimes} = reservationStatus
    const slots: ReservationSlot[] = getTimeSlots(date, openingTime, closingTime, reservedTimes)

    return (
        <div className="grid grid-cols-4 gap-2">
            {slots.map((slot, index) => <TimeSelectButton key={index} slot={slot}/>)}
        </div>
    )
}

/**
 * 예약 시간 선택 버튼 component
 */
function TimeSelectButton({slot}: { slot: ReservationSlot }) {
    const hourString = slot.dateTime.getHours().toString().padStart(2, '0')
    const minutesString = slot.dateTime.getMinutes().toString().padStart(2, '0')

    return (
        <Button onClick={undefined} label={`${hourString}:${minutesString}`} on={!slot.reserved}/>
    );
}

const getTimeSlots = (date: string, openingTime: string, closingTime: string,
                      reservedTimes: string[]): ReservationSlot[] => {

    let currentDate = Time.stringToDate(date, openingTime)
    const closingDate = Time.stringToDate(date, closingTime)
    const slots: ReservationSlot[] = []

    while (currentDate < closingDate) {
        slots.push({
            dateTime: currentDate,
            reserved: false
        })
        currentDate = Time.addMinutesToDateTime(currentDate, 30)
    }

    reservedTimes.forEach(reservedTime => {
        const slot = slots.find(slot => slot.dateTime === Time.stringToDate(date, reservedTime))
        if (slot) slot.reserved = true
    })

    return slots
}

type ReservationSlot = {
    dateTime: Date
    reserved: boolean
}

export default ShopSubPageReservation
