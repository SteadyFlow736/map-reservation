import Error from "next/error";

export default function CustomError() {
    return <Error statusCode={400}/>
}
