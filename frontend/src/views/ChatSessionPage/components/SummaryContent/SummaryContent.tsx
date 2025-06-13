import React, { useRef } from "react";
import "./SummaryContent.css";
import { getDocxFile } from "../../ChatSessionPage.api";
import { ProgressSpinner } from "primereact/progressspinner";
import axiosInstance from "../../../../api/axiosInstance";
import { useAppSelector } from "../../../../redux/hooks";
import { IMessage } from "../../ChatSessionPage.interface";
import { sessionState } from "../../ChatSessionPage.slice";
import { Toast } from "primereact/toast";
import { Tooltip } from "primereact/tooltip";
import { CopyToClipboard } from "react-copy-to-clipboard";
import "./SummaryContent.css";
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';


export const SummaryContent = ({
  messages,
  sessionId,
}: {
  messages: IMessage[];
  sessionId: string;
}) => {
  const { sessionLoadingStates } = useAppSelector(sessionState);
  const { selectedSession } = useAppSelector(sessionState);
  const [loadingStates, setLoadingStates] = React.useState<
    Record<string, boolean>
  >({});
  const [copiedMsgID, setCopiedMsgID] = React.useState<string | null>(null);

  async function handleDownload(msgId: string, sessionName: string) {
    try {
      setLoadingStates((prev) => ({ ...prev, [msgId]: true }));

      await getDocxFile(msgId, sessionName);

      setLoadingStates((prev) => ({ ...prev, [msgId]: false }));
    } catch (error) {
      console.log(error, "error");
      toastRef.current?.show({
        severity: "error",
        detail: "Error while downloading document",
      });
      setLoadingStates((prev) => ({ ...prev, [msgId]: false }));
    }
  }
  const toastRef = useRef<Toast>(null);

  // Function to generate shareable link
  const generateLink = async (messageID: string) => {
    try {
      const response = await axiosInstance.post(
        `/link/generate?documentId=${messageID}`
      );
      const link = await response.data.link;

      toastRef.current?.show({
        severity: "success",
        summary: "Link Copied!",
        detail: link,
        life: 3000,
      });

      navigator.clipboard.writeText(link);
    } catch (error) {
      console.error("Error generating shareable link:", error);
      toastRef.current?.show({
        severity: "error",
        summary: "Error",
        detail: "Failed to generate link",
        life: 3000,
      });
    }
  };

  return (
    <div className="chat-container">
      <Toast ref={toastRef} />

      {messages.length > 0 ? (
        messages.map((msg) => (
          <div
            key={msg.id}
            className={`chat-message ${
              msg.isAIGenerated ? "ai-message" : "user-message"
            }`}
          >
            <div className="formatted-message">
              <ReactMarkdown
                remarkPlugins={[remarkGfm]}
                components={{
                  code({ className, children, ...props }) {
                    // Check if the code block has a language specified (fenced code block)
                    const match = /language-(\w+)/.exec(className || '');
                    if (match) {
                      return (
                        <div className="code-box">
                          <pre
                            className={className}
                            style={{ width: '100%' }}
                            {...(props as React.HTMLAttributes<HTMLPreElement>)}
                          >
                          <code>{children}</code>
                        </pre>
                        </div>
                      );
                    }
                    // Otherwise, render inline code
                    return (
                      <code className="inline-code" {...props}>
                        {children}
                      </code>
                    );
                  },
                }}
              >
                {msg.message}
              </ReactMarkdown>
            </div>
            <div className="icon-time-container">
              {msg.isAIGenerated && (
                <div className="icons-container">
                  <Tooltip
                    target=".download-icon"
                    content="Download"
                    position="bottom"
                  />

                  <div>
                    {loadingStates[msg.id] ? (
                      <ProgressSpinner className="progress-spinner" />
                    ) : (
                      <i
                        className={"pi pi-download download-icon"}
                        onClick={() => {
                          handleDownload(msg.id, selectedSession.sessionName);
                        }}
                      />
                    )}
                  </div>

                  <Tooltip
                    target=".link-icon"
                    content="Link"
                    position="bottom"
                  />
                  <i
                    className="pi pi-link link-icon"
                    onClick={() => generateLink(msg.id)}
                  />
                  <Tooltip
                    target=".copy-icon"
                    content="Copy"
                    position="bottom"
                  />
                  <CopyToClipboard
                    text={msg.message}
                    onCopy={() => {
                      setCopiedMsgID(msg.id);
                      toastRef.current?.show({
                        severity: "success",
                        summary: "Copied",
                        detail: "Copied to clipboard",
                        life: 2000,
                      });
                    }}
                  >
                    <i
                      className={`pi pi-copy copy-icon ${
                        copiedMsgID === msg.id ? "copied" : ""
                      }`}
                    />
                  </CopyToClipboard>
                </div>
              )}

              <div>
                <p className="message-time">
                  {new Date(msg.createdAt).toLocaleTimeString()}
                </p>
              </div>
            </div>
          </div>
        ))
      ) : (
        <div className="greeting">
          <h1 className="placeholder-text">
            Welcome to Codelens! How can I help you today?
          </h1>
        </div>
      )}

      {sessionLoadingStates.get(sessionId) && (
        <div className="chat-message ai-message">
          <div className="loading-container">
            <span className="dot one">.</span>
            <span className="dot two">.</span>
            <span className="dot three">.</span>
          </div>
        </div>
      )}
    </div>
  );
};
