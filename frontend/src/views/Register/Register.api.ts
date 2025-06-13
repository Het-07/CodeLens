import axios, { AxiosError } from "axios";
import { IRegisterUser } from "./Register.interface";
import { CONFIG } from "../../config/dev.config";
import { IError } from "../ForgotPassword/ForgotPassword.interface";

export const registrationApi = async (payload: IRegisterUser) => {
    try {
        const response = await axios
          .post(`${CONFIG.BASE_URL}/auth/register`, payload);
    
        return response.data;
    } catch (e) {
        const error = e as AxiosError;
        const errorBody = error.response?.data as IError
        throw new Error(errorBody.body.message);
    }
};