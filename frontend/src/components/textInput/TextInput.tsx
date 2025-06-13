import { InputText } from "primereact/inputtext";
import './TextInput.css'
export const TextInput = (props : any) => {
    return (
        <div>
            <InputText
                type = {props.type}
                value={props.value}
                onChange={props.onChange}
                placeholder={props.placeholder}
                className= "custom-input"
            />
        </div>
    )
}