import { notFound } from "next/navigation";
import type { paths } from "../../../lib/backend/apiV1/schema.d.ts";

interface Params {
  params: { id: string } | Promise<{ id: string }>;
}

export default async function ItemDetailPage({ params }: Params) {
  const { id } = await params;
  const numericId = Number(id);

  // auto-generated API 명세에 따른 GET 요청 (query parameter id: number)
  const response = await fetch(
    `http://localhost:8080/user/item?id=${numericId}`,
    {
      next: { revalidate: 60 },
    }
  );

  if (!response.ok) {
    return notFound();
  }

  // API 응답을 타입 단언으로 지정합니다.
  const result =
    (await response.json()) as paths["/user/item"]["get"]["responses"]["200"]["content"]["*/*"];
  const item = result.data;

  if (!item) return notFound();

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4">상품 상세 정보</h1>
      <div className="border p-4 rounded">
        <p>
          <strong>ID:</strong> {item.id}
        </p>
        <p>
          <strong>상품명:</strong> {item.name}
        </p>
        <p>
          <strong>가격:</strong> {item.price.toLocaleString()} 원
        </p>
        <p>
          <strong>수량:</strong> {item.quantity}
        </p>
        <p>
          <strong>상태:</strong> {item.status}
        </p>
      </div>
    </div>
  );
}
