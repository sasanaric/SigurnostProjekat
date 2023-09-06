import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import "./App.css";
import Navbar from "./layout/Navbar";
import Login from "./pages/LoginPage";
import Chat from "./pages/Chat";
import { AuthProvider } from "./services/AuthContext";
function App() {
  return (
    <div className="App">
      <AuthProvider>
        <Router>
          <Navbar />
          <Routes>
            <Route exact path="/" element={<Login />} />
            <Route exact path="/chat" element={<Chat />} />
          </Routes>
        </Router>
      </AuthProvider>
    </div>
  );
}

export default App;
