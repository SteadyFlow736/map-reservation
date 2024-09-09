import {api_base_url} from "@/envs";
import axios from "axios";
import qs from "qs";

const baseURL = api_base_url

export const instance = axios.create({
    baseURL,
    // https://developer.mozilla.org/en-US/docs/Web/API/XMLHttpRequest/withCredentials
    // csrf token을 받아오려면 당연히 받아올 토근과 관련된 세션 아이디도 요청에 같이 포함되어야 한다.
    // cookie에 세션 아이디가 있으므로, withCredentials를 true로 하여 쿠키를 보내도록 한다.
    withCredentials: true,
    paramsSerializer: (params) => {
        // ?sort=field1,asc&sort=field2,desc 처럼 쿼리 문자열에서 sort를 반복된 키로 직렬화
        return qs.stringify(params, {arrayFormat: 'repeat'})
    }
})
