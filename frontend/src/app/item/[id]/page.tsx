import { notFound } from "next/navigation";
import type { paths } from "../../../lib/backend/apiV1/schema.d.ts";
import OrderButton from "./orderButton"; // 아래에서 만든 클라이언트 컴포넌트

interface Params {
  params: { id: string } | Promise<{ id: string }>;
}

export default async function ItemDetailPage({ params }: Params) {
  const { id } = await params;
  const numericId = Number(id);

  // API 호출: query parameter id를 사용해 상품 상세 정보를 가져옵니다.
  const response = await fetch(
    `http://localhost:8080/user/item?id=${numericId}`,
    {
      // next: { revalidate: 60 } // 필요 시 캐시/ISR 설정
    }
  );

  if (!response.ok) {
    return notFound();
  }

  const result =
    (await response.json()) as paths["/user/item"]["get"]["responses"]["200"]["content"]["*/*"];
  const item = result.data;

  if (!item) return notFound();

  return (
    <div className="min-h-screen bg-amber-50 flex items-center justify-center p-6">
      <div className="bg-white rounded-lg shadow-xl p-8 w-full max-w-md">
        <h1 className="text-3xl font-bold text-center mb-6">상품 상세 정보</h1>
        <div className="border-t border-gray-200 pt-4">
          <p className="mb-2">
            <span className="font-semibold">ID:</span> {item.id}
          </p>
          <p className="mb-2">
            <span className="font-semibold">상품명:</span> {item.name}
          </p>
          <p className="mb-2">
            <span className="font-semibold">가격:</span>{" "}
            {item.price.toLocaleString()} 원
          </p>
          <p className="mb-2">
            <span className="font-semibold">수량:</span> {item.quantity}
          </p>
          <p className="mb-4">
            <span className="font-semibold">상태:</span>{" "}
            {item.status === "ON_SALE" ? (
              <span className="text-green-600">판매중</span>
            ) : (
              <span className="text-red-600">품절</span>
            )}
          </p>
        </div>
        {/* 주문 기능을 담당할 클라이언트 컴포넌트 */}
        <div className="mt-6">
          <OrderButton itemId={item.id} maxQuantity={item.quantity} />
        </div>
      </div>
    </div>
  );
}
