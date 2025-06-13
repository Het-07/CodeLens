import { DEFAULT_SESSION_NAME, MESSAGE_LISTENER, STORAGE_KEYS } from "../constants/constants"
import { getFromLocalStorage, setToLocalStorage } from "../utils/utils.function";
import { createMessage, createSession, createUserSession, deleteSession, downloadDoc, generateResponse, getSessionById } from "./api.function";

chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
    switch (message.type) {
        case MESSAGE_LISTENER.SEND_PROMPT:
            ( async () => {
                await sendUserPrompt(message, sender, sendResponse);
            })();
            return true;
        break;

        case MESSAGE_LISTENER.CREATE_SESSION:
            (async () => {
                await createSessionOnRedirection(message, sender, sendResponse)
            })();
            return true;
        break;

        case MESSAGE_LISTENER.SAVE_RESPONSE:
            (async () => {
                await saveAIResponse(message, sender, sendResponse);
            })();
            return true;
        break;

        case MESSAGE_LISTENER.DOWNLOAD_RESPONSE:
            (async () => {
                await downloadResponse(message, sender, sendResponse);
            })();
            return true;
        break;
    }
})


const sendUserPrompt = async (message, sender, sendResponse) => {
    try {
        const res = await getFromLocalStorage([STORAGE_KEYS.USERID]);
        const response = await generateResponse({ userId: res.userId, userPrompt: message.text, sessionId: "", isCodeModelEnabled: false });
        sendResponse({ status: true, response: response.body.message });
    } catch (error) {
        sendResponse({ status: false, error: error.message });
    }
}

const createSessionOnRedirection = async (message, sender , sendResponse) => {
    try {
        const res = await getFromLocalStorage([STORAGE_KEYS.USERID]);
        const response = await createSession({ userId: res.userId, userPrompt: message.text });
        sendResponse({ status: true, response: response.body });
    } catch (error) {
        sendResponse({  status: false, error: error.message })
    }
}

const saveAIResponse = async (message, sender, sendResponse) => {
    try {
        const res = await getFromLocalStorage([STORAGE_KEYS.SESSION_ID, STORAGE_KEYS.USERID]);
        var sessionId = res.sessionId;
        if(!sessionId) {
            const sessionResponse = await createUserSession({ userId: res.userId, sessionName: DEFAULT_SESSION_NAME});
            sessionId = sessionResponse.body.session.id;
            await setToLocalStorage({ sessionId });
        }else {
            const existingSession = await getSessionById(sessionId);
            if(existingSession.body && existingSession.body.session) {
                sessionId = existingSession.body.session.id;
            }else {
                const sessionResponse = await createUserSession({ userId: res.userId, sessionName: DEFAULT_SESSION_NAME});
                sessionId = sessionResponse.body.session.id;
            }
            await setToLocalStorage({ sessionId });
        }
        
        const userPrompt = message.userPrompt;
        const llamaResponse = message.response;
        const storage = await getFromLocalStorage([STORAGE_KEYS.USERID]);
        const userMessageResponse = await createMessage({ userId: storage.userId, sessionId: sessionId, message: userPrompt, isAIGenerated: false });
        let AIMessageResponse;

        if(message.response)
            AIMessageResponse = await createMessage({ userId: storage.userId, sessionId: sessionId, message: llamaResponse, isAIGenerated: true });
        
        if(userMessageResponse.body.response && AIMessageResponse.body.response) 
            sendResponse({ status: true });
        else 
            sendResponse({ status: false, message: "Error while saving document" });
    } catch (error) {
        console.log(error,"error");
        sendResponse({ status: false, error:"Error while saving document" });
    }
}

const downloadResponse = async (message, sender, sendResponse) => {
    try {
        const res = await getFromLocalStorage([STORAGE_KEYS.SESSION_ID, STORAGE_KEYS.USERID]);
       
        if(!message.response)
            sendResponse({ status: false, message: "No content in response" });

        const sessionResponse = await createUserSession({ userId: res.userId, sessionName: DEFAULT_SESSION_NAME});

        if(sessionResponse.body && !sessionResponse.body.session?.id) {
            sendResponse({ status: false, message: "Error while downloading document" });
        }

        const sessionId = sessionResponse.body.session.id;
        
        const userPrompt = message.userPrompt;
        const llamaResponse = message.response;
        const storage = await getFromLocalStorage([STORAGE_KEYS.USERID]);
        const userMessageResponse = await createMessage({ userId: storage.userId, sessionId: sessionId, message: userPrompt, isAIGenerated: false });
        const AIMessageResponse = await createMessage({ userId: storage.userId, sessionId: sessionId, message: llamaResponse, isAIGenerated: true });
        if(userMessageResponse.body && AIMessageResponse.body) {
            let downloadResponse;
            if(AIMessageResponse.body.response) {
                downloadResponse = await downloadDoc(AIMessageResponse.body.response.id);

                const base64String = downloadResponse.body.response.trim(); // Ensure no extra spaces

                if (!base64String) {
                    return false;
                }
                await deleteSession(res.userId, sessionId);
                chrome.tabs.sendMessage(sender.tab.id, { action: "download", base64: base64String });
            }
        }
    } catch (error) {
        sendResponse({ status: false, error: error.message })
    }
}