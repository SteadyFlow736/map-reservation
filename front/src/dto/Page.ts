type Page<T> = {
    totalPages: number          // 총 페이지 갯수
    totalElements: number       // 총 요소 수
    number: number              // 현 페이지 번호
    numberOfElements: number    // 현 페이지에 담겨 있는 요소 수
    size: number                // 요청된 페이지 사이즈
    content: T[]                // 조회된 데이터
}
