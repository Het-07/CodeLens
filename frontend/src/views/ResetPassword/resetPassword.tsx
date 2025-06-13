import { useRef, useState } from "react";
import { CustomCard } from "../../components/card/CustomCard";
import { TextInput } from "../../components/textInput/TextInput";
import { CustomButton } from "../../components/button/CustomButton";
import {
  validatePassword,
  validateConfirmPassword,
} from "../../utils/validationUtils";
import logo from "../../assets/file.svg";
import "./resetPassword.css";
import { useLocation, useNavigate } from "react-router-dom";
import { resetPassword } from "./ResetPassword.api";
import { getKey } from "../../utils/utils.function";
import { Toast } from "primereact/toast";
import { showToast } from "../../utils/ToastUtil";

export const ResetPassword = () => {
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError] = useState("");
  const state = useLocation();
  const email = state.state.email;
  const toast = useRef(null);
  const navigate = useNavigate();
  

  const handlePasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    const validationError = validatePassword(value);
    setPassword(value);
    setError(validationError);
  };

  const handleConfirmPasswordChange = (
    e: React.ChangeEvent<HTMLInputElement>
  ) => {
    const value = e.target.value;
    const validationError = validateConfirmPassword(password, value);
    setConfirmPassword(value);
    setError(validationError);
  };

  const handleVerify = async () => {

    try {
      if (!error && password && confirmPassword) {
        
        await resetPassword({ email, password, token: getKey("token") || "" })
        navigate('/')
      }
    } catch (e) {
      const error = e as Error;
      showToast(toast, "error", "Error", error.message);
    }
  };

  return (
    <div className="verify-password-container">
      <div className="logo-password">
        <img src={logo} alt="CodeLens Logo" />
      </div>

      <div className="custom-card-container">
        <CustomCard
          title="Reset Password"
          content={
            <div className="password-container">
              <div className="password-input">
                <TextInput
                  type="password"
                  value={password}
                  className="password-input"
                  onChange={handlePasswordChange}
                  placeholder="Enter new password"
                />
              </div>

              <div className="password-input">
                <TextInput
                  type="password"
                  value={confirmPassword}
                  onChange={handleConfirmPasswordChange}
                  placeholder="Confirm password"
                />
              </div>

              {error && <p className="error-text">{error}</p>}

              <CustomButton
                label="Submit"
                className="password-verify-btn"
                onClick={handleVerify}
              />
            </div>
          }
        />
      </div>

      <Toast ref={toast} />
    </div>
  );
};
