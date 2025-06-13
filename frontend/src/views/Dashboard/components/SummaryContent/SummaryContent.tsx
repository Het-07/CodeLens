import { IMessage } from '../../Dashboard.interface';
import './SummaryContent.css';

export const SummaryContent = ({ messages, loading }: { messages: IMessage[], loading: boolean }) => {
  return (
    <div className="chat-container">
      {messages.length > 0 ? (
        messages.map((msg) => (
          <div
            key={msg.id}
            className={`chat-message ${msg.isAIGenerated ? "ai-message" : "user-message"}`}
          >
            <p className="message-text">{msg.message}</p>
            <span className="message-time">{new Date(msg.createdAt).toLocaleTimeString()}</span>
          </div>
        ))
      ) : (
        <p className="placeholder-text">Start a conversation to see messages here.</p>
      )}
      
       {loading && (
        <div className='chat-message ai-message'>
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
