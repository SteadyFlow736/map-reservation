type Pageable = {
    page: number    // 페이지 번호(0부터 시작)
    size: number    // 페이지 크기
    sort: string[]  // 정렬 예시 ['name,asc', 'createdDate,desc']
}

export default Pageable
