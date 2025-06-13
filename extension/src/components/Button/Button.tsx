import React from "react"
import { Button } from "primereact/button"
import "./Button.css"
import { ButtonProps } from "./Button.interface"


export const CustomButton: React.FC<ButtonProps> = ({label})=> {
return(
    <Button label= {label} className="custom-button"/>
)
}
