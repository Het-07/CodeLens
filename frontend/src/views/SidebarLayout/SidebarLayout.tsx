import { Link, Outlet, useNavigate } from "react-router-dom";
import { useFetchUserSessions } from "../Dashboard/hooks/useFetchUserSessions";
import { ISession } from "./SidebarLayout.interface";
import { Session } from "./components/Session/Session";
import { Button } from "primereact/button";
import { Toast } from "primereact/toast";
import { showToast } from "../../utils/ToastUtil";
import { createSession } from "./SidebarLayout.api";
import {
  DEFAULT_SESSION,
  DEFAULT_SESSION_NAME,
} from "./SidebarLayout.constants";
import { useState } from "react";
import "./SidebarLayout.css";
import { useAppSelector } from "../../redux/hooks";
import {
  sessionState,
  setCurrentSession,
} from "../ChatSessionPage/ChatSessionPage.slice";
import { ProfilePopover } from "./components/ProfilePopOver/ProfilePopOver";
import { setAllSessions } from "../ChatSessionPage/ChatSessionPage.slice";
import { useDispatch } from "react-redux";
import { TextInput } from "../../components/textInput/TextInput";

export const SidebarLayout = () => {
  const userId = localStorage.getItem("userId") || "";
  const { sessions, toast, setSessions } = useFetchUserSessions(userId);
  const [selectedSession, setSelectedSession] = useState<ISession>(
    sessions.length > 0 ? sessions[0] : DEFAULT_SESSION
  );
  const { allSessions } = useAppSelector(sessionState);
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const [searchTerm, setSearchTerm] = useState("");

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(e.target.value);
  };

  const handleTabClick = async (session: ISession) => {
    setSelectedSession(session);
    dispatch(setCurrentSession({ session }));
  };

  const handleNewChat = async () => {
    try {
      const newSession = await createSession({
        userId: localStorage.getItem("userId") || "",
        sessionName: DEFAULT_SESSION_NAME,
      });

      if (newSession) {
        setSessions((prev) => {
          return [newSession, ...prev];
        });
        await dispatch(
          setAllSessions({ allSessions: [newSession, ...sessions] })
        );
      }
      navigate(`/session/${newSession.id}`);
    } catch (e) {
      const error = e as Error;
      showToast(toast, "error", "Error", error?.message);
    }
  };

  const onDeleteChat = async (sessionId: string) => {
    const filteredSessions = sessions.filter(
      (session) => session.id !== sessionId
    );
    setSessions(filteredSessions);
    await dispatch(setAllSessions({ allSessions: filteredSessions }));

    if (filteredSessions.length !== 0) {
      dispatch(setCurrentSession(filteredSessions[0]));
      navigate(`/session/${filteredSessions[0].id}`);
    } else {
      navigate("/");
    }
    handleTabClick(
      filteredSessions.length > 0 ? filteredSessions[0] : DEFAULT_SESSION
    );
  };

  const onSessionRename = async (sessionId: string, sessionName: string) => {
    const filteredSession = sessions.map((session) => {
      if (session.id === sessionId) {
        return {
          ...session,
          sessionName,
        };
      }
      return session;
    });
    await dispatch(setAllSessions({ allSessions: filteredSession }));
    setSessions(filteredSession);
  };

  const filteredSessions = allSessions.filter((session: ISession) =>
    session.sessionName.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="landing-page">
      <div className="history-sidebar">
        <Button className="new-chat-btn" onClick={handleNewChat}>
          <i className="pi pi-plus plus-icon" /> New Chat
        </Button>

        <div className="search-container">
          <p className="search-icon">
            <i className="pi pi-search" />
          </p>
          <div className="search-input">
            <TextInput
              type="text"
              value={searchTerm}
              onChange={handleSearchChange}
              placeholder="Search"
            />
          </div>
        </div>

        <h2>History</h2>

        <div className="sessions-list">
          {filteredSessions.length > 0 ? (
            filteredSessions.map((session: ISession) => (
              <Link key={session.id} to={`/session/${session.id}`}>
                <Session
                  key={session.id}
                  session={session}
                  selectedSession={selectedSession}
                  onTabClick={handleTabClick}
                  onDeleteChat={onDeleteChat}
                  onNewChatClick={handleNewChat}
                  onSessionRename={onSessionRename}
                />
              </Link>
            ))
          ) : (
            <div className="no-results">
              {searchTerm ? (
                <>No chats found matching "{searchTerm}"</>
              ) : (
                <>No chats available</>
              )}
            </div>
          )}
        </div>
      </div>

      <div className="main-content">
        <div className="main-content-header">
          <div className="left-header">
            <h2 className="session-title-header">
              {selectedSession.sessionName}
            </h2>
          </div>
          <ProfilePopover />
        </div>

        <div className="main-content-body">
          <Outlet />
        </div>
      </div>

      <Toast ref={toast} />
    </div>
  );
};
