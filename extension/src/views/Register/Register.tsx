import React from 'react'
import { useState } from 'react';
import './Register.css';
import { CustomTextInput } from '../../components/InputText/CustomtextInput';
import { CustomButton } from '../../components/Button/Button';
import { IErrorState, IUser } from './Register.interface';
import { validateConfirmPassword, validateEmail, validateName, validatePassword } from '../../utils/validationUtils';
import { createUser } from './Register.api';  
import Logo from "../../static/svg/file.svg";


export const Register = ({ onLogin }) => {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [errors, setErrors] = useState<IErrorState>({
      firstName: "",
      lastName: "",
      email: "",
      password: "",
      confirmPassword: ""
    })
  
    const handleSubmit = async (e) => {
      e.preventDefault();
      
      if(validateForm()) {
        try {
          const payload: IUser = {
            first_name: firstName,
            last_name: lastName,
            email,
            password
          };
  
          const registeredUser = await createUser(payload);
  
          if(registeredUser) {
            onLogin(true);
            alert('User registered successfully');
          }
        } catch (error) {
          alert('Error while creating user');
        }
      }
    };

    const validateForm = () => {

      const newError = { firstName: "", lastName: "", email: "", password: "", confirmPassword: "" }

      newError.firstName = validateName(firstName, 'First name'),
      newError.lastName = validateName(lastName, 'Last name'),
      newError.email = validateEmail(email),
      newError.password = validatePassword(password),
      newError.confirmPassword = validateConfirmPassword(password, confirmPassword)

      setErrors(newError);

      return !newError.firstName.length && !newError.email.length && !newError.password.length && !newError.lastName.length && !newError.confirmPassword.length;
    } 

    const onChange = (e) => {
      e.preventDefault();
  
      switch(e.target.name) {
        case "email":
          setEmail(e.target.value);
          break;
        case "password":
          setPassword(e.target.value);
          break;
        case "firstName":
          setFirstName(e.target.value);
          break;
        case "lastName":
          setLastName(e.target.value);
          break;
        case "confirmPassword":
          setConfirmPassword(e.target.value);
          break;
      }
    }
  
    return (
      <div className="register-container">
        <form onSubmit={handleSubmit} className='register-form'>

        <div className="logo">
          <img src={Logo} alt="App Logo" className="app-logo" />
        </div>

          <h2>Register</h2>

          <div className="form-group">
            <label>First Name</label>
            <CustomTextInput type='input' value={firstName} name='firstName' onChange={onChange} />
            {errors.firstName && <small className="error-text">{errors.firstName}</small>}
          </div>
  
          <div className="form-group">
            <label>Last Name</label>
            <CustomTextInput type='input' value={lastName} name='lastName' onChange={onChange} />
            {errors.lastName && <small className="error-text">{errors.lastName}</small>}
          </div>

          <div className="form-group">
            <label>Email</label>
            <CustomTextInput type='input' value={email} name='email' onChange={onChange} />
            {errors.email && <small className="error-text">{errors.email}</small>}
          </div>
  
          <div className="form-group">
            <label>Password</label>
            <CustomTextInput type='password' value={password} name='password' onChange={onChange} />
            {errors.password && <small className="error-text">{errors.password}</small>}
          </div>

          <div className="form-group">
            <label>Confirm Password</label>
            <CustomTextInput type='password' value={confirmPassword} name='confirmPassword' onChange={onChange} />
            {errors.confirmPassword && <small className="error-text">{errors.confirmPassword}</small>}
          </div>
  
          <CustomButton label="Register"/>

           {/* <div className="divider">OR</div>

           <div className="google-container">
            <img className="google-icon" src='svg/google.svg'></img>
            <p className="google-text">Sign up with Google</p>
          </div>
 */}

          <div className="login-link">
            Already have an account? <span onClick={() => onLogin(true)}>Log in</span>
          </div> 
        </form>
      </div>
    );
}
