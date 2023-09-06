import React, { useState, useEffect, useCallback, useRef } from "react";
import { Layout, Menu, Input, Button, List } from "antd";
import { SendOutlined, ReloadOutlined } from "@ant-design/icons";
import authService from "../services/authService";
import userService from "../services/userService";
import messageService from "../services/messageService";
export default function Chat() {
  const { TextArea } = Input;
  const messagesEndRef = useRef(null);
  const { Sider, Content } = Layout;
  const [userId, setUserId] = useState(0);
  const [messages, setMessages] = useState([]);
  const [message, setMessage] = useState("");
  const [users, setUsers] = useState([]);
  const [receiverId, setReceiverId] = useState(0);

  const loadMessages = useCallback(() => {
    if (receiverId !== 0) {
      messageService.getUsersChat(receiverId).then((result) => {
        setMessages(result.data);
      });
    }
  }, [receiverId]);

  useEffect(() => {
    loadMessages();
  }, [receiverId, loadMessages]);
  useEffect(() => {
    if (messagesEndRef.current) {
      console.log("Trying to scroll to the end");
      messagesEndRef.current.scrollIntoView({ behavior: "smooth" });
    }
  }, [messages]);
  useEffect(() => {
    loadChatUsers();
    loadUserId();
  }, []);
  const loadUserId = () => {
    authService.getUserId().then((result) => {
      setUserId(result.data);
    });
  };
  const handleSend = async () => {
    console.log(message);
    const objectMessage = {
      text: message,
      senderId: userId,
    };
    setMessages((prevMessages) => [...prevMessages, objectMessage]);
    await messageService
      .sendMessage(objectMessage, receiverId)
      .then((result) => {
        console.log(result);
      });
    setMessage("");
    // loadMessages();
  };
  const handleReload = () => {
    loadMessages();
    const lastId = messages.length - 1;
    console.log(`LAST ID: ${messages[lastId].id}`);
  };
  const loadChatUsers = () => {
    userService.getUsers().then((result) => {
      setUsers(result.data);
    });
  };

  const handleMenuClick = (e) => {
    console.log(e.key);
    setReceiverId(e.key);
  };

  const menuItems = users.map((user) => ({
    key: user.id,
    label: user.username,
  }));

  return (
    <div style={{ padding: 100 }}>
      <Layout
        style={{
          height: "70vh",
          border: 20,
          display: "flex",
        }}
      >
        <Sider style={{ height: "100%", width: "250px" }}>
          <Menu
            style={{ height: "100%", width: "100%", overflow: "auto" }}
            mode="inline"
            theme="dark"
            items={menuItems}
            onClick={handleMenuClick}
          />
        </Sider>
        <Content
          style={{
            height: "100%",
            display: "flex",
            flexDirection: "column",
          }}
        >
          <div
            style={{
              height: "90%",
              overflowY: "auto",
              width: "90%",
              margin: "0 auto",
            }}
          >
            <List
              style={{ display: "flex", flexDirection: "column" }}
              size="large"
              bordered
              dataSource={messages}
              renderItem={(item) => (
                <List.Item
                  style={{
                    display: "flex",
                    width: "100%",
                    justifyContent:
                      item.senderId === userId ? "flex-end" : "flex-start",
                  }}
                >
                  <div
                    style={{
                      padding: "10px 20px",
                      backgroundColor:
                        item.senderId === userId ? "#470076" : "lightgray",
                      color: item.senderId === userId ? "white" : "black",
                      borderRadius: 15,
                    }}
                  >
                    {item.text}
                  </div>
                </List.Item>
              )}
            >
              <div ref={messagesEndRef} />
            </List>
          </div>
          <div
            style={{
              width: "90%",
              padding: "0 0 20px 60px",
              display: "flex",
            }}
          >
            <TextArea
              value={message}
              onChange={(e) => setMessage(e.target.value)}
            />
            <Button
              icon={<SendOutlined style={{ fontSize: 30 }} />}
              style={{ height: 60, width: 60, marginLeft: 10 }}
              onClick={handleSend}
            />
            <Button
              icon={<ReloadOutlined style={{ fontSize: 30 }} />}
              style={{ height: 60, width: 60, marginLeft: 10 }}
              onClick={handleReload}
            />
          </div>
        </Content>
      </Layout>
    </div>
  );
}
