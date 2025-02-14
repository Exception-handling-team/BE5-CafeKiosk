"use client";

import Image from "next/image";

export default function Page() {
  return (
    <main className="relative min-h-screen bg-gray-900">
      {/* 배경 이미지 */}
      <div className="absolute inset-0">
        <Image
          src="/coffeeImage.jpg"
          alt="Coffee background"
          fill
          className="object-cover opacity-80"
          priority
        />
      </div>

      {/* 콘텐츠 영역 */}
      <div className="relative z-10 flex flex-col items-center justify-center min-h-screen p-6 text-white">
        <h1 className="text-4xl md:text-5xl font-extrabold drop-shadow-lg mb-4">
          프로그래머스 백엔드 5기 부트캠프
        </h1>
        <h2 className="text-2xl md:text-3xl font-bold drop-shadow-lg mb-8">
          9팀 <span className="italic">“예외처리반”</span>
        </h2>

        <p className="text-lg md:text-xl mb-12 max-w-2xl text-center drop-shadow-sm">
          저희는 <strong>카페 메뉴 관리 프로그램</strong>을 개발하며, 편리하고
          효율적인 주문 환경을 제공하는 것을 목표로 하고 있습니다.
        </p>

        {/* 팀원 소개 카드 */}
        <div className="w-full max-w-3xl bg-gray-800 bg-opacity-70 rounded-lg p-6">
          <h3 className="text-xl md:text-2xl font-semibold mb-4">팀원 소개</h3>
          <ul className="space-y-2 text-lg">
            <li>• 최재우</li>
            <li>• 최현민</li>
            <li>• 장무영</li>
            <li>• 신동훈</li>
            <li>• 신윤호</li>
          </ul>
        </div>
      </div>
    </main>
  );
}
