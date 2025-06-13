export interface IForgotPassword{
    email: string
}

export interface IError {
    body: IErrorBody
}

export interface IErrorBody {
    message: string,
    query_params: object,
    exception_type: string,
    method: string

}