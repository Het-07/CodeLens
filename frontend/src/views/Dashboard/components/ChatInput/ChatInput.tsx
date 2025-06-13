import { Dispatch, SetStateAction, useEffect, useRef, useState } from 'react';
import { Button } from 'primereact/button';
import './ChatInput.css';
import { createMessage, generateResponse } from '../../Dashboard.api';
import { showToast } from '../../../../utils/ToastUtil';
import { Toast } from 'primereact/toast';
import { IMessage, ISession } from '../../Dashboard.interface';

interface chatInputProps {
  selectedSession: ISession
  handleNewMessage: (message: IMessage) => Promise<void>,
  setLoading: Dispatch<SetStateAction<boolean>>
}

export const ChatInput = ({selectedSession, handleNewMessage, setLoading} : chatInputProps) => {
  const [input, setInput] = useState('');
  const toast = useRef(null);
  
  useEffect(() => {
    setInput('');
  },[selectedSession])

  const handleSubmit = async (e: { preventDefault: () => void; }) => {
    e.preventDefault();
    try {
      const userId = localStorage.getItem("userId") || "";
      const newMessage = await createMessage({ userId, sessionId: selectedSession.id, isAIGenerated: false, message: input });
      await handleNewMessage(newMessage);
      setLoading(true);

      const res = await generateResponse({ userId, sessionId: selectedSession.id, userPrompt: input });
      if(res) {
        setLoading(false);
        await handleNewMessage({ id: res,message: res, isAIGenerated: true, createdAt: new Date() });
      }
      setInput('');
    } catch (e) {
      const error = e as Error;
      showToast(toast, "error", "Error", error?.message);
      setLoading(false);
    }
  };

  return (
    <form className="chat-input-container" onSubmit={handleSubmit}>
      <input
        type="text"
        className="chat-input-field"
        value={input}
        onChange={(e) => setInput(e.target.value)}
        placeholder="Type your message..."
      />
      <Button type="submit" label="Send" className="chat-input-button" />
      <Toast ref={toast} />
    </form>
  );
};
