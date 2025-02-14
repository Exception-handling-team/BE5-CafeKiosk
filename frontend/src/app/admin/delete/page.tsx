"use client";
import React, { useState } from "react";
import axiosInstance from "../../../lib/axios";

export default function DeleteItemPage() {
  const [deleteId, setDeleteId] = useState<number | null>(null);
  const [deleteResult, setDeleteResult] = useState("");

  const handleDeleteItem = async (e: React.FormEvent) => {
    e.preventDefault();
    setDeleteResult("");
    if (!deleteId) return;
    try {
      const response = await axiosInstance.delete("/admin/delete", {
        params: { id: deleteId },
      });
      setDeleteResult(JSON.stringify(response.data, null, 2));
    } catch (err) {
      console.error(err);
      setDeleteResult("상품 삭제 실패");
    }
  };

  return (
    <div className="bg-gray-800 p-6 rounded-lg shadow-md">
      <h2 className="text-2xl font-bold mb-4">상품 삭제</h2>
      <form onSubmit={handleDeleteItem} className="space-y-4">
        <div>
          <label className="block mb-1">상품 ID</label>
          <input
            type="number"
            value={deleteId ?? ""}
            onChange={(e) => setDeleteId(Number(e.target.value))}
            required
            className="w-full p-2 rounded bg-gray-700 border border-gray-600 focus:outline-none"
          />
        </div>
        <button
          type="submit"
          className="bg-indigo-600 hover:bg-indigo-700 text-white py-2 px-4 rounded font-bold transition-colors"
        >
          삭제
        </button>
      </form>
      {deleteResult && (
        <pre className="mt-4 p-2 bg-gray-700 rounded text-sm">
          {deleteResult}
        </pre>
      )}
    </div>
  );
}
