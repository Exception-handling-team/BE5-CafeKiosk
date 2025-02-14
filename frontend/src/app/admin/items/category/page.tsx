"use client";
import React, { useState } from "react";
import axiosInstance from "../../../../lib/axios";

export default function CategoryItemsPage() {
  const [category, setCategory] = useState("BEVERAGE");
  const [categoryItems, setCategoryItems] = useState<any[]>([]);

  const handleShowCategoryItems = async (e: React.FormEvent) => {
    e.preventDefault();
    setCategoryItems([]);
    try {
      const response = await axiosInstance.post("/admin/items/cat", {
        category: category, // JSON 형식으로 데이터 전송
      });

      setCategoryItems(response.data.data);
    } catch (err) {
      console.error(err);
      alert("카테고리별 상품 조회 실패");
    }
  };

  return (
    <div className="bg-gray-800 p-6 rounded-lg shadow-md">
      <h2 className="text-2xl font-bold mb-4">카테고리별 상품 조회</h2>
      <form onSubmit={handleShowCategoryItems} className="space-y-4">
        <div>
          <label className="block mb-1">카테고리</label>
          <select
            value={category}
            onChange={(e) => setCategory(e.target.value)}
            required
            className="w-full p-2 rounded bg-gray-700 border border-gray-600 focus:outline-none"
          >
            <option value="BEVERAGE">음료</option>
            <option value="DESSERT">디저트</option>
            <option value="ETC">기타</option>
          </select>
        </div>
        <button
          type="submit"
          className="bg-indigo-600 hover:bg-indigo-700 text-white py-2 px-4 rounded font-bold transition-colors"
        >
          조회
        </button>
      </form>
      <ul className="mt-4 space-y-2 text-sm">
        {categoryItems.length > 0 ? (
          categoryItems.map((item, idx) => (
            <li key={idx} className="bg-gray-700 p-2 rounded">
              {JSON.stringify(item)}
            </li>
          ))
        ) : (
          <p className="text-gray-400">조회된 상품이 없습니다.</p>
        )}
      </ul>
    </div>
  );
}
