import { Button } from 'primereact/button';
import { Tooltip } from 'primereact/tooltip';
import './ToggleModel.css';
import { useDispatch } from 'react-redux';
import { setState } from '../../../ChatSessionPage.slice';

interface ModelToggleProps {
  onToggle: (isCodeModelEnabled: boolean) => void;
  isCodeModelEnabled: boolean;
}

export const ToggleModel = ({ onToggle, isCodeModelEnabled }: ModelToggleProps) => {

    const dispatch = useDispatch();

  const toggleModel = () => {
    onToggle(!isCodeModelEnabled);
    dispatch(setState({ key: "isCodeModeEnabled", value: !isCodeModelEnabled }));
  };

  return (
    <>
    <Tooltip target=".code-model-toggle" position="top" />
    <Button 
      className={`code-model-toggle ${isCodeModelEnabled ? 'enabled' : 'disabled'}`}
      onClick={toggleModel}
      data-pr-tooltip={isCodeModelEnabled ? "Code Model Enabled" : "Code Model Disabled"}
      icon={isCodeModelEnabled ? "pi pi-code" : "pi pi-code"}
      aria-label="Toggle Code Model"
      type="button" // Important: Set type to button to prevent form submission
    />
  </>
  );
};