import axios, { AxiosError } from "axios";
import { IError, IForgotPassword } from "./ForgotPassword.interface";
import { CONFIG } from "../../config/dev.config";

export const sendForgotPasswordMail = async (payload: IForgotPassword) => {
    try{
        const response = await axios.post(`${CONFIG.BASE_URL}/auth/forgotPassword`, payload);
        return response.data;
    }catch(e) {
        const error = e as AxiosError;
        const errorBody = error.response?.data as IError
        throw new Error(errorBody.body.message)
    }
}