import { CONFIG } from "../config/dev.config"
import { STORAGE_KEYS } from "../constants/constants";
import { getFromLocalStorage } from "../utils/utils.function";
import { ICreateMessage, ICreateSession, ICreateUserMessage } from "./serviceFunction.interface";

export const generateResponse = async (payload: ICreateMessage) => {
    try {
        const res = await getFromLocalStorage([STORAGE_KEYS.TOKEN]);
        const response = await fetch(`${CONFIG.BASE_URL}/ollama/generate/short`, {
            method: 'POST',
            headers: {
                "Content-Type": "application/json",
                "userId": payload.userId,
                "message": payload.userPrompt,
                Authorization: `Bearer ${res.token}`
            },
            body: JSON.stringify(payload)
        });
        
        const json = await response.json();
        return json;
    } catch (error) {
        throw error;
    }
}

export const createSession = async (payload: ICreateSession) => {
    try {
        const res = await getFromLocalStorage([STORAGE_KEYS.TOKEN]);
        const response = await fetch(`${CONFIG.BASE_URL}/redirect/codelens`, {
            method: 'POST',
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${res.token}`
            },
            body: JSON.stringify(payload)
        });
        
        const json = await response.json();
        return json;
    } catch (error) {
        throw error;
    }
}

export const createMessage  = async (payload: ICreateUserMessage) => {
    try{
        const res = await getFromLocalStorage([STORAGE_KEYS.TOKEN]);
        const response = await fetch(`${CONFIG.BASE_URL}/message/`, {
            method: 'POST',
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${res.token}`
            },
            body: JSON.stringify(payload)
        });
        
        const json = await response.json();
        return json;
    }catch(error){
        throw error;
    }
}

export const createUserSession = async (payload) => {
    try {
        const res = await getFromLocalStorage([STORAGE_KEYS.TOKEN]);
        const response = await fetch(`${CONFIG.BASE_URL}/session/`, {
            method: 'POST',
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${res.token}`
            },
            body: JSON.stringify(payload)
        });
        
        const json = await response.json();
        return json;
    } catch (error) {
        throw error;
    }
}

export const downloadDoc = async (messageId: string) => {
    try {
        const res = await getFromLocalStorage([STORAGE_KEYS.TOKEN]);
        const response = await fetch(`${CONFIG.BASE_URL}/download/${messageId}`, {
            method: 'GET',
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${res.token}`
            },
        });
        
        const json = await response.json();
        return json;
    } catch (error) {
        throw error;
    }
}

export const getSessionById = async (sessionId: string) => {
    try {
        const res = await getFromLocalStorage([STORAGE_KEYS.TOKEN]);
        const response = await fetch(`${CONFIG.BASE_URL}/session/id/${sessionId}`, {
            method: 'GET',
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${res.token}`
            },
        });
        
        const json = await response.json();
        return json;
    } catch (error) {
        throw error;
    }
}

export const deleteSession = async (userId: string,sessionId: string) => {
    try {
        const res = await getFromLocalStorage([STORAGE_KEYS.TOKEN]);
        const response = await fetch(`${CONFIG.BASE_URL}/session/${userId}/${sessionId}`, {
            method: 'DELETE',
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${res.token}`
            },
        });
        
        const json = await response.json();
        return json;
    } catch (error) {
        throw error;
    }
}