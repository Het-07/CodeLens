import React, { useRef, useState } from "react";
import logo from "../../assets/file.svg";
import "./forgotPassword.css";
import { CustomCard } from "../../components/card/CustomCard";
import { TextInput } from "../../components/textInput/TextInput";
import { CustomButton } from "../../components/button/CustomButton";
import { validateEmail } from "../../utils/validationUtils";
import { sendForgotPasswordMail } from "./forgotPassword.api";
import { ProgressSpinner } from 'primereact/progressspinner';
import { useNavigate } from "react-router-dom";
import { showToast } from "../../utils/ToastUtil";
import { Toast } from "primereact/toast";
        

export const ForgotPassword = () => {
  const [email, setEmail] = React.useState("");
  const [errors, setErrors] = React.useState({ email: "" });
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const toast = useRef(null);
  

  const validateForm = () => {
    const newErrors = { email: "" };

    newErrors.email = validateEmail(email);

    setErrors(newErrors);
    return !newErrors.email;
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    try {
      e.preventDefault();
      if (validateForm()) {
        setLoading(true);
        const res = await sendForgotPasswordMail({ email });
        if(res) {
          navigate('/verify-otp', { state: {email} });
        }
      }
    } catch (e) {
      const error = e as Error;
      showToast(toast, "error", "Error", error?.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className=".forgotPassword-body ">
      <div className="logo-forgot-password">
        <img src={logo} alt="CodeLens"></img>
      </div>
      <div className="card-container">
        <CustomCard
          title= {"Forgot Password?"}
          content={
            !loading ? (
              <div className="email-container">
                <p>
                  Enter your email to receive a One-Time Password (OTP) for
                  resetting your password.
                </p>
                <form onSubmit={handleSubmit}>
                  <div className="input-content">
                    <TextInput
                      type="text"
                      placeholder="Email"
                      value={email}
                      onChange={(e: React.ChangeEvent<HTMLInputElement>) =>
                        setEmail(e.target.value)
                      }
                    />
                    {errors.email && (
                      <small className="error-text">{errors.email}</small>
                    )}
                  </div>

                  <div className="input-content">
                    <CustomButton label="Continue" />
                  </div>
                </form>
              </div>
            ) : (
              <div className="email-container">
                  <ProgressSpinner />
              </div>
            )
          }
        />
      </div>
      <Toast ref={toast} />
    </div>
  );
};
