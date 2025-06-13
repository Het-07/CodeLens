import { useEffect, useState } from "react";
import { Button } from "primereact/button";
import "./ChatInput.css";
import { createMessage, generateResponse } from "../../ChatSessionPage.api";
import { showToast } from "../../../../utils/ToastUtil";
import { Toast } from "primereact/toast";
import { IMessage } from "../../ChatSessionPage.interface";
import { useDispatch, useSelector } from "react-redux";
import {
  sessionState,
  setAllSessions,
  setCurrentSession,
  setSessionLoading,
} from "../../ChatSessionPage.slice";
import { createSession } from "../../../SidebarLayout/SidebarLayout.api";
import { DEFAULT_SESSION_NAME } from "../../../SidebarLayout/SidebarLayout.constants";
import { useFetchUserSessions } from "../../../Dashboard/hooks/useFetchUserSessions";
import { useNavigate } from "react-router-dom";
import { ToggleModel } from "./components/ToggleModel";

interface chatInputProps {
  sessionId: string;
  handleNewMessage: (message: IMessage) => Promise<void>;
}

export const ChatInput = ({ sessionId, handleNewMessage }: chatInputProps) => {
  const [input, setInput] = useState("");
  //const toast = useRef(null);
  const userId = localStorage.getItem("userId") || "";
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { toast, setSessions } = useFetchUserSessions(userId);
  const { allSessions } = useSelector(sessionState);
  const [isCodeModelEnabled, setIsCodeModelEnabled] = useState(false);

  useEffect(() => {
    setInput("");
  }, [sessionId]);

  const handleSubmit = async (e: { preventDefault: () => void }) => {
    e.preventDefault();

    if (!sessionId) {
      try {
        const newSession = await createSession({
          userId: localStorage.getItem("userId") || "",
          sessionName: DEFAULT_SESSION_NAME,
        });
        if (newSession) {
          setSessions((prev) => {
            return [newSession, ...prev];
          });
        }
        sessionId = newSession.id;
        dispatch(setAllSessions({ allSessions: [newSession, ...allSessions] }));
        dispatch(setCurrentSession({ session: newSession }));
        await dispatch(setSessionLoading({ sessionId, isLoading: true }));
        navigate(`/session/${sessionId}`);
      } catch (e) {
        const error = e as Error;
        showToast(toast, "error", "Error", error?.message);
      }
    }

    try {
      const userId = localStorage.getItem("userId") || "";
      const newMessage = await createMessage({
        userId,
        sessionId: sessionId,
        isAIGenerated: false,
        message: input,
      });
      await handleNewMessage(newMessage);
      setInput("");
      await dispatch(setSessionLoading({ sessionId, isLoading: true }));

      const res = await generateResponse({
        userId,
        sessionId: sessionId,
        userPrompt: input,
        isCodeModelEnabled,
      });
      if (res) {
        await dispatch(setSessionLoading({ sessionId, isLoading: false }));
        await handleNewMessage({
          id: res,
          message: res,
          isAIGenerated: true,
          createdAt: new Date(),
        });
      }
    } catch (e) {
      const error = e as Error;
      showToast(toast, "error", "Error", error?.message);
      await dispatch(setSessionLoading({ sessionId, isLoading: false }));
    }
  };

  return (
    <div className="chat-input-wrapper">
      <form className="chat-input-container" onSubmit={handleSubmit}>
        <ToggleModel
          onToggle={() => setIsCodeModelEnabled((prev) => !prev)}
          isCodeModelEnabled={isCodeModelEnabled}
        />
        <input
          type="text"
          className="chat-input-field"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          placeholder="What do you want to know?"
        />
        <Button type="submit" label="Send" className="chat-input-button" />
        <Toast ref={toast} />
      </form>
    </div>
  );
};
