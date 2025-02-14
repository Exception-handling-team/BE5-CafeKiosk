"use client";

import { useState, useEffect } from "react";
import Link from "next/link";

interface ShowSimpleItem {
  id: number;
  name: string;
  price: number;
  quantity: number;
  status: "ON_SALE" | "SOLD_OUT";
}

interface ApiResponse<T> {
  message: string;
  data: T;
}

const categories = [
  { label: "전체 상품", value: "all" },
  { label: "음료", value: "BEVERAGE" },
  { label: "디저트", value: "DESSERT" },
  { label: "기타", value: "ETC" },
];

export default function Page() {
  const [items, setItems] = useState<ShowSimpleItem[]>([]);
  const [selectedCategory, setSelectedCategory] = useState<string>("all");
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchItems = async () => {
      setLoading(true);
      setError(null);

      try {
        let url = "";
        if (selectedCategory === "all") {
          url = "http://localhost:8080/user/items";
        } else {
          url = `http://localhost:8080/user/items/cat?category=${selectedCategory}`;
        }

        const res = await fetch(url, {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          },
        });
        if (!res.ok) {
          throw new Error("API 호출 실패");
        }

        const data: ApiResponse<ShowSimpleItem[]> = await res.json();
        setItems(data.data);
      } catch (err) {
        setError("상품을 불러오지 못했습니다.");
      }
      setLoading(false);
    };

    fetchItems();
  }, [selectedCategory]);

  return (
    <div className="min-h-screen bg-amber-50 p-6">
      <h1 className="text-3xl font-bold text-center mb-6">상품 목록</h1>
      <nav className="flex justify-center mb-6">
        {categories.map((cat) => (
          <button
            key={cat.value}
            onClick={() => setSelectedCategory(cat.value)}
            className={`mx-2 px-6 py-3 rounded-full font-semibold transition-colors duration-300 ${
              selectedCategory === cat.value
                ? "bg-indigo-600 text-white shadow-lg"
                : "bg-white text-gray-800 border border-gray-300 hover:bg-gray-100"
            }`}
          >
            {cat.label}
          </button>
        ))}
      </nav>

      {loading && (
        <div className="text-center text-xl font-medium text-gray-700">
          로딩중...
        </div>
      )}
      {error && (
        <div className="text-center text-red-500 font-semibold">{error}</div>
      )}
      {!loading && !error && (
        <div className="overflow-x-auto bg-white rounded-lg shadow-lg">
          <table className="min-w-full table-auto">
            <thead>
              <tr className="bg-amber-100">
                <th className="border border-gray-300 px-4 py-3 text-lg font-medium">
                  ID
                </th>
                <th className="border border-gray-300 px-4 py-3 text-lg font-medium">
                  상품명
                </th>
                <th className="border border-gray-300 px-4 py-3 text-lg font-medium">
                  가격
                </th>
                <th className="border border-gray-300 px-4 py-3 text-lg font-medium">
                  수량
                </th>
                <th className="border border-gray-300 px-4 py-3 text-lg font-medium">
                  상태
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200">
              {items.map((item) => (
                <tr
                  key={item.id}
                  className="hover:bg-amber-50 transition-colors duration-200"
                >
                  <td className="border border-gray-300 px-4 py-3 text-center">
                    {item.id}
                  </td>
                  <td className="border border-gray-300 px-4 py-3">
                    <Link
                      href={`/item/${item.id}`}
                      className="text-indigo-600 hover:underline font-semibold"
                    >
                      {item.name}
                    </Link>
                  </td>
                  <td className="border border-gray-300 px-4 py-3 text-right">
                    {item.price.toLocaleString()} 원
                  </td>
                  <td className="border border-gray-300 px-4 py-3 text-center">
                    {item.quantity}
                  </td>
                  <td className="border border-gray-300 px-4 py-3 text-center">
                    {item.status === "ON_SALE" ? (
                      <span className="text-green-600 font-bold">판매중</span>
                    ) : (
                      <span className="text-red-600 font-bold">품절</span>
                    )}
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
