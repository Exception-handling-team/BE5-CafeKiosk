// // app/page.tsx
// import Link from "next/link";

// export default async function Page() {
//   const response = await fetch("http://localhost:8080/user/items");

//   if (!response.ok) {
//     throw new Error("error!");
//   }

//   const rsData = await response.json();

//   return (
//     <div className="p-6">
//       <h1 className="text-2xl font-bold mb-4">상품 목록</h1>
//       <div className="overflow-x-auto">
//         <table className="min-w-full border-collapse border border-gray-300">
//           <thead>
//             <tr className="bg-gray-200">
//               <th className="border border-gray-300 px-4 py-2">ID</th>
//               <th className="border border-gray-300 px-4 py-2">상품명</th>
//               <th className="border border-gray-300 px-4 py-2">가격</th>
//               <th className="border border-gray-300 px-4 py-2">수량</th>
//               <th className="border border-gray-300 px-4 py-2">상태</th>
//             </tr>
//           </thead>
//           <tbody>
//             {rsData.data.map((item: any) => (
//               <tr key={item.id} className="hover:bg-gray-100">
//                 <td className="border border-gray-300 px-4 py-2 text-center">
//                   {item.id}
//                 </td>
//                 <td className="border border-gray-300 px-4 py-2">
//                   <Link
//                     href={`/item/${item.id}`}
//                     className="text-blue-500 hover:underline"
//                   >
//                     {item.name}
//                   </Link>
//                 </td>
//                 <td className="border border-gray-300 px-4 py-2 text-right">
//                   {item.price.toLocaleString()} 원
//                 </td>
//                 <td className="border border-gray-300 px-4 py-2 text-center">
//                   {item.quantity}
//                 </td>
//                 <td className="border border-gray-300 px-4 py-2 text-center">
//                   {item.status}
//                 </td>
//               </tr>
//             ))}
//           </tbody>
//         </table>
//       </div>
//     </div>
//   );
// }
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
          // 백엔드 컨트롤러가 @RequestParam("category")로 변경되었다고 가정
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
