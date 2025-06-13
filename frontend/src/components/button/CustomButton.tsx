import { Button } from "primereact/button";
import "./CustomButton.css";

export const CustomButton = (props: any) => {
  return (
    <Button
      label={props.label}
      className={`custom-button ${props.className}`}
      onClick={props.onClick}
    />
  );
};
