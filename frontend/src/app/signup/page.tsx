"use client";

import { useState } from "react";
import axiosInstance from "../../lib/axios";
import { useRouter } from "next/navigation";

export default function SignupPage() {
  const [loginId, setLoginId] = useState("");
  const [password, setPassword] = useState("");
  const [adminKey, setAdminKey] = useState("");
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");
  const router = useRouter();

  const handleSignup = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setMessage("");

    try {
      // 백엔드 /auth/signup 엔드포인트로 회원가입 요청
      const response = await axiosInstance.post("/auth/signup", {
        loginId,
        password,
        adminKey,
      });
      // 성공 시 메시지 표시
      setMessage("회원가입에 성공했습니다! 이제 로그인해주세요.");
      // 필요 시 일정 시간 후 로그인 페이지로 이동
      // router.push('/login');
    } catch (err) {
      console.error(err);
      setError("회원가입에 실패했습니다. 아이디나 비밀번호를 확인해주세요.");
    }
  };

  return (
    <div className="min-h-screen bg-gray-800 flex items-center justify-center p-6">
      <div className="bg-gray-900 text-white rounded-lg shadow-2xl p-8 w-full max-w-md">
        <h1 className="text-3xl font-extrabold text-center mb-6">
          관리자 회원가입
        </h1>
        <form onSubmit={handleSignup}>
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
          <div className="mb-4">
            <label htmlFor="username" className="block mb-1 font-semibold">
              관리자 인증키
            </label>
            <input
              id="adminKey"
              type="password"
              value={adminKey}
              onChange={(e) => setAdminKey(e.target.value)}
              required
              className="w-full p-3 rounded bg-gray-700 border border-gray-600 focus:outline-none focus:ring-2 focus:ring-indigo-500"
            />
          </div>
          {error && <p className="mb-4 text-red-400">{error}</p>}
          {message && <p className="mb-4 text-green-400">{message}</p>}
          <button
            type="submit"
            className="w-full bg-indigo-600 hover:bg-indigo-700 text-white py-3 rounded-lg font-bold transition-colors duration-300"
          >
            회원가입
          </button>
        </form>
      </div>
    </div>
  );
}
