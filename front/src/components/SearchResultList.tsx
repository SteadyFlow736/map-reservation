import StoreSummaryCard from "@/components/StoreSummaryCard";

function SearchResultList() {
    const searchResultList: SearchResultDto[] = [
        {
            storeId: 1,
            storeName: '메이원 헤어',
            stars: 5,
            summary: '네이버 첫방문 예약고객 N페이&리뷰시 펌/염색/클리닉 20%할인(부분시술,원장,부원장 제외)',
            images: []
        },
        {
            storeId: 2,
            storeName: '라뷰티코아',
            stars: 4.5,
            summary: '- 네이버 첫 방문 최대 40%할인\n' + '- Pay 결제시 최대 7% 포인트 적립',
            images: []
        },
        {
            storeId: 3,
            storeName: '라뷰티코아',
            stars: 4.5,
            summary: '- 네이버 첫 방문 최대 40%할인\n' + '- Pay 결제시 최대 7% 포인트 적립',
            images: []
        },
        {
            storeId: 4,
            storeName: '라뷰티코아',
            stars: 4.5,
            summary: '- 네이버 첫 방문 최대 40%할인\n' + '- Pay 결제시 최대 7% 포인트 적립',
            images: []
        },
        {
            storeId: 5,
            storeName: '라뷰티코아',
            stars: 4.5,
            summary: '- 네이버 첫 방문 최대 40%할인\n' + '- Pay 결제시 최대 7% 포인트 적립',
            images: []
        },
        {
            storeId: 6,
            storeName: '라뷰티코아',
            stars: 4.5,
            summary: '- 네이버 첫 방문 최대 40%할인\n' + '- Pay 결제시 최대 7% 포인트 적립',
            images: []
        },
        {
            storeId: 7,
            storeName: '라뷰티코아',
            stars: 4.5,
            summary: '- 네이버 첫 방문 최대 40%할인\n' + '- Pay 결제시 최대 7% 포인트 적립',
            images: []
        },
        {
            storeId: 8,
            storeName: '라뷰티코아',
            stars: 4.5,
            summary: '- 네이버 첫 방문 최대 40%할인\n' + '- Pay 결제시 최대 7% 포인트 적립',
            images: []
        },
        {
            storeId: 9,
            storeName: '라뷰티코아',
            stars: 4.5,
            summary: '- 네이버 첫 방문 최대 40%할인\n' + '- Pay 결제시 최대 7% 포인트 적립',
            images: []
        },
        {
            storeId: 10,
            storeName: '라뷰티코아',
            stars: 4.5,
            summary: '- 네이버 첫 방문 최대 40%할인\n' + '- Pay 결제시 최대 7% 포인트 적립',
            images: []
        },
        {
            storeId: 11,
            storeName: '라뷰티코아',
            stars: 4.5,
            summary: '- 네이버 첫 방문 최대 40%할인\n' + '- Pay 결제시 최대 7% 포인트 적립',
            images: []
        },

    ]

    return (
        <div className="overflow-y-auto divide-y">
            {searchResultList.map(s => <StoreSummaryCard key={s.storeId} storeSummary={s}/>)}
        </div>
    )
}

export default SearchResultList
