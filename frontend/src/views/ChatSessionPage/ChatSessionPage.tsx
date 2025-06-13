import { useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";
import { IMessage } from "./ChatSessionPage.interface";
import { showToast } from "../../utils/ToastUtil";
import { getAllMessageBySessionId } from "../Dashboard/Dashboard.api";
import { SummaryContent } from "./components/SummaryContent/SummaryContent";
import { ChatInput } from "./components/ChatInput/ChatInput";
import { Toast } from "primereact/toast";
import { generateResponse } from "./ChatSessionPage.api";
import { useDispatch, useSelector } from "react-redux";
import { sessionState, setSessionLoading } from "./ChatSessionPage.slice";

export const ChatSessionPage = () => {

    const userId = localStorage.getItem("userId") || "";
    const { sessionId } = useParams();
    const [messages, setMessages] = useState<IMessage[]>([]);
    const searchParams = new URLSearchParams(window.location.search);
    const isRedirected = searchParams.get("isRedirected");
    const { isCodeModelEnabled } = useSelector(sessionState);

    const dispatch = useDispatch();
    const toast = useRef(null);
    
      const handleNewMessage = async (message: IMessage) => {
        setMessages(prev => {
          return [
            ...prev,
            message
          ]
        })
      }

      const createAIResponse = async (input:string) => {
        await dispatch(setSessionLoading({ sessionId, isLoading: true }));
        const res = await generateResponse({ userId, sessionId: sessionId || "", userPrompt: input, isCodeModelEnabled });
        
        if(res) {
          await dispatch(setSessionLoading({ sessionId, isLoading: false }));
          await handleNewMessage({ id: res,message: res, isAIGenerated: true, createdAt: new Date() });
        }
      }

    useEffect(() => {
        const getAllMessages = async (sessionId: string) => {
            try{
                  const messages = await getAllMessageBySessionId(userId, sessionId);
                  setMessages(messages);

                  if(isRedirected) {
                    await createAIResponse(messages && messages[0].message);
                  }
                }catch(e) {
                  const error = e as Error;
                  showToast(toast, "error", "Error", error?.message);
                  await dispatch(setSessionLoading({ sessionId, isLoading: false }));
                }
        }
        getAllMessages(sessionId || "");
    }, [sessionId, userId]);

  return (
    <>
        <SummaryContent messages={messages} sessionId={sessionId || ""}/>
        <ChatInput sessionId={sessionId || ""} handleNewMessage={handleNewMessage} />
        <Toast ref={toast} />
    </>
  )
}
