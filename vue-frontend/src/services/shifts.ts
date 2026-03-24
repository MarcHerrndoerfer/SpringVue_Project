export interface Shift {
  id: number;
  department: string;
  title: string;
  startAt: string;
  endAt: string;
  requiredStaff: number;
  assignedCount: number;
  createdBy: string;
}

export interface Department {
  id: number;
  name: string;
}

export interface CreateShiftPayload {
  departmentId: number;
  title: string;
  startAt: string;
  endAt: string;
  requiredStaff: number;
}

export interface ShiftAssignment {
  id: number;
  shiftId: number;
  shiftTitle: string;
  department: string;
  startAt: string;
  endAt: string;
  userId: number;
  userName: string;
  userEmail: string;
  userRole: string;
  status: string;
  requestedAt: string;
  decidedAt: string | null;
  decidedByName: string | null;
}

const API_BASE = import.meta.env.VITE_API_URL ?? "http://localhost:8080";

export async function getAvailableShifts(): Promise<Shift[]> {
  const response = await fetch(`${API_BASE}/api/shifts/available`, {
    credentials: "include"
  });

  if (!response.ok) {
    throw new Error(`Failed to fetch shifts: ${response.statusText}`);
  }

  return await response.json();
}

export async function getDepartments(): Promise<Department[]> {
  const response = await fetch(`${API_BASE}/api/departments`, {
    credentials: "include"
  });

  if (!response.ok) {
    throw new Error(`Failed to fetch departments (${response.status})`);
  }

  return await response.json();
}

export async function createShift(payload: CreateShiftPayload): Promise<Shift> {
  const response = await fetch(`${API_BASE}/api/shifts`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    credentials: "include",
    body: JSON.stringify(payload)
  });

  if (!response.ok) {
    const contentType = response.headers.get("content-type") ?? "";

    if (contentType.includes("application/json")) {
      const data = (await response.json()) as { message?: string };
      throw new Error(data.message || `Failed to create shift (${response.status})`);
    }

    if (response.status === 401 || response.status === 403) {
      throw new Error("Not authorized. Please sign in again.");
    }

    throw new Error(`Failed to create shift (${response.status})`);
  }

  return await response.json();
}

export async function getShiftsByDepartment(deptId: number): Promise<Shift[]> {
  const response = await fetch(`${API_BASE}/api/shifts/available/department/${deptId}`, {
    credentials: "include"
  });

  if (!response.ok) {
    throw new Error(`Failed to fetch shifts: ${response.statusText}`);
  }

  return await response.json();
}

export async function requestShift(shiftId: number): Promise<void> {
  const response = await fetch(`${API_BASE}/api/shifts/${shiftId}/request`, {
    method: "POST",
    credentials: "include"
  });

  if (!response.ok) {
    const text = await response.text();
    throw new Error(text || "Failed to request shift");
  }
}

export async function cancelShiftRequest(shiftId: number): Promise<void> {
  const response = await fetch(`${API_BASE}/api/shifts/${shiftId}/cancel`, {
    method: "DELETE",
    credentials: "include"
  });

  if (!response.ok) {
    const text = await response.text();
    throw new Error(text || "Failed to cancel request");
  }
}

export async function getPendingAssignments(): Promise<ShiftAssignment[]> {
  const response = await fetch(`${API_BASE}/api/admin/shift-assignments/pending`, {
    credentials: "include"
  });

  if (!response.ok) {
    throw new Error(`Failed to fetch pending assignments: ${response.statusText}`);
  }

  return await response.json();
}

export async function getAllAssignments(): Promise<ShiftAssignment[]> {
  const response = await fetch(`${API_BASE}/api/admin/shift-assignments/all`, {
    credentials: "include"
  });

  if (!response.ok) {
    throw new Error(`Failed to fetch assignments: ${response.statusText}`);
  }

  return await response.json();
}

export async function getMyAssignments(): Promise<ShiftAssignment[]> {
  const response = await fetch(`${API_BASE}/api/shifts/my-assignments`, {
    credentials: "include"
  });

  if (!response.ok) {
    throw new Error(`Failed to fetch my assignments: ${response.statusText}`);
  }

  return await response.json();
}

export async function getAssignmentsByShift(shiftId: number): Promise<ShiftAssignment[]> {
  const response = await fetch(`${API_BASE}/api/admin/shift-assignments/shift/${shiftId}`, {
    credentials: "include"
  });

  if (!response.ok) {
    throw new Error(`Failed to fetch assignments: ${response.statusText}`);
  }

  return await response.json();
}

export async function approveAssignment(assignmentId: number): Promise<ShiftAssignment> {
  const response = await fetch(`${API_BASE}/api/admin/shift-assignments/${assignmentId}`, {
    method: "PATCH",
    headers: {
      "Content-Type": "application/json"
    },
    credentials: "include",
    body: JSON.stringify({ status: "APPROVED" })
  });

  if (!response.ok) {
    const text = await response.text();
    throw new Error(text || "Failed to approve assignment");
  }

  return await response.json();
}

export async function rejectAssignment(assignmentId: number): Promise<ShiftAssignment> {
  const response = await fetch(`${API_BASE}/api/admin/shift-assignments/${assignmentId}`, {
    method: "PATCH",
    headers: {
      "Content-Type": "application/json"
    },
    credentials: "include",
    body: JSON.stringify({ status: "REJECTED" })
  });

  if (!response.ok) {
    const text = await response.text();
    throw new Error(text || "Failed to reject assignment");
  }

  return await response.json();
}

export async function deleteAssignment(assignmentId: number): Promise<void> {
  const response = await fetch(`${API_BASE}/api/admin/shift-assignments/${assignmentId}`, {
    method: "DELETE",
    credentials: "include"
  });

  if (!response.ok) {
    const text = await response.text();
    throw new Error(text || "Failed to delete assignment");
  }
}
