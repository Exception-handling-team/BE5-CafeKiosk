// app/item/[id]/page.tsx
import { notFound } from "next/navigation";

interface Item {
  id: number;
  name: string;
  price: number;
  quantity: number;
  status: string;
}

interface Params {
  params: { id: string } | Promise<{ id: string }>;
}

export default async function ItemDetailPage({ params }: Params) {
  // params가 Promise일 수 있으므로 await를 통해 먼저 해결합니다.
  const { id } = await params;

  const response = await fetch(`http://localhost:8080/user/item?id=${id}`, {
    next: { revalidate: 60 }, // ISR 설정 (선택사항)
  });

  if (!response.ok) {
    return notFound();
  }

  const rsData = await response.json();
  const item: Item = rsData.data;

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
