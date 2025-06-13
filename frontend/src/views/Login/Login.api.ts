import axios, { AxiosError } from "axios";
import { ILogin } from "./Login.interface";
import { CONFIG } from "../../config/dev.config";
import { IError } from "../ForgotPassword/ForgotPassword.interface";

export const loginApi = async (payload: ILogin) => {
    try {
        const response = await axios
          .post(`${CONFIG.BASE_URL}/auth/authenticate`, payload);
    
        return response.data;
    } catch (e) {
        const error = e as AxiosError;
        const errorBody = error.response?.data as IError
        throw new Error(errorBody.body.message);
    }
};