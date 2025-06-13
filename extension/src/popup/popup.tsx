import React, { useEffect, useState } from "react";
import "./popup.css";
import Login from "../views/Login/Login";
import { Register } from "../views/Register/Register";
import {
  getFromLocalStorage,
  setToLocalStorage,
} from "../utils/utils.function";
import { STORAGE_KEYS } from "../constants/constants";
import { CONFIG } from "../config/dev.config";
import developer from "../../assets/file.svg";

export const Popup = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [haveAccount, setHaveAccount] = useState(false);

  const toggleHaveAccount = (value: boolean) => {
    setHaveAccount(value);
  };

  const handleLogout = () => {
    chrome.cookies.remove({ url: CONFIG.ORIGIN, name: "jwtToken" }, () => {
      setIsLoggedIn(false);
      setToLocalStorage({ isLoggedIn: false, userId: "", sessionId: "" });
    });
  };

  useEffect(() => {
    const checkLoggedIn = async () => {
      const response = await getFromLocalStorage([
        STORAGE_KEYS.USERID,
        STORAGE_KEYS.IS_LOGGED_IN,
      ]);

      if(response.isLoggedIn) {
        setIsLoggedIn(true);
      }else {
        setIsLoggedIn(false)
      }
    }
    checkLoggedIn();
  }, []);

  return (
    <>
      {!isLoggedIn ? (
        <>
          {haveAccount ? (
            <>
              {" "}
              <Login onRegister={toggleHaveAccount} />{" "}
            </>
          ) : (
            <Register onLogin={toggleHaveAccount} />
          )}
        </>
      ) : (
        <>
          <div className="logged-in-container">
            <h2 className="logged-in-title">Successfully Logged In</h2>
            <hr />
            <p className="logged-in-message">
              <strong> Welcome to Codelens!</strong>
            </p>

            <div className="extension-message">
              <strong>How to use CodeLens?</strong>
              <p>Highlight the code snippet you want to summarize. </p>
              <p>
                A dynamic icon will appear near your selection; click on this
                icon.
              </p>
              <p>
                A popup will display a concise, human-readable summary of the
                selected code.
              </p>{" "}
              <p>
                If the selected text exceeds the character limit, an error
                notification will appear, prompting you to use the web
                application for extended functionality.
              </p>
            </div>

            {/* <img className="login-image" src={developer} alt="Developer" /> */}
            <button className="logout-button" onClick={handleLogout}>
              Logout
            </button>
          </div>
        </>
      )}
    </>
  );
};
