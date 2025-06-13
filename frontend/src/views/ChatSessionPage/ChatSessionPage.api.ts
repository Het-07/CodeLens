import { AxiosError } from "axios";
import axiosInstance from "../../api/axiosInstance";
import { ICreateMessage, IGenerateResponse } from "./ChatSessionPage.interface";
import { IError } from "../ForgotPassword/ForgotPassword.interface";

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

export const getDocxFile =  async(messageId :string , sessionName: string) => {
    try {
     
      const response = await axiosInstance.get(`/download/${messageId}`);
  
      const base64String = response.data.body.response.trim(); // Ensure no extra spaces

      if (!base64String) {
        return false;
      }
     
      // Decode Base64 to Binary
      const byteCharacters = atob(base64String);
      const byteNumbers = new Array(byteCharacters.length);
      for (let i = 0; i < byteCharacters.length; i++) {
        byteNumbers[i] = byteCharacters.charCodeAt(i);
      }
      const byteArray = new Uint8Array(byteNumbers);
      const blob = new Blob([byteArray], { type: "application/vnd.openxmlformats-officedocument.wordprocessingml.document" });
  
      // Create Download Link
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", `${sessionName}.docx`);
      document.body.appendChild(link);
      link.click();
  
      // Cleanup
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
      return true;
  
    } catch (e) {
        const error = e as AxiosError;
        const errorBody = error.response?.data as IError
        throw new Error(errorBody.body.message)
    }
    } 