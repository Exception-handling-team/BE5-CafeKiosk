"use client";

import { ReactNode } from "react";

export default function AdminLayout({ children }: { children: ReactNode }) {
  return (
    <div className="container mx-auto p-6">
      {/* 관리자 전용 네비게이션 */}
      <nav className="bg-gray-800 p-4 rounded-lg mb-6">
        <ul className="flex gap-6 font-semibold text-white">
          <li>
            <a href="/admin/register">상품 등록</a>
          </li>
          <li>
            <a href="/admin/items">전체 상품 조회</a>
          </li>
          <li>
            <a href="/admin/items/category">카테고리별 조회</a>
          </li>
          <li>
            <a href="/admin/items/single">단건 조회</a>
          </li>
          <li>
            <a href="/admin/edit/info">상품 정보 수정</a>
          </li>
          <li>
            <a href="/admin/edit/status">상품 상태 수정</a>
          </li>
          <li>
            <a href="/admin/delete">상품 삭제</a>
          </li>
        </ul>
      </nav>

      {/* 관리자 페이지의 메인 콘텐츠 */}
      <section className="bg-gray-800 p-6 rounded-lg shadow-lg">
        {children}
      </section>
    </div>
  );
}
