"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { AuthProvider, useAuth } from "../context/AuthContext";

function Header() {
  const { accessToken, setAccessToken } = useAuth();
  const router = useRouter();

  const handleLogout = () => {
    setAccessToken(null);
    router.push("/");
  };

  return (
    <header className="bg-amber-50 shadow-md border-b border-gray-200 flex items-center px-6 py-4">
      <div className="flex items-center space-x-6">
        <Link
          href="/"
          className="text-xl font-semibold text-gray-800 hover:text-gray-900"
        >
          MAIN
        </Link>
        <Link
          href="/about"
          className="text-xl font-semibold text-gray-800 hover:text-gray-900"
        >
          INFO
        </Link>
        <Link
          href="/menu"
          className="text-xl font-semibold text-gray-800 hover:text-gray-900"
        >
          MENU
        </Link>
      </div>
      <div className="ml-auto flex space-x-4">
        {accessToken ? (
          <>
            <Link href="/admin">
              <button className="bg-indigo-500 text-white px-4 py-2 rounded-lg text-lg hover:bg-indigo-600">
                관리자 페이지
              </button>
            </Link>
            <Link href="/someProtectedPage">
              <button className="bg-amber-500 text-white px-4 py-2 rounded-lg text-lg hover:bg-amber-600">
                보호된 페이지
              </button>
            </Link>
            <button
              onClick={handleLogout}
              className="bg-red-500 text-white px-4 py-2 rounded-lg text-lg hover:bg-red-600"
            >
              로그아웃
            </button>
          </>
        ) : (
          <>
            <Link href="/signup">
              <button className="bg-green-500 text-white px-4 py-2 rounded-lg text-lg hover:bg-green-600">
                회원가입
              </button>
            </Link>
            <Link href="/login">
              <button className="bg-blue-500 text-white px-4 py-2 rounded-lg text-lg hover:bg-blue-600">
                로그인
              </button>
            </Link>
          </>
        )}
      </div>
    </header>
  );
}

export default function ClientLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <AuthProvider>
      <Header />
      <main className="min-h-screen bg-white p-6">{children}</main>
      <footer className="bg-amber-50 p-4 text-center text-gray-600 border-t border-gray-200">
        Cafe Kiosk © {new Date().getFullYear()}
      </footer>
    </AuthProvider>
  );
}
