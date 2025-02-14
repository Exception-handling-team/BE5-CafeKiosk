"use client";

import Image from "next/image";
import { useRouter } from "next/navigation";

export default function Home() {
  const router = useRouter();

  const handleOrderClick = () => {
    router.push("/menu");
  };

  return (
    <main className="relative min-h-screen flex items-center justify-center bg-gray-900">
      {/* 배경 이미지 */}
      <div className="absolute inset-0">
        <Image
          src="/coffeeImage.jpg"
          alt="Warm Coffee Image"
          fill
          className="object-cover opacity-80"
          priority
        />
      </div>

      {/* 오버레이 & 콘텐츠 */}
      <div className="absolute inset-0 flex flex-col items-center justify-center text-center px-4">
        <h1 className="text-4xl md:text-5xl font-extrabold text-white drop-shadow-lg mb-4">
          Welcome to Our Cafe
        </h1>
        <p className="text-lg md:text-xl text-gray-100 mb-8 max-w-xl mx-auto">
          따뜻한 커피와 향긋한 디저트가 가득한 공간, 지금 바로 주문하여 특별한
          순간을 만들어보세요!
        </p>
        <button
          onClick={handleOrderClick}
          className="bg-indigo-600 hover:bg-indigo-700 text-white px-8 py-3 rounded-full font-semibold transition-colors duration-300"
        >
          주문하기
        </button>
      </div>
    </main>
  );
}
