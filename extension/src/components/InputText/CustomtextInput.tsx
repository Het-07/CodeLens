import React from 'react'
import { InputText } from "primereact/inputtext";
import './CustomTextInput.css'
import {InputTextProps} from './CustomTextInput.interface'

export const CustomTextInput:React.FC<InputTextProps> = ({type,value,onChange,placeholder,name}) => {
    return (
        <div>
            <InputText
                placeholder= {placeholder}
                type = {type}
                value={value}
                onChange={onChange}
                className= "custom-input"
                name={name}
            />
        </div>
    )
}
