import React, { useState, useEffect, useContext } from "react";
import { AuthContext } from "../services/AuthContext";
import { Button } from "antd";
import { useNavigate } from "react-router-dom";
import authService from "../services/authService";

export default function Navbar() {
  const navigate = useNavigate();
  const [userId, setUserId] = useState(0);
  const { setIsLoggedIn } = useContext(AuthContext);
  const { isLoggedIn } = useContext(AuthContext);
  const logout = () => {
    setIsLoggedIn(false);
    localStorage.removeItem("accessToken");
    setUserId(0);
    navigate("/");
  };
  const login = () => {
    navigate("/");
  };
  useEffect(() => {
    loadUserId();
  }, [userId]);
  const loadUserId = () => {
    authService.getUserId().then((result) => {
      setUserId(result.data);
    });
  };
  return (
    <nav className="navbar">
      <div style={{ width: 150 }}></div>
      <div style={{ width: 150 }}>
        <h1>Chat</h1>
      </div>
      <div style={{ width: 150 }}>
        <Button
          type="text"
          className="LogButton"
          onClick={() => {
            isLoggedIn ? logout() : login();
          }}
        >
          {isLoggedIn ? "Odjavi se" : "Prijavi se"}
        </Button>
      </div>
    </nav>
  );
}
