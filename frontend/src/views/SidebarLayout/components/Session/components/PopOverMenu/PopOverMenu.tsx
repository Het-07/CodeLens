import { OverlayPanel } from "primereact/overlaypanel";
import { Button } from "primereact/button";
import { useRef } from "react";
import "primeicons/primeicons.css";
import "./PopOverMenu.css";

interface PopoverMenuProps {
  onRename: () => void;
  onDelete: () => void;
}

export const PopoverMenu = ({ onRename, onDelete }: PopoverMenuProps) => {
  const menuRef = useRef<OverlayPanel>(null);

  const toggleMenu = (event: React.MouseEvent) => {
    menuRef.current?.toggle(event);
  };

  return (
    <div className="popover-container">
      <Button icon="pi pi-ellipsis-h" className="ellipsis-button" onClick={toggleMenu} />
      
      <OverlayPanel ref={menuRef}>
        <div className="menu-item" onClick={onRename}>
          <i className="pi pi-pencil" /> Rename
        </div>
        <div className="menu-item" onClick={onDelete}>
          <i className="pi pi-trash" /> Delete
        </div>
      </OverlayPanel>
    </div>
  );
};
