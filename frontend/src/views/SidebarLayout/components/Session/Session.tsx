import { Card } from 'primereact/card';
import "primeicons/primeicons.css";
import { showToast } from '../../../../utils/ToastUtil';
import { deleteSession, renameSession } from '../../SidebarLayout.api';
import { useRef, useState } from 'react';
import { PopoverMenu } from './components/PopOverMenu/PopOverMenu';
import { ISession } from '../../SidebarLayout.interface';

export interface HistorySideBarProp {
  session: ISession, 
  selectedSession: ISession
  onTabClick: (session: ISession) => Promise<void>, 
  onNewChatClick: (session: ISession) => Promise<void>,
  onDeleteChat: (sessionId: string) => Promise<void>,
  onSessionRename: (sessionId: string, sessionName: string) => Promise<void>
}


export const Session = ({ session, onTabClick, onDeleteChat, onSessionRename, selectedSession }: HistorySideBarProp) => {

  const toast = useRef(null);
  const [editingSessionId, setEditingSessionId] = useState("");
  const [tempName, setTempName] = useState<string>("");

  const handleRenameClick = (sessionId: string, sessionName: string) => {
    setEditingSessionId(sessionId);
    setTempName(sessionName);
  };

  // Handle input change
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setTempName(e.target.value);
  };

  // Handle input blur or enter press (Save Name)
  const handleBlurOrEnter = async (sessionId: string) => {
    try {
      if (tempName.trim() !== "") {
        const userId = localStorage.getItem("userId") || "";
        await renameSession({ userId, sessionId, sessionName: tempName.trim() });

      }
      setEditingSessionId("");
      onSessionRename(sessionId, tempName);
    } catch (e) { 
      const error = e as Error;
      showToast(toast, "error", "Error", error?.message); 
    }
  };
  

  const handleDelete = async () => {
    try {
      const userId = localStorage.getItem("userId") || "";
      const response = await deleteSession(userId, session.id); 
      
      if(response) onDeleteChat(session.id);
    } catch (e) {
      const error = e as Error;
      showToast(toast, "error", "Error", error?.message);
    }
  }

  return (
        <Card
          key={session.id}
          className={`history-tab ${selectedSession && selectedSession.id === session.id ? "active-history-tab" : ""}`}
          onClick={() => onTabClick(session)}
        >
         <div className="tab-header">
            {
              editingSessionId == session.id ? (
                <input
                type="text"
                className="session-name-input"
                value={tempName}
                onChange={handleInputChange}
                onBlur={() => handleBlurOrEnter(session.id)}
                onKeyDown={(e) => e.key === "Enter" && handleBlurOrEnter(session.id)}
                autoFocus
              />
              ) : (
                <h2>{session.sessionName}</h2>
              )
            }
            <div className="ellipsis-container">
              <PopoverMenu
                onRename={() => handleRenameClick(session.id, session.sessionName)}
                onDelete={handleDelete}
              />
            </div>
          </div>
          <p className="date">{new Date(session.createdAt).toLocaleDateString()}</p>
        </Card>
  );
};
