import axios, { AxiosError } from "axios";
import { IResetPassword } from "./ResetPassword.interface";
import { CONFIG } from "../../config/dev.config";
import { IError } from "../ForgotPassword/ForgotPassword.interface";

export const resetPassword = async (payload: IResetPassword) => {
    try {
        const newPassword = await axios.post(`${CONFIG.BASE_URL}/auth/resetPassword`, payload);
        return newPassword.data;
    } catch (e) {
        const error = e as AxiosError;
        const errorBody = error.response?.data as IError;
        throw new Error(errorBody.body.message);
    }
}