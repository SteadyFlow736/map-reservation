import {useContext, useState} from "react";
import Calendar from "react-calendar";
import 'react-calendar/dist/Calendar.css';
import './react-calendar-custom.css'
import {skipToken, useQuery} from "@tanstack/react-query";
import {QueryKeys} from "@/config/queryClient";
import {fetchReservationStatus} from "@/api";
import {useAtomValue} from "jotai";
import {selectedHairShopIdAtom} from "@/atoms";
import Time from "@/utils/Time";
import {ShopMainPageContext, TimeSlot, TimeSlotContext} from "@/components/ShopDetailPage/ShopDetailWrapperPage";

type ValuePiece = Date | null
type Value = ValuePiece | [ValuePiece, ValuePiece]

/**
 * 샵 예약 서브 페이지
 * 예약 가능한 날짜와 시간을 달력에서 조회하고 예약 진행할 수 있다.
 */
function ShopSubPageReservation() {
    const today = new Date()
    const [value, onChange] = useState<Value>(today) // 자동으로 달력에서 오늘 선택
    const minDate = today // 달력에서 오늘부터 선택가능
    const selectedHairShopId = useAtomValue(selectedHairShopIdAtom)
    const {selectedTimeSlot, setSelectedTimeSlot} = useContext(TimeSlotContext)

    const {data, status} = useQuery({
        queryKey: [QueryKeys.reservationStatus, value],
        queryFn: selectedHairShopId && value && value instanceof Date ?
            () => fetchReservationStatus(selectedHairShopId.shopId, value) : skipToken
    })

    const banner = selectedTimeSlot ? selectedTimeSlot.dateTime.toLocaleString() : '날짜와 시간을 선택해 주세요'

    return (
        <TimeSlotContext.Provider value={{selectedTimeSlot, setSelectedTimeSlot}}>
            <div className="bg-white p-3">
                <p className="text-xl my-3">{banner}</p>

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
                <NextButton/>
            </div>
        </TimeSlotContext.Provider>
    )
}

/**
 * 다음 단계(예약 확정 페이지)로 넘어가는 버튼 component
 * 예약 시간이 선택되었을 때만 누를 수 있다.
 */
function NextButton() {
    const {selectedTimeSlot} = useContext(TimeSlotContext)
    const {setShopMainPage} = useContext(ShopMainPageContext)
    const selectedHairShopId = useAtomValue(selectedHairShopIdAtom)

    const next = async () => {
        setShopMainPage('ReservationVerify')
    }

    return (
        <button
            className={`
            btn w-full mt-3 
            ${selectedTimeSlot ? 'bg-green-400 text-white' : ''}
            `}
            disabled={!selectedTimeSlot}
            onClick={next}
        >
            다음 단계
        </button>
    )
}

/**
 * 예약 시간 선택 버튼 그룹 component
 */
function ReservationSelectButtons(
    {reservationStatus}: { reservationStatus: HairShopReservationStatus }) {

    const {date, openingTime, closingTime, reservedTimes} = reservationStatus
    const slots: TimeSlot[] = getTimeSlots(date, openingTime, closingTime, reservedTimes)

    return (
        <div className="grid grid-cols-4 gap-2">
            {slots.map((slot, index) => <TimeSelectButton key={index} slot={slot}/>)}
        </div>
    )
}

/**
 * 예약 시간 선택 버튼 component
 */
function TimeSelectButton({slot}: { slot: TimeSlot }) {
    const {selectedTimeSlot, setSelectedTimeSlot} = useContext(TimeSlotContext)
    const hourString = slot.dateTime.getHours().toString().padStart(2, '0')
    const minutesString = slot.dateTime.getMinutes().toString().padStart(2, '0')
    const selected = selectedTimeSlot?.dateTime.getTime() === slot.dateTime.getTime()

    return (
        <button
            className={`
            btn
            ${selected ? 'bg-green-400 text-white' : ''} 
            `}
            disabled={slot.reserved}
            onClick={() => setSelectedTimeSlot(slot)}
        >
            {`${hourString}:${minutesString}`}
        </button>
        // <Button onClick={undefined} label={`${hourString}:${minutesString}`} on={!slot.reserved}/>
    );
}

/**
 * 예약 가능한 시간 리스트를 생성.
 *
 * 영업 시작 시간, 영업 종료 시간 사이를 30분씩 나눠 시간 슬롯 리스트를 생성하고, 각 시간이 이미 예약되었는지 여부도 표시한다.
 * @param date 기준 날짜
 * @param openingTime 영업 시작 시간
 * @param closingTime 영업 종료 시간
 * @param reservedTimes 예약된 시간 리스트
 * @return 기준 날짜의 예약 가능한 시간 슬롯 리스트
 */
const getTimeSlots = (date: string, openingTime: string, closingTime: string,
                      reservedTimes: string[]): TimeSlot[] => {

    let currentDate = Time.stringToDate(date, openingTime)
    const closingDate = Time.stringToDate(date, closingTime)
    const slots: TimeSlot[] = []

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

export default ShopSubPageReservation
