import Calendar from "react-calendar";
import {useState} from "react";
import 'react-calendar/dist/Calendar.css';
import './react-calendar-custom.css'

type ValuePiece = Date | null
type Value = ValuePiece | [ValuePiece, ValuePiece]

function ShopSubPageReservation() {
    const [value, onChange] = useState<Value>()
    const minDate = new Date() // 달력에서 오늘부터 선택가능
    console.log(value)

    return (
        <div className="bg-white p-3">
            <p className="text-xl my-3">날짜와 시간을 선택해 주세요</p>
            <Calendar defaultView="month" minDate={minDate}
                      onChange={onChange} value={value}
                      formatDay={(_, date) => date.getDate().toString()}
            />
        </div>
    )
}

export default ShopSubPageReservation
