import axios, { AxiosError } from "axios";
import { CONFIG } from "../../config/dev.config";
import { IResendOtp, IVerifyOtp } from "./VerifyOtp.interface";
import { IError } from "../ForgotPassword/ForgotPassword.interface";

export const verifyOtp = async (payload: IVerifyOtp) => {
    try {
        const res = await axios.post(`${CONFIG.BASE_URL}/auth/verifyOtp`, payload);

        return res.data;
    } catch (e) {
        const error = e as AxiosError;
        const errorBody = error.response?.data as IError
        throw new Error(errorBody.body.message);
    }
}

export const resendOTP = async (payload: IResendOtp) => {
    try {
        const res = await axios.post(`${CONFIG.BASE_URL}/auth/resendOtp`, payload);
        return res.data;
    } catch (e) {
        const error = e as AxiosError;
        const errorBody = error.response?.data as IError
        throw new Error(errorBody.body.message);
    }
}