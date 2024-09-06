type Slice<T> = {
    content: T[]    // 현재 페이지 내용
    last: boolean   // 마지막 페이지인지
    number: number  // 현재 페이지 번호
}
