export interface ILogin {
    email: string,
    password: string
}


export interface IAuth {
    userId: string,
    token: string,
    message: string,
    isError: boolean,
    isSuccess: boolean
}