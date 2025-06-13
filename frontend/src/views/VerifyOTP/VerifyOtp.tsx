import { useRef, useState } from "react";
import { CustomCard } from "../../components/card/CustomCard";
import { TextInput } from "../../components/textInput/TextInput";
import { CustomButton } from "../../components/button/CustomButton";
import logo from "../../assets/file.svg";
import "./VerifyOtp.css";
import { useLocation, useNavigate } from "react-router-dom";
import { resendOTP, verifyOtp } from "./VerifyOtp.api";
import { showToast } from "../../utils/ToastUtil";
import { Toast } from "primereact/toast";
import { storeKey } from "../../utils/utils.function";

export const VerifyOtp = () => {
  const [otp, setOtp] = useState("");
  const [error, setError] = useState("");
  const state = useLocation();
  const navigate = useNavigate();
  const email = state.state.email;
  const toast = useRef(null);
  

  const handleVerifyError = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setOtp(value);
    if (/^\d{6}$/.test(value)) {
      setError("");
    } else {
      setError("OTP must be a 6-digit numeric code");
    }
  };

  const handleVerify = async () => {
    try {
      if(!error.length) {
        const res = await verifyOtp({ email, otp });
        if(res) {
          storeKey("token", res.body.token);
          navigate('/reset-password', { state: { email } })
        }
      }
    } catch (e) {
      const error = e as Error;
      showToast(toast, "error","Error", error.message);
    }
  };

  const handleResendOTP = async () => {
    try {
      const res = await resendOTP({ email });

      showToast(toast, "success", "Success", res?.body?.message);
    } catch (e) {
      const error = e as Error;
      showToast(toast, "error", "Error", error.message);
    } 
  }

  return (
    <div className="otp-verify-container">
      <div className="logo-otp">
        <img src={logo} alt="CodeLens Logo" />
      </div>

      <div className="custom-card-container">
        <CustomCard
          title="OTP Verification"
          content={
            <div className="otp-container">
              <p>Please enter the OTP sent to your registered email.</p>

              <div className="otp-wrapper">
                <TextInput
                  value={otp}
                  className="otp-input"
                  onChange={handleVerifyError}
                  maxLength={6}
                  placeholder="Enter OTP"
                />
                {error && <p className="error-text">{error}</p>}

                <div className="reset-otp">
                  <a onClick={handleResendOTP}>Resend OTP</a>
                </div>

                <CustomButton
                  label="Verify OTP"
                  className="otp-verify-btn"
                  onClick={handleVerify}
                />

              </div>
            </div>
          }
        />
      </div>

      <Toast ref={toast} />
    </div>
  );
};
