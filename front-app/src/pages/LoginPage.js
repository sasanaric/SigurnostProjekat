import React from "react";
import { Button, message, Form, Input } from "antd";
import { useNavigate } from "react-router-dom";
import authService from "../services/authService";
import { useContext } from "react";
import { AuthContext } from "../services/AuthContext";

export default function LoginPage() {
  const navigate = useNavigate();
  const { setIsLoggedIn } = useContext(AuthContext);
  const onFinish = async (values) => {
    try {
      console.log(values);
      const response = await authService.login(values);
      console.log(response);
      const jwt = response.data.accessToken;
      localStorage.setItem("accessToken", jwt);
      message.success("Uspjesno ste se ulogovali");
      setIsLoggedIn(true);
      navigate("/chat");
    } catch (error) {
      message.error("Niste se uspjesno ulogovali");
    }
  };
  return (
    <div className="login-div">
      <Form
        name="basic"
        labelCol={{
          span: 8,
        }}
        wrapperCol={{
          span: 16,
        }}
        style={{
          maxWidth: 600,
        }}
        initialValues={{
          remember: true,
        }}
        onFinish={onFinish}
        autoComplete="off"
      >
        <Form.Item
          label="Username"
          name="username"
          rules={[
            {
              required: true,
              message: "Please input your username!",
            },
          ]}
        >
          <Input />
        </Form.Item>
        <Form.Item
          label="Password"
          name="password"
          rules={[
            {
              required: true,
              message: "Please input your password!",
            },
          ]}
        >
          <Input.Password />
        </Form.Item>
        <Form.Item
          wrapperCol={{
            offset: 8,
            span: 16,
          }}
        >
          <Button type="primary" htmlType="submit">
            LogIn
          </Button>
        </Form.Item>
      </Form>
    </div>
  );
}
