// app/page.tsx
import Link from "next/link";

export default async function Page() {
  const response = await fetch("http://localhost:8080/user/items");

  if (!response.ok) {
    throw new Error("error!");
  }

  const rsData = await response.json();

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4">상품 목록</h1>
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
            {rsData.data.map((item: any) => (
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
    </div>
  );
}
