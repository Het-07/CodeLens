import { AxiosError } from "axios";
import { ICreateMessage, ICreateSession, IError, IGenerateResponse, ISession, IUpdateSession } from "./Dashboard.interface";
import axiosInstance from "../../api/axiosInstance";

export const getAllUserSession = async (userId: string): Promise<ISession[]> => {
    try {
        const {data: response } = await axiosInstance.get(`/session/${userId}`);
        return response.body.session;
    } catch (e) {
        const error = e as AxiosError;
        const errorBody = error.response?.data as IError
        throw new Error(errorBody.body.message)
    }
}

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

export const getAllMessageBySessionId = async (userId: string, sessionId: string ) => {
    try {
        const {data: response } = await axiosInstance.get(`/message/${userId}/${sessionId}`);
        return response.body.messages;
    } catch (e) {
        const error = e as AxiosError;
        const errorBody = error.response?.data as IError
        throw new Error(errorBody.body.message)
    }
}

export const createMessage = async (payload: ICreateMessage) => {
    try {
        const {data: response } = await axiosInstance.post(`/message/`, payload);
        return response.body.response;
    } catch (e) {
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

export const generateResponse = async (payload: IGenerateResponse) => {
    try {
        const {data: response } = await axiosInstance.post(`/ollama/generate/descriptive`, payload);
        return response.body.message;
    } catch (e) {
        const error = e as AxiosError;
        const errorBody = error.response?.data as IError
        throw new Error(errorBody.body.message)
    }
}