import { BrowserRouter, Route, Routes } from "react-router-dom";
import { Login } from "../views/Login/Login";
import { Register } from "../views/Register/Register";
import { ForgotPassword } from "../views/ForgotPassword/forgotPassword";
import { VerifyOtp } from "../views/VerifyOTP/VerifyOtp";
import { ResetPassword } from "../views/ResetPassword/resetPassword";
import { useEffect, useState } from "react";
import { SidebarLayout } from "../views/SidebarLayout/SidebarLayout";
import { ChatSessionPage } from "../views/ChatSessionPage/ChatSessionPage";
import { DefaultSessionPage } from "../views/ChatSessionPage/DefaultSessionPage";

export const Router = () => {
const [isAuthenticated, setIsAuthenticated] = useState(false);

useEffect(() => {
  const checkAuth = () => {
    const token = localStorage.getItem("token");
    setIsAuthenticated(!!token);
  }

  checkAuth();

  window.addEventListener("storage", checkAuth);
  window.addEventListener("login", checkAuth);
  window.addEventListener("logout", checkAuth);

  return () => {
    window.removeEventListener("storage", checkAuth);
    window.removeEventListener("login", checkAuth);
  }
  
}, []);



  return (
      <>
        {!isAuthenticated ? (
          <BrowserRouter>
            <Routes>
              <Route path="/" element={<Login />} />
              <Route path="/register" element={<Register />} />
              <Route path="/forgot-password" element={<ForgotPassword />} />
              <Route path="/verify-otp" element={<VerifyOtp />} />
              <Route path="/reset-password" element={<ResetPassword />} />
              <Route path="*" element={<Login />} />
            </Routes>
          </BrowserRouter>
        ) : (
          <BrowserRouter>
            <Routes>
              <Route path="/" element={<SidebarLayout />}>
                <Route index element={<DefaultSessionPage/>} />
                <Route path="session/:sessionId" element={<ChatSessionPage />} />
              </Route>
            </Routes>
          </BrowserRouter>
        )}
      </>
      );
  };