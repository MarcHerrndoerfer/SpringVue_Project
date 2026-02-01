export type MeResponse = {
  id: number;
  name: string;
  email: string;
  role: string;
};

const API_BASE = import.meta.env.VITE_API_URL ?? "http://localhost:8080";

type ErrorResponse = { message?: string };

async function handleResponse<T>(response: Response): Promise<T> {
  if (response.ok) {
    return (await response.json()) as T;
  }

  const fallbackMessage = `Request failed (${response.status} ${response.statusText})`;
  const contentType = response.headers.get("content-type") ?? "";

  if (contentType.includes("application/json")) {
    try {
      const data = (await response.json()) as ErrorResponse;
      throw new Error(data?.message || fallbackMessage);
    } catch {
      throw new Error(fallbackMessage);
    }
  }

  try {
    const text = await response.text();
    throw new Error(text || fallbackMessage);
  } catch {
    throw new Error(fallbackMessage);
  }
}

export async function login(payload: { email: string; password: string }): Promise<MeResponse> {
  const response = await fetch(`${API_BASE}/api/auth/login`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    credentials: "include",
    body: JSON.stringify(payload)
  });

  return handleResponse<MeResponse>(response);
}

export async function register(payload: {
  name: string;
  email: string;
  password: string;
  role: string;
}): Promise<MeResponse> {
  const response = await fetch(`${API_BASE}/api/auth/register`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    credentials: "include",
    body: JSON.stringify(payload)
  });

  return handleResponse<MeResponse>(response);
}

export async function me(): Promise<MeResponse> {
  const response = await fetch(`${API_BASE}/api/auth/me`, {
    credentials: "include"
  });

  return handleResponse<MeResponse>(response);
}

export async function logout(): Promise<void> {
  const response = await fetch(`${API_BASE}/api/auth/logout`, {
    method: "POST",
    credentials: "include"
  });

  if (!response.ok) {
    await handleResponse<void>(response);
    return;
  }
}
