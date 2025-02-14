"use client";
import React, { useState } from "react";
import axiosInstance from "../../../../lib/axios";

export default function EditInfoPage() {
  const [editInfoId, setEditInfoId] = useState<number | null>(null);
  const [editInfoName, setEditInfoName] = useState("");
  const [editInfoPrice, setEditInfoPrice] = useState(0);
  const [editInfoQuantity, setEditInfoQuantity] = useState(0);
  const [editInfoDescription, setEditInfoDescription] = useState("");
  const [editInfoResult, setEditInfoResult] = useState("");

  const handleEditInfo = async (e: React.FormEvent) => {
    e.preventDefault();
    setEditInfoResult("");
    if (!editInfoId) return;
    try {
      const response = await axiosInstance.put(
        "/admin/edit/info",
        {
          name: editInfoName,
          price: editInfoPrice,
          quantity: editInfoQuantity,
          description: editInfoDescription,
        },
        {
          params: { id: editInfoId },
        }
      );
      setEditInfoResult(JSON.stringify(response.data, null, 2));
    } catch (err) {
      console.error(err);
      setEditInfoResult("상품 정보 수정 실패");
    }
  };

  return (
    <div className="bg-gray-800 p-6 rounded-lg shadow-md">
      <h2 className="text-2xl font-bold mb-4">상품 정보 수정</h2>
      <form onSubmit={handleEditInfo} className="space-y-4">
        <div>
          <label className="block mb-1">상품 ID</label>
          <input
            type="number"
            value={editInfoId ?? ""}
            onChange={(e) => setEditInfoId(Number(e.target.value))}
            required
            className="w-full p-2 rounded bg-gray-700 border border-gray-600 focus:outline-none"
          />
        </div>
        <div>
          <label className="block mb-1">새 상품명</label>
          <input
            type="text"
            value={editInfoName}
            onChange={(e) => setEditInfoName(e.target.value)}
            className="w-full p-2 rounded bg-gray-700 border border-gray-600 focus:outline-none"
          />
        </div>
        <div>
          <label className="block mb-1">새 가격</label>
          <input
            type="number"
            value={editInfoPrice}
            onChange={(e) => setEditInfoPrice(Number(e.target.value))}
            className="w-full p-2 rounded bg-gray-700 border border-gray-600 focus:outline-none"
          />
        </div>
        <div>
          <label className="block mb-1">새 수량</label>
          <input
            type="number"
            value={editInfoQuantity}
            onChange={(e) => setEditInfoQuantity(Number(e.target.value))}
            className="w-full p-2 rounded bg-gray-700 border border-gray-600 focus:outline-none"
          />
        </div>
        <div>
          <label className="block mb-1">새 설명</label>
          <input
            type="text"
            value={editInfoDescription}
            onChange={(e) => setEditInfoDescription(e.target.value)}
            className="w-full p-2 rounded bg-gray-700 border border-gray-600 focus:outline-none"
          />
        </div>
        <button
          type="submit"
          className="bg-indigo-600 hover:bg-indigo-700 text-white py-2 px-4 rounded font-bold transition-colors"
        >
          수정
        </button>
      </form>
      {editInfoResult && (
        <pre className="mt-4 p-2 bg-gray-700 rounded text-sm">
          {editInfoResult}
        </pre>
      )}
    </div>
  );
}
