"use client";

import React, { useState } from "react";
import { useAuth } from "../../context/AuthContext";
import axiosInstance from "../../lib/axios";
import { useRouter } from "next/navigation";

export default function LoginPage() {
  const [loginId, setLoginId] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const { setAccessToken } = useAuth();
  const router = useRouter();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");

    try {
      // 백엔드 /auth/login 호출
      const response = await axiosInstance.post("/auth/login", {
        loginId,
        password,
      });
      // 예: { message: "로그인에 성공하였습니다.", data: { accessToken: "..." } }
      const accessToken = response.data.data.accessToken;
      // 전역 상태에 access token 저장
      setAccessToken(accessToken);

      // 로그인 성공 후 메인 페이지('/')로 이동 (예시)
      router.push("/");
    } catch (err) {
      console.error(err);
      setError("로그인에 실패하였습니다. 아이디/비밀번호를 확인해주세요.");
    }
  };

  return (
    <div className="min-h-screen bg-gray-800 flex items-center justify-center p-6">
      <div className="bg-gray-900 text-white rounded-lg shadow-2xl p-8 w-full max-w-md">
        <h1 className="text-3xl font-extrabold text-center mb-6">
          관리자 로그인
        </h1>
        <form onSubmit={handleSubmit}>
          <div className="mb-4">
            <label htmlFor="loginId" className="block mb-1 font-semibold">
              아이디
            </label>
            <input
              id="loginId"
              type="text"
              value={loginId}
              onChange={(e) => setLoginId(e.target.value)}
              required
              className="w-full p-3 rounded bg-gray-700 border border-gray-600 focus:outline-none focus:ring-2 focus:ring-indigo-500"
            />
          </div>
          <div className="mb-4">
            <label htmlFor="password" className="block mb-1 font-semibold">
              비밀번호
            </label>
            <input
              id="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              className="w-full p-3 rounded bg-gray-700 border border-gray-600 focus:outline-none focus:ring-2 focus:ring-indigo-500"
            />
          </div>
          {error && <p className="mb-4 text-red-400">{error}</p>}
          <button
            type="submit"
            className="w-full bg-indigo-600 hover:bg-indigo-700 text-white py-3 rounded-lg font-bold transition-colors duration-300"
          >
            로그인
          </button>
        </form>
      </div>
    </div>
  );
}
