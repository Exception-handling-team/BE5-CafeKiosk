"use client";

import { useState } from "react";

// 주문 API 요청에 대한 응답 타입 (예: OrderResponseDto)
interface OrderResponseDto {
  tradeId?: number;
  status?: "BUY" | "PAY" | "END" | "REFUND" | "REFUSED";
  totalPrice?: number;
  tradeUUID?: string;
}

interface ApiResponse<T> {
  message: string;
  data: T;
}

// 이 컴포넌트가 받을 props
interface OrderButtonProps {
  itemId: number;
  maxQuantity: number;
}

export default function OrderButton({ itemId, maxQuantity }: OrderButtonProps) {
  const [quantity, setQuantity] = useState(1);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");

  // 주문 버튼 클릭 시 /order 엔드포인트로 POST 요청
  const handleOrder = async () => {
    setLoading(true);
    setMessage("");

    try {
      // OrderRequestDto 배열 (복수 상품 주문 시 여러 객체)
      const payload = [
        {
          itemId,
          quantity,
        },
      ];

      const res = await fetch("http://localhost:8080/order", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(payload),
      });

      if (!res.ok) {
        throw new Error("주문 요청 실패");
      }

      const json: ApiResponse<OrderResponseDto> = await res.json();
      setMessage(`주문이 완료되었습니다. 주문번호: ${json.data.tradeUUID}`);
    } catch (error) {
      console.error(error);
      setMessage("주문에 실패했습니다.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="mt-4">
      <label htmlFor="quantity" className="mr-2">
        주문 수량:
      </label>
      <input
        id="quantity"
        type="number"
        min={1}
        max={maxQuantity}
        value={quantity}
        onChange={(e) => setQuantity(Number(e.target.value))}
        className="border border-gray-300 p-1 mr-2 w-20"
      />

      <button
        onClick={handleOrder}
        disabled={loading}
        className="bg-blue-500 text-white px-4 py-2 rounded"
      >
        {loading ? "주문 중..." : "주문하기"}
      </button>

      {message && <div className="mt-2 text-sm">{message}</div>}
    </div>
  );
}
