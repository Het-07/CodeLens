import { useRef } from "react";
import { Button } from "primereact/button";
import { OverlayPanel } from "primereact/overlaypanel";
import { useNavigate } from "react-router-dom";
import "./ProfilePopOver.css"; // Create this CSS file

export const ProfilePopover = () => {
  const targetRef = useRef<OverlayPanel>(null);
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem("userId");
    localStorage.removeItem("token");
    window.dispatchEvent(new Event("logout"));
    navigate("/");
  };

  return (
    <div className="profile-popover-container">
      <Button
        icon="pi pi-user"
        className="profile-button p-button-rounded p-button-text"
        onClick={(e) => targetRef.current?.toggle(e)}
      />
      <OverlayPanel ref={targetRef} className="profile-popover-content">
        <ul className="popover-list">
          <li onClick={handleLogout}>Logout</li>
        </ul>
      </OverlayPanel>
    </div>
  );
};
