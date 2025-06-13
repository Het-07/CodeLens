import { Card } from "primereact/card";
import "./HistorySideBar.css";
import { ISession } from "../../Dashboard.interface";
import { Button } from "primereact/button";
import "primeicons/primeicons.css";
import { showToast } from "../../../../utils/ToastUtil";
import {
  createSession,
  deleteSession,
  renameSession,
} from "../../Dashboard.api";
import { DEFAULT_SESSION_NAME } from "./HistorySideBar.constant";
import { useRef, useState } from "react";
import { Toast } from "primereact/toast";
import { PopoverMenu } from "./components/PopOverMenu/PopOverMenu";

export interface HistorySideBarProp {
  sessions: ISession[];
  onTabClick: (id: string) => Promise<void>;
  onNewChatClick: (session: ISession) => Promise<void>;
  selectedSession: ISession;
  onDeleteChat: (sessionId: string) => Promise<void>;
  onSessionRename: (sessionId: string, sessionName: string) => Promise<void>;
}

export const HistorySidebar = ({
  sessions,
  onTabClick,
  onNewChatClick,
  selectedSession,
  onDeleteChat,
  onSessionRename,
}: HistorySideBarProp) => {
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
        await renameSession({
          userId,
          sessionId,
          sessionName: tempName.trim(),
        });
      }
      setEditingSessionId("");
      onSessionRename(sessionId, tempName);
    } catch (e) {
      const error = e as Error;
      showToast(toast, "error", "Error", error?.message);
    }
  };

  const handleNewChat = async () => {
    try {
      const newSession = await createSession({
        userId: localStorage.getItem("userId") || "",
        sessionName: DEFAULT_SESSION_NAME,
      });

      if (newSession) {
        onNewChatClick(newSession);
      }
    } catch (e) {
      const error = e as Error;
      showToast(toast, "error", "Error", error?.message);
    }
  };

  const handleDelete = async () => {
    try {
      const userId = localStorage.getItem("userId") || "";
      const response = await deleteSession(userId, selectedSession.id);

      if (response) onDeleteChat(selectedSession.id);
    } catch (e) {
      const error = e as Error;
      showToast(toast, "error", "Error", error?.message);
    }
  };

  return (
    <div className="history-sidebar">
      <Button className="new-chat-btn" onClick={handleNewChat}>
        <i className="pi pi-plus plus-icon" />
      </Button>

      <h2 style={{ color: "var(--accent)" }}>History</h2>
      {sessions.map((session: ISession) => (
        <Card
          key={session.id}
          className={`history-tab ${
            selectedSession && selectedSession.id === session.id
              ? "active-history-tab"
              : ""
          }`}
          onClick={() => onTabClick(session.id)}
        >
          <div className="tab-header">
            {editingSessionId == session.id ? (
              <input
                type="text"
                className="session-name-input"
                value={tempName}
                onChange={handleInputChange}
                onBlur={() => handleBlurOrEnter(session.id)}
                onKeyDown={(e) =>
                  e.key === "Enter" && handleBlurOrEnter(session.id)
                }
                autoFocus
              />
            ) : (
              <h2>{session.sessionName}</h2>
            )}
            <div className="ellipsis-container">
              <PopoverMenu
                onRename={() =>
                  handleRenameClick(session.id, session.sessionName)
                }
                onDelete={handleDelete}
              />
            </div>
          </div>
          <p className="date">
            {new Date(session.createdAt).toLocaleDateString()}
          </p>
        </Card>
      ))}
      <Toast ref={toast} />
    </div>
  );
};
