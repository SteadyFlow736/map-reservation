import {MouseEventHandler} from "react";

/**
 * 공통 버튼 component
 *
 * @param onClick 클릭 callback
 * @param label 라벨
 * @param on 버튼 on 여부. 기본값 true.
 */
function Button({onClick, label, on = true}: {
    onClick: MouseEventHandler<HTMLButtonElement> | undefined,
    label: string,
    on?: boolean
}) {
    return (
        <button
            className="p-2 border-2 border-gray-100 rounded w-full
                    flex justify-center
                    hover:cursor-pointer
                    "
            onClick={onClick}
            disabled={!on}
        >
            {label}
        </button>
    )
}

export default Button
