"use client";
import React, { useState } from "react";
import axiosInstance from "../../../lib/axios";

export default function AllItemsPage() {
  const [allItems, setAllItems] = useState<any[]>([]);

  const handleShowAllItems = async () => {
    try {
      const response = await axiosInstance.get("/admin/items");
      setAllItems(response.data.data);
    } catch (err) {
      console.error(err);
      alert("전체 상품 조회 실패");
    }
  };

  return (
    <div className="bg-gray-800 p-6 rounded-lg shadow-md">
      <h2 className="text-2xl font-bold mb-4">전체 상품 조회</h2>
      <button
        onClick={handleShowAllItems}
        className="bg-indigo-600 hover:bg-indigo-700 text-white py-2 px-4 rounded font-bold transition-colors"
      >
        조회
      </button>

      <ul className="mt-4 space-y-2 text-sm">
        {allItems.map((item, idx) => (
          <li key={idx} className="bg-gray-700 p-2 rounded">
            {JSON.stringify(item)}
          </li>
        ))}
      </ul>
    </div>
  );
}
