"use client";
import React, { useState } from "react";
import axiosInstance from "../../../../lib/axios";

export default function EditStatusPage() {
  const [editStatusId, setEditStatusId] = useState<number | null>(null);
  const [editStatusResult, setEditStatusResult] = useState("");

  const handleEditStatus = async (e: React.FormEvent) => {
    e.preventDefault();
    setEditStatusResult("");
    if (!editStatusId) return;
    try {
      const response = await axiosInstance.put("/admin/edit/status", null, {
        params: { id: editStatusId },
      });
      setEditStatusResult(JSON.stringify(response.data, null, 2));
    } catch (err) {
      console.error(err);
      setEditStatusResult("상품 상태 수정 실패");
    }
  };

  return (
    <div className="bg-gray-800 p-6 rounded-lg shadow-md">
      <h2 className="text-2xl font-bold mb-4">상품 상태 수정</h2>
      <form onSubmit={handleEditStatus} className="space-y-4">
        <div>
          <label className="block mb-1">상품 ID</label>
          <input
            type="number"
            value={editStatusId ?? ""}
            onChange={(e) => setEditStatusId(Number(e.target.value))}
            required
            className="w-full p-2 rounded bg-gray-700 border border-gray-600 focus:outline-none"
          />
        </div>
        <button
          type="submit"
          className="bg-indigo-600 hover:bg-indigo-700 text-white py-2 px-4 rounded font-bold transition-colors"
        >
          상태 수정
        </button>
      </form>
      {editStatusResult && (
        <pre className="mt-4 p-2 bg-gray-700 rounded text-sm">
          {editStatusResult}
        </pre>
      )}
    </div>
  );
}
