"use client";

import Link from "next/link";

export default function AdminPage() {
  return (
    <div className="min-h-screen bg-gray-900 text-white flex flex-col items-center justify-center p-6">
      <h1 className="text-3xl font-bold mb-6">관리자 페이지</h1>

      {/* 관리자 기능 선택 영역 */}
      <div className="grid grid-cols-2 gap-6 bg-gray-800 p-6 rounded-lg shadow-lg">
        <Link href="/admin/register">
          <button className="w-48 bg-indigo-500 hover:bg-indigo-600 text-white py-3 px-6 rounded-lg font-semibold text-lg transition">
            상품 등록
          </button>
        </Link>
        <Link href="/admin/items">
          <button className="w-48 bg-green-500 hover:bg-green-600 text-white py-3 px-6 rounded-lg font-semibold text-lg transition">
            전체 상품 조회
          </button>
        </Link>
        <Link href="/admin/items/category">
          <button className="w-48 bg-blue-500 hover:bg-blue-600 text-white py-3 px-6 rounded-lg font-semibold text-lg transition">
            카테고리별 조회
          </button>
        </Link>
        <Link href="/admin/items/single">
          <button className="w-48 bg-teal-500 hover:bg-teal-600 text-white py-3 px-6 rounded-lg font-semibold text-lg transition">
            단건 조회
          </button>
        </Link>
        <Link href="/admin/edit/info">
          <button className="w-48 bg-yellow-500 hover:bg-yellow-600 text-white py-3 px-6 rounded-lg font-semibold text-lg transition">
            상품 정보 수정
          </button>
        </Link>
        <Link href="/admin/edit/status">
          <button className="w-48 bg-orange-500 hover:bg-orange-600 text-white py-3 px-6 rounded-lg font-semibold text-lg transition">
            상품 상태 수정
          </button>
        </Link>
        <Link href="/admin/delete">
          <button className="w-48 bg-red-500 hover:bg-red-600 text-white py-3 px-6 rounded-lg font-semibold text-lg transition">
            상품 삭제
          </button>
        </Link>
      </div>
    </div>
  );
}
