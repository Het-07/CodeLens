import { AxiosError } from "axios";
import axiosInstance from "../../api/axiosInstance";
import { ICreateSession, IError, IUpdateSession } from "./SidebarLayout.interface";

export const createSession = async (payload: ICreateSession) => {
    try{
        const {data: response } = await axiosInstance.post(`/session/`, payload);
        return response.body.session;
    }catch(e) {
        const error = e as AxiosError;
        const errorBody = error.response?.data as IError
        throw new Error(errorBody.body.message)
    }
}

export const deleteSession = async (userId: string, sessionId: string) => {
    try {
        const {data: response } = await axiosInstance.delete(`/session/${userId}/${sessionId}`);
        return response.statusCode === 204 ? true : false;
    } catch (e) {
        const error = e as AxiosError;
        const errorBody = error.response?.data as IError
        throw new Error(errorBody.body.message)
    }
}

export const renameSession = async (payload: IUpdateSession) => {
    try {
        const {data: response } = await axiosInstance.put(`/session/`, payload);
        return response.body.session;
    } catch (e) {
        const error = e as AxiosError;
        const errorBody = error.response?.data as IError
        throw new Error(errorBody.body.message)
    }
}