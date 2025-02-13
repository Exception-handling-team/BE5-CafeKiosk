// app/itemsList.tsx (클라이언트 컴포넌트)
"use client";

import { useState, useEffect } from "react";
import Link from "next/link";

type Item = {
  id: number;
  name: string;
  price: number;
  quantity: number;
  status: string;
};

type ApiResponse<T> = {
  message: string;
  data: T;
};

const categories = [
  { label: "전체 상품", value: "all" },
  { label: "음료", value: "BEVERAGE" },
  { label: "디저트", value: "DESSERT" },
  { label: "기타", value: "ETC" },
];

export default function ItemsList() {
  const [items, setItems] = useState<Item[]>([]);
  const [selectedCategory, setSelectedCategory] = useState<string>("all");
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  const fetchItems = async (category: string) => {
    setLoading(true);
    setError(null);
    try {
      let url = "";
      const options: RequestInit = {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
        },
      };

      if (category === "all") {
        // 전체 상품 조회
        url = "http://localhost:8080/user/items";
      } else {
        // TS API 파일에 따르면 GET /user/items/cat는 query 파라미터로 categoryDto를 받습니다.
        // (백엔드 컨트롤러는 @RequestBody를 사용하지만, TS 스펙에 맞춰 쿼리스트링으로 호출합니다.)
        const queryParam = encodeURIComponent(JSON.stringify({ category }));
        url = `http://localhost:8080/user/items/cat?categoryDto=${queryParam}`;
      }

      const res = await fetch(url, options);
      if (!res.ok) throw new Error("API 호출 실패");
      const data: ApiResponse<Item[]> = await res.json();
      setItems(data.data);
    } catch (err) {
      setError("상품을 불러오지 못했습니다.");
    }
    setLoading(false);
  };

  useEffect(() => {
    fetchItems(selectedCategory);
  }, [selectedCategory]);

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4">상품 목록</h1>
      {/* 네비게이션 바 */}
      <nav className="mb-4">
        {categories.map((cat) => (
          <button
            key={cat.value}
            onClick={() => setSelectedCategory(cat.value)}
            className={`mr-2 px-4 py-2 rounded ${
              selectedCategory === cat.value
                ? "bg-blue-500 text-white"
                : "bg-gray-200 text-black"
            }`}
          >
            {cat.label}
          </button>
        ))}
      </nav>
      {loading && <div>로딩중...</div>}
      {error && <div>{error}</div>}
      {!loading && !error && (
        <div className="overflow-x-auto">
          <table className="min-w-full border-collapse border border-gray-300">
            <thead>
              <tr className="bg-gray-200">
                <th className="border border-gray-300 px-4 py-2">ID</th>
                <th className="border border-gray-300 px-4 py-2">상품명</th>
                <th className="border border-gray-300 px-4 py-2">가격</th>
                <th className="border border-gray-300 px-4 py-2">수량</th>
                <th className="border border-gray-300 px-4 py-2">상태</th>
              </tr>
            </thead>
            <tbody>
              {items.map((item) => (
                <tr key={item.id} className="hover:bg-gray-100">
                  <td className="border border-gray-300 px-4 py-2 text-center">
                    {item.id}
                  </td>
                  <td className="border border-gray-300 px-4 py-2">
                    <Link
                      href={`/item/${item.id}`}
                      className="text-blue-500 hover:underline"
                    >
                      {item.name}
                    </Link>
                  </td>
                  <td className="border border-gray-300 px-4 py-2 text-right">
                    {item.price.toLocaleString()} 원
                  </td>
                  <td className="border border-gray-300 px-4 py-2 text-center">
                    {item.quantity}
                  </td>
                  <td className="border border-gray-300 px-4 py-2 text-center">
                    {item.status}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
