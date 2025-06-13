import { useRef, useState } from "react";
import { useParams } from "react-router-dom";
import { IMessage } from "./ChatSessionPage.interface";
import { SummaryContent } from "./components/SummaryContent/SummaryContent";
import { ChatInput } from "./components/ChatInput/ChatInput";
import { Toast } from "primereact/toast";

export const DefaultSessionPage = () => {

    const { sessionId } = useParams();
    const [messages, setMessages] = useState<IMessage[]>([]);
    const toast = useRef(null);
      const handleNewMessage = async (message: IMessage) => {
        setMessages(prev => {
          return [
            ...prev,
            message
          ]
        })
      }


  return (
    <>
        <SummaryContent messages={messages} sessionId=""/>
        <ChatInput sessionId={sessionId || ""} handleNewMessage={handleNewMessage}/>
        <Toast ref={toast} />
    </>
  )
}
