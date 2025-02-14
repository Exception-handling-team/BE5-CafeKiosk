// lib/tokenStorage.ts

/**
 * Access Token 저장
 */
export function setAccessToken(token: string) {
  if (typeof window !== "undefined") {
    localStorage.setItem("accessToken", token);
  }
}

/**
 * Access Token 가져오기
 */
export function getAccessToken(): string | null {
  if (typeof window !== "undefined") {
    return localStorage.getItem("accessToken");
  }
  return null;
}

/**
 * Access Token 제거
 */
export function removeAccessToken() {
  if (typeof window !== "undefined") {
    localStorage.removeItem("accessToken");
  }
}
