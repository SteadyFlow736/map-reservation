import StoreSummaryCard from "@/components/StoreSummaryCard";

function SearchResultList() {
    const searchResultList: SearchResultDto[] = [
        {
            storeName: '메이원 헤어',
            stars: 5,
            summary: '네이버 첫방문 예약고객 N페이&리뷰시 펌/염색/클리닉 20%할인(부분시술,원장,부원장 제외)',
            images: []
        },
        {
            storeName: '라뷰티코아',
            stars: 4.5,
            summary: '- 네이버 첫 방문 최대 40%할인\n' + '- Pay 결제시 최대 7% 포인트 적립',
            images: []
        },
        {
            storeName: '라뷰티코아',
            stars: 4.5,
            summary: '- 네이버 첫 방문 최대 40%할인\n' + '- Pay 결제시 최대 7% 포인트 적립',
            images: []
        },
        {
            storeName: '라뷰티코아',
            stars: 4.5,
            summary: '- 네이버 첫 방문 최대 40%할인\n' + '- Pay 결제시 최대 7% 포인트 적립',
            images: []
        },
        {
            storeName: '라뷰티코아',
            stars: 4.5,
            summary: '- 네이버 첫 방문 최대 40%할인\n' + '- Pay 결제시 최대 7% 포인트 적립',
            images: []
        },
        {
            storeName: '라뷰티코아',
            stars: 4.5,
            summary: '- 네이버 첫 방문 최대 40%할인\n' + '- Pay 결제시 최대 7% 포인트 적립',
            images: []
        },
        {
            storeName: '라뷰티코아',
            stars: 4.5,
            summary: '- 네이버 첫 방문 최대 40%할인\n' + '- Pay 결제시 최대 7% 포인트 적립',
            images: []
        },
        {
            storeName: '라뷰티코아',
            stars: 4.5,
            summary: '- 네이버 첫 방문 최대 40%할인\n' + '- Pay 결제시 최대 7% 포인트 적립',
            images: []
        },
        {
            storeName: '라뷰티코아',
            stars: 4.5,
            summary: '- 네이버 첫 방문 최대 40%할인\n' + '- Pay 결제시 최대 7% 포인트 적립',
            images: []
        },
        {
            storeName: '라뷰티코아',
            stars: 4.5,
            summary: '- 네이버 첫 방문 최대 40%할인\n' + '- Pay 결제시 최대 7% 포인트 적립',
            images: []
        },
        {
            storeName: '라뷰티코아',
            stars: 4.5,
            summary: '- 네이버 첫 방문 최대 40%할인\n' + '- Pay 결제시 최대 7% 포인트 적립',
            images: []
        },

    ]

    return (
        <div className="overflow-y-auto divide-y">
            {searchResultList.map(s => <StoreSummaryCard storeSummary={s}/>)}
        </div>
    )
}

export default SearchResultList
