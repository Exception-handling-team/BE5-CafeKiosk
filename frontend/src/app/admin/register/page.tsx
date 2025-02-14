"use client";

import React, { useState } from "react";
import axiosInstance from "../../../lib/axios";

export default function RegisterPage() {
  const [registerName, setRegisterName] = useState("");
  const [registerPrice, setRegisterPrice] = useState(0);
  const [registerDescription, setRegisterDescription] = useState("");
  const [registerStatus, setRegisterStatus] = useState("ON_SALE");
  const [registerCategory, setRegisterCategory] = useState("BEVERAGE");
  const [registerQuantity, setRegisterQuantity] = useState(0);
  const [registerResult, setRegisterResult] = useState("");

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    setRegisterResult("");

    try {
      const response = await axiosInstance.post("/admin/register", {
        name: registerName,
        price: registerPrice,
        description: registerDescription,
        status: registerStatus,
        category: registerCategory,
        quantity: registerQuantity,
      });
      setRegisterResult(JSON.stringify(response.data, null, 2));
    } catch (err) {
      console.error(err);
      setRegisterResult("상품 등록 실패");
    }
  };

  return (
    <div className="bg-gray-800 p-6 rounded-lg shadow-md">
      <h2 className="text-2xl font-bold mb-4">상품 등록</h2>
      <form onSubmit={handleRegister} className="space-y-4">
        <div>
          <label className="block mb-1">상품명</label>
          <input
            type="text"
            value={registerName}
            onChange={(e) => setRegisterName(e.target.value)}
            required
            className="w-full p-2 rounded bg-gray-700 border border-gray-600 focus:outline-none"
          />
        </div>
        <div>
          <label className="block mb-1">가격</label>
          <input
            type="text"
            value={registerPrice}
            onChange={(e) => setRegisterPrice(Number(e.target.value))}
            required
            className="w-full p-2 rounded bg-gray-700 border border-gray-600 focus:outline-none"
          />
        </div>
        <div>
          <label className="block mb-1">설명</label>
          <input
            type="text"
            value={registerDescription}
            onChange={(e) => setRegisterDescription(e.target.value)}
            className="w-full p-2 rounded bg-gray-700 border border-gray-600 focus:outline-none"
          />
        </div>
        <div>
          <label className="block mb-1">상품 상태</label>
          <select
            value={registerStatus}
            onChange={(e) => setRegisterStatus(e.target.value)}
            className="w-full p-2 rounded bg-gray-700 border border-gray-600 focus:outline-none"
          >
            <option value="ON_SALE">판매중</option>
            <option value="SOLD_OUT">품절</option>
          </select>
        </div>
        <div>
          <label className="block mb-1">카테고리</label>
          <select
            value={registerCategory}
            onChange={(e) => setRegisterCategory(e.target.value)}
            className="w-full p-2 rounded bg-gray-700 border border-gray-600 focus:outline-none"
          >
            <option value="BEVERAGE">음료</option>
            <option value="DESSERT">디저트</option>
            <option value="ETC">기타</option>
          </select>
        </div>
        <div>
          <label className="block mb-1">수량</label>
          <input
            type="number"
            value={registerQuantity}
            onChange={(e) => setRegisterQuantity(Number(e.target.value))}
            required
            className="w-full p-2 rounded bg-gray-700 border border-gray-600 focus:outline-none"
          />
        </div>
        <button
          type="submit"
          className="bg-indigo-600 hover:bg-indigo-700 text-white py-2 px-4 rounded font-bold transition-colors"
        >
          상품 등록
        </button>
      </form>

      {registerResult && (
        <pre className="mt-4 p-2 bg-gray-700 rounded text-sm">
          {registerResult}
        </pre>
      )}
    </div>
  );
}
