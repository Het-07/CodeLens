import { Card } from "primereact/card";
import "./CustomCard.css";

export const CustomCard = (props: any) => {
  return (
    <Card title={props.title} className={`custom-card ${props.className}`}>
      {props.content}
    </Card>
  );
};
