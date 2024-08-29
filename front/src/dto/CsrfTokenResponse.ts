type CsrfTokenResponse = {
    csrfToken: CsrfToken
}

type CsrfToken = {
    headerName: string
    parameterName: string
    token: string
}
