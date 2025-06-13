import { useEffect, useRef, useState } from "react";
import "./Login.css";
import { TextInput } from "../../components/textInput/TextInput";
import { CustomButton } from "../../components/button/CustomButton";
import { useNavigate } from "react-router-dom";
import developer from "/svg/web-developer.svg";
import { validateEmail, validatePassword } from "../../utils/validationUtils";
import logo from "../../assets/file.svg";
import { Toast } from "primereact/toast";
import { showToast } from "../../utils/ToastUtil";
import { useAppDispatch, useAppSelector } from "../../redux/hooks";
import { authState, getUserInfo } from "./Login.slice";

export const Login = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [errors, setErrors] = useState({ email: "", password: "" });
  const [rememberMe, setRememberMe] = useState(false);
  const navigate = useNavigate();
  const toast = useRef(null);
  const dispatch = useAppDispatch();
  const { message, isError, userId, isSuccess } = useAppSelector(authState);

  const validateForm = () => {
    const newErrors = { email: "", password: "" };

    newErrors.email = validateEmail(email);
    newErrors.password = validatePassword(password);

    setErrors(newErrors);
    return !newErrors.email && !newErrors.password;
  };

  useEffect(() => {
    if (!isError && userId && email !== "") {
      showToast(toast, "success", "Success", message);

      window.dispatchEvent(new Event("login"));
      navigate("/");
    }
    if (isError && email !== "") {
      showToast(toast, "error", "Error", message);
    }
  }, [dispatch, isError, isSuccess, message, navigate, userId]);

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    try {
      e.preventDefault();
      if (validateForm()) {
        dispatch(getUserInfo({ email, password }));
      }
    } catch (e) {
      const error = e as Error;
      showToast(toast, "error", "Error", error.message);
    }
  };

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const handleForgotPassword = (e: any) => {
    e.preventDefault();
    navigate("/forgot-password");
  };

  return (
    <div className="login-body">
      <div className="left-container">
        <div className="logo-login">
          <img src={logo} alt="CodeLens Logo" className="app-logo" />
        </div>
        <div className="description-container">
          <p className="description">
            Code documentation made <br />
            simple and efficient.
          </p>
        </div>
        <div className="svg-container">
          <img src={developer} alt="Developer" />
        </div>
      </div>

      <form onSubmit={handleSubmit} className="right-container">
        <div className="wrapper-container">
          <h2>Sign In</h2>

          <div className="input-content">
            <label>Email</label>
            <TextInput
              type="text"
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
            <label>Password</label>
            <TextInput
              type="password"
              value={password}
              onChange={(e: React.ChangeEvent<HTMLInputElement>) =>
                setPassword(e.target.value)
              }
            />
            {errors.password && (
              <small className="error-text">{errors.password}</small>
            )}
          </div>

          <div className="divider">
            <div className="remember-me">
              <input
                type="checkbox"
                checked={rememberMe}
                onChange={(e: React.ChangeEvent<HTMLInputElement>) =>
                  setRememberMe(e.target.checked)
                }
              />
              <label>Remember Me</label>
            </div>

            <div className="forgot-password">
              <a onClick={handleForgotPassword}>Forgot Password?</a>
            </div>
          </div>

          <div className="input-content">
            <CustomButton label="Sign In" disabled={handleSubmit} />
          </div>

          <div className="login-container">
            <small className="login-text">
              Don't have an account?{" "}
              <span
                className="register-link"
                style={{
                  fontFamily: "Poppins-SemiBold",
                  cursor: "pointer",
                }}
                onClick={() => navigate("/register")}
              >
                Register
              </span>
            </small>
          </div>
        </div>
      </form>

      <Toast ref={toast} />
    </div>
  );
};
