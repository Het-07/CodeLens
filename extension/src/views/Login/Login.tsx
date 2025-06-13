import React, { useState } from "react";
import "./Login.css"; // External CSS file
import { CustomButton } from "../../components/Button/Button"; // Importing CustomButton component from Button.tsx
import { CustomTextInput } from "../../components/InputText/CustomtextInput";
import { ErrorState, LogUsers } from "./Login.interface";
import { validateEmail, validatePassword } from "../../utils/validationUtils";
import { CONFIG } from "../../config/dev.config";
import { LoginUser } from "./Login.api";
import Logo from "../../static/svg/file.svg"; // Ensure the path is correct
import { setToLocalStorage } from "../../utils/utils.function";

const LoginPage = ({ onRegister }) => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [errors, setErrors] = useState<ErrorState>({
    email: "",
    password: "",
  });

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (validateForm()) {
      try {
        const payload: LogUsers = {
          email,
          password,
        };

        const loggedUser = await LoginUser(payload);

        if (loggedUser) {
          const userDetails = loggedUser.body;
          alert("User logged in successfully");
          await setToLocalStorage({ token: userDetails.token, userId: userDetails.user_details.userId, isLoggedIn: true });
          window.close();
        } else {
          alert("Invalid credentials");
        }
      } catch (error) {
        console.error("Login error:", error);
        alert("Invalid credentials.");
      }
    }
  };

  const validateForm = () => {
    const newError = { email: "", password: "" };

    newError.email = validateEmail(email);
    newError.password = validatePassword(password);

    setErrors(newError);

    return !newError.email.length && !newError.password.length;
  };

  const onChange = (e) => {
    e.preventDefault();

    switch (e.target.name) {
      case "email":
        setEmail(e.target.value);
        break;
      case "password":
        setPassword(e.target.value);
        break;
    }
  };

  return (
    <div className="login-container">
      <form onSubmit={handleSubmit} className="login-form">
        <div className="logo">
          <img src={Logo} alt="App Logo" className="app-logo" />
        </div>

        <h2>Login</h2>
        <div className="form-group">
          <label>Email</label>
          <CustomTextInput
            name="email"
            type="input"
            value={email}
            onChange={onChange}
          />
          {errors.email && <small className="error-text">{errors.email}</small>}
        </div>

        <div className="form-group">
          <label>Password</label>
          <CustomTextInput
            type="password"
            name="password"
            value={password}
            onChange={onChange}
          />
          {errors.password && (
            <small className="error-text">{errors.password}</small>
          )}
        </div>

        <CustomButton label="Login" />
        <div className="signup-link">
          <p>
            Don't have an account?{" "}
            <span onClick={() => onRegister(false)}>Sign up</span>
          </p>
        </div>
        {/* <div className="divider">OR</div>

        <div className="google-container">
          <img className="google-icon" src="svg/google.svg"></img>
          <p className="google-text">Login with Google</p>
        </div> */}
        <div className="forgot-password">
          <a
            href={`${CONFIG.FRONTEND_URL}/forgot-password`}
            target="_blank"
            rel="noopener noreferrer"
          >
            <span>Forgot password</span>{" "}
          </a>
        </div>
      </form>
    </div>
  );
};

export default LoginPage;
