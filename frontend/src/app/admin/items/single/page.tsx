"use client";
import React, { useState } from "react";
import axiosInstance from "../../../../lib/axios";

export default function SingleItemPage() {
  const [singleItemId, setSingleItemId] = useState<number | null>(null);
  const [singleItem, setSingleItem] = useState<any>(null);

  const handleShowSingleItem = async (e: React.FormEvent) => {
    e.preventDefault();
    setSingleItem(null);
    if (!singleItemId) return;
    try {
      const response = await axiosInstance.get("/admin/item", {
        params: { id: singleItemId },
      });
      setSingleItem(response.data.data);
    } catch (err) {
      console.error(err);
      alert("단건 상품 조회 실패");
    }
  };

  return (
    <div className="bg-gray-800 p-6 rounded-lg shadow-md">
      <h2 className="text-2xl font-bold mb-4">단건 상품 조회</h2>
      <form onSubmit={handleShowSingleItem} className="space-y-4">
        <div>
          <label className="block mb-1">상품 ID</label>
          <input
            type="number"
            value={singleItemId ?? ""}
            onChange={(e) => setSingleItemId(Number(e.target.value))}
            required
            className="w-full p-2 rounded bg-gray-700 border border-gray-600 focus:outline-none"
          />
        </div>
        <button
          type="submit"
          className="bg-indigo-600 hover:bg-indigo-700 text-white py-2 px-4 rounded font-bold transition-colors"
        >
          조회
        </button>
      </form>
      {singleItem && (
        <pre className="mt-4 p-2 bg-gray-700 rounded text-sm">
          {JSON.stringify(singleItem, null, 2)}
        </pre>
      )}
    </div>
  );
}
