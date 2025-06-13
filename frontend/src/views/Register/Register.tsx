import React, { useRef } from "react";
import { useNavigate } from "react-router-dom";
import "./Register.css";
import { TextInput } from "../../components/textInput/TextInput";
import { CustomButton } from "../../components/button/CustomButton";
import developer from "/svg/web-developer.svg";
import {
  validateEmail,
  validatePassword,
  validateConfirmPassword,
  validateFirstName,
  validateLastName,
} from "../../utils/validationUtils";
import logo from "../../assets/file.svg";
import { registrationApi } from "./Register.api";
import { Toast } from "primereact/toast";
import { showToast } from "../../utils/ToastUtil";

export const Register = () => {
  const [firstName, setFirstName] = React.useState("");
  const [lastName, setLastName] = React.useState("");
  const [email, setEmail] = React.useState("");
  const [password, setPassword] = React.useState("");
  const [confirmPassword, setConfirmPassword] = React.useState("");
  const [errors, setErrors] = React.useState({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    confirmPassword: "",
  });
  const navigate = useNavigate();
  const toast = useRef(null);

  const validateForm = () => {
    const newError = {
      firstName: "",
      lastName: "",
      email: "",
      password: "",
      confirmPassword: "",
    };

    newError.firstName = validateFirstName(firstName);
    newError.lastName = validateLastName(lastName);
    newError.email = validateEmail(email);
    newError.password = validatePassword(password);
    newError.confirmPassword = validateConfirmPassword(
      password,
      confirmPassword
    );

    setErrors(newError);

    return (
      !newError.firstName &&
      !newError.lastName &&
      !newError.email &&
      !newError.password &&
      !newError.confirmPassword
    );
  };

  const handleSubmit = async (e: React.FormEvent) => {
    try {
      e.preventDefault();
      if (validateForm()) {
        const user = await registrationApi({
          first_name: firstName,
          last_name: lastName,
          email,
          password,
        });
        showToast(toast, "success", "Success", user.body.message);
        navigate("/");
      }
    } catch (e) {
      const error = e as Error;
      showToast(toast, "error", "Error", error.message);
    }
  };

  return (
    <div className="registration-body row">
      <div className="logo-container">
        <div className="logo-register">
          <img src={logo} alt="CodeLens Logo" className="app-logo" />
        </div>
        <div className="description-container">
          <p className="description">
            Code documentation made <br />
            simple and efficient.
          </p>
        </div>
        <div className="svg-container">
          <img src={developer}></img>
        </div>
      </div>

      <form onSubmit={handleSubmit} className="form-container col-6">
        <div className="input-container">
          <div>
            <h2>Create Account</h2>
          </div>

          <div className="input-row">
            {/* First Name */}
            <div className="input-group">
              <label>First Name</label>
              <TextInput
                type="text"
                value={firstName}
                onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                  setFirstName(e.target.value);
                }}
              />
              {errors.firstName && (
                <small className="error-text">{errors.firstName}</small>
              )}
            </div>

            {/* Last Name */}
            <div className="input-group">
              <label>Last Name</label>
              <TextInput
                type="text"
                value={lastName}
                onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                  setLastName(e.target.value);
                }}
              />
              {errors.lastName && (
                <small className="error-text">{errors.lastName}</small>
              )}
            </div>
          </div>

          <div className="input-group">
            <label>Email</label>
            <TextInput
              type="text"
              value={email}
              onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                setEmail(e.target.value);
              }}
            />
            {errors.email && (
              <small className="error-text">{errors.email}</small>
            )}
          </div>

          <div className="input-group">
            <label>Password</label>
            <TextInput
              type="password"
              value={password}
              onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                setPassword(e.target.value);
              }}
            />
            {errors.password && (
              <small className="error-text">{errors.password}</small>
            )}
          </div>

          <div className="input-group">
            <label>Confirm Password</label>
            <TextInput
              type="password"
              value={confirmPassword}
              onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                setConfirmPassword(e.target.value);
              }}
            />
            {errors.confirmPassword && (
              <small className="error-text">{errors.confirmPassword}</small>
            )}
          </div>

          <div className="input-group">
            <CustomButton label="Register" />
          </div>

          <div className="signin-container">
            <small className="signin-text">
              Already have an account?{" "}
              <span
                className="signin-link"
                style={{
                  fontFamily: "Poppins-SemiBold",
                  cursor: "pointer",
                  color: "#176B87",
                }}
                onClick={() => navigate("/")}
              >
                Sign in
              </span>
            </small>
          </div>
        </div>
      </form>

      <Toast ref={toast} />
    </div>
  );
};
