type CustomErrorResponse<T> = {
    code: string
    message: string
    errors: T
}
