import React, { useState } from "react";
import styled from "styled-components";
import { useNavigate } from "react-router-dom";
import PixelButton from "../components/PixelButton";
import client from "../api/client";

export default function Auth() {
  const [isLoginMode, setIsLoginMode] = useState(true);
  const [formData, setFormData] = useState({
    username: "",
    password: "",
    email: "",
  });
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const validateForm = () => {
    if (!isLoginMode) {
      if (formData.username.trim().length < 3) {
        setError("아이디는 최소 3자 이상이어야 합니다.");
        return false;
      }

      if (formData.password.length < 6) {
        setError("비밀번호는 최소 6자 이상이어야 합니다.");
        return false;
      }

      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(formData.email)) {
        setError("올바른 이메일 형식을 입력해주세요.");
        return false;
      }
    } else {
      if (!formData.username.trim() || !formData.password) {
        setError("아이디와 비밀번호를 입력해주세요.");
        return false;
      }
    }

    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    // 입력값 검증
    if (!validateForm()) {
      return;
    }

    try {
      if (isLoginMode) {
        const response = await client.post("/auth/login", {
          username: formData.username,
          password: formData.password,
        });
        const { token } = response.data.data;
        localStorage.setItem("jwt_token", token);
        navigate("/app/village");
      } else {
        await client.post("/auth/signup", formData);
        alert("회원가입이 완료되었습니다! 로그인해주세요.");
        setIsLoginMode(true);
        setFormData({ username: "", password: "", email: "" });
      }
    } catch (err) {
      setError(err.response?.data?.message || "오류가 발생했습니다.");
    }
  };

  return (
    <Container>
      <PixelButton
        text={isLoginMode ? "로그인" : "회원가입"}
        isButton={false}
      />
      <Form>
        <Input
          name="username"
          type="text"
          placeholder="아이디"
          value={formData.username}
          onChange={handleChange}
          required
        />
        <Input
          name="password"
          type="password"
          placeholder="비밀번호"
          value={formData.password}
          onChange={handleChange}
          required
        />
        {!isLoginMode && (
          <Input
            name="email"
            type="email"
            placeholder="이메일"
            value={formData.email}
            onChange={handleChange}
            required
          />
        )}
        {error && <ErrorMessage>{error}</ErrorMessage>}
      </Form>
      <PixelButton
        text={isLoginMode ? "로그인" : "회원가입"}
        onClick={handleSubmit}
        backgroundColor='#DD1A21'
        textColor='white'
        borderColor='#05131D'
        width='210px'
        fontSize='24px'
      />
      <Toggle onClick={() => setIsLoginMode(!isLoginMode)}>
        {isLoginMode
          ? "계정이 없으신가요? 회원가입"
          : "이미 계정이 있으신가요? 로그인"}
      </Toggle>
    </Container>
  );
}

const Container = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #A0E4FF;
  padding: 20px;
  gap: 60px;
`;

const Form = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 20px;
`;

const Input = styled.input`
  font-family: inherit;
  font-size: 20px;
  padding: 20px;
  border: 2px solid #dfe6e9;
  border-radius: 8px;
  outline: none;

  &:focus {
    border-color: #8D1FA6;
  }
`;

const Toggle = styled.div`
  font-size: 16px;
  color: #8D1FA6;
  cursor: pointer;
`;

const ErrorMessage = styled.div`
  color: red;
  font-size: 16px;
`;
