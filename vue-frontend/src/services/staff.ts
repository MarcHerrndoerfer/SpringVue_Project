export type Department = {
  id: number;
  name: string;
};

export type Shift = {
  id: number;
  departmentId: number;
  departmentName: string;
  title: string;
  startAt: string;
  endAt: string;
  requiredStaff: number;
};

export type MyShift = {
  id: number;
  shiftId: number;
  ward: string;
  title: string;
  startAt: string;
  endAt: string;
  status: string;
};

export type StaffProfile = {
  id: number;
  name: string;
  email: string;
  role: string;
  phone?: string | null;
  departmentId?: number | null;
  departmentName?: string | null;
  bio?: string | null;
};

export type StaffProfileUpdate = {
  name?: string | null;
  phone?: string | null;
  departmentId?: number | null;
  bio?: string | null;
};

export type AvailabilityEntry = {
  name: string;
  role: string;
  days: string[];
  status: string;
};

export type SickLeaveEntry = {
  name: string;
  role: string;
  startDate: string;
  endDate: string;
  note?: string | null;
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

export async function fetchWards(): Promise<Department[]> {
  const response = await fetch(`${API_BASE}/api/staff/wards`, {
    credentials: "include"
  });

  return handleResponse<Department[]>(response);
}

export async function fetchShifts(departmentId?: number): Promise<Shift[]> {
  const params = departmentId ? `?departmentId=${departmentId}` : "";
  const response = await fetch(`${API_BASE}/api/staff/shifts${params}`, {
    credentials: "include"
  });

  return handleResponse<Shift[]>(response);
}

export async function fetchMyShifts(): Promise<MyShift[]> {
  const response = await fetch(`${API_BASE}/api/staff/my-shifts`, {
    credentials: "include"
  });

  return handleResponse<MyShift[]>(response);
}

export async function fetchProfile(): Promise<StaffProfile> {
  const response = await fetch(`${API_BASE}/api/staff/profile`, {
    credentials: "include"
  });

  return handleResponse<StaffProfile>(response);
}

export async function updateProfile(payload: StaffProfileUpdate): Promise<StaffProfile> {
  const response = await fetch(`${API_BASE}/api/staff/profile`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json"
    },
    credentials: "include",
    body: JSON.stringify(payload)
  });

  return handleResponse<StaffProfile>(response);
}

export async function fetchAvailability(): Promise<AvailabilityEntry[]> {
  const response = await fetch(`${API_BASE}/api/staff/availability`, {
    credentials: "include"
  });

  return handleResponse<AvailabilityEntry[]>(response);
}

export async function fetchSickLeave(): Promise<SickLeaveEntry[]> {
  const response = await fetch(`${API_BASE}/api/staff/sick`, {
    credentials: "include"
  });

  return handleResponse<SickLeaveEntry[]>(response);
}
