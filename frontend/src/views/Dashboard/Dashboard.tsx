import { useState } from 'react';
import './Dashboard.css';
import { HistorySidebar } from './components/HistorySidebar/HistorySideBar';
import { ChatInput } from './components/ChatInput/ChatInput';
import { SummaryContent } from './components/SummaryContent/SummaryContent';
import { useFetchUserSessions } from './hooks/useFetchUserSessions';
import { Toast } from 'primereact/toast';
import { IMessage, ISession } from './Dashboard.interface';
import { DEFAULT_SESSION } from './components/HistorySidebar/HistorySideBar.constant';
import { getAllMessageBySessionId } from './Dashboard.api';
import { showToast } from '../../utils/ToastUtil';


export const Dashboard = () => {
  const userId = localStorage.getItem("userId") || "";
  const { sessions, toast, setSessions } = useFetchUserSessions(userId); 
  const [selectedSession, setSelectedSession] = useState<ISession>(sessions.length > 0 ? sessions[0] : DEFAULT_SESSION);
  const [messages, setMessages] = useState<IMessage[]>([]);
  const [loading, setLoading] = useState(false);

  const handleTabClick = async (id: string) => {
    const currentSession = sessions.find(session => session.id === id);
    setSelectedSession(currentSession || DEFAULT_SESSION);
    
    try{
      const messages = await getAllMessageBySessionId(userId, currentSession?.id || "");
      setMessages(messages);
    }catch(e) {
      const error = e as Error;
      showToast(toast, "error", "Error", error?.message);
    }
  };

  const handleNewChat = async (session: ISession) => {
    setSessions(prev => {
      return [
        session,
        ...prev
      ]
    });  
    setSelectedSession(session);
    setMessages([]);
  }

  const handleNewMessage = async (message: IMessage) => {
    setMessages(prev => {
      return [
        ...prev,
        message
      ]
    })
  }

  const onDeleteChat = async (sessionId: string) => {
    const filteredSessions = sessions.filter(session => session.id !== sessionId);
    setSessions(filteredSessions);
    handleTabClick(filteredSessions.length > 0 ? filteredSessions[0].id : "")
  }

  const onSessionRename = async (sessionId: string, sessionName: string) => {
    const filteredSession = sessions.map(session => {
      if(session.id === sessionId) {
        return {
          ...session,
          sessionName
        }
      }
      return session;
    });

    setSessions(filteredSession);
    setSelectedSession(sessions.filter(session => session.id === sessionId)[0]);
  }

  return (
  <div className="landing-page">
    <HistorySidebar 
      sessions={sessions} 
      onTabClick={handleTabClick} 
      onNewChatClick={handleNewChat} 
      selectedSession={selectedSession}
      onDeleteChat={onDeleteChat}
      onSessionRename={onSessionRename}
    />
    <div className="main-content">
      <SummaryContent messages={messages} loading={loading}/>
      <ChatInput selectedSession={selectedSession} handleNewMessage={handleNewMessage} setLoading={setLoading}/>
    </div>
    <Toast ref={toast} />
  </div>
  );
};