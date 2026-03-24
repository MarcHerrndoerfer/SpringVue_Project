<template>
  <div class="container py-5 staff-page">
    <StaffNavbar class="mb-4" />

    <div class="d-flex flex-column flex-lg-row align-items-lg-center justify-content-between gap-3 mb-4">
      <div>
        <h1 class="h3 mb-1">Staff Area</h1>
        <p class="text-muted mb-0">Overview of hospital wards and duty schedules</p>
        <div v-if="displayName" class="text-muted small mt-1">Signed in as {{ displayName }}</div>
      </div>
      <div class="d-flex align-items-center gap-2">
        <div class="badge text-bg-primary px-3 py-2">Staff only</div>
        <router-link v-if="authUser?.role === 'ADMIN'" to="/admin/assignments" class="btn btn-warning btn-sm">
          Admin Panel
        </router-link>
        <button class="btn btn-outline-secondary btn-sm" type="button" @click="handleLogout">
          Log out
        </button>
      </div>
    </div>

    <div class="row g-3 mb-4">
      <div class="col-12 col-md-4">
        <div class="card h-100">
          <div class="card-body d-flex align-items-center justify-content-between">
            <div>
              <div class="text-muted small">Open Shifts</div>
              <div class="fs-4 fw-bold">{{ openShiftsCount }}</div>
            </div>
            <span class="kpi-icon">📅</span>
          </div>
        </div>
      </div>

      <div class="col-12 col-md-4">
        <div class="card h-100">
          <div class="card-body d-flex align-items-center justify-content-between">
            <div>
              <div class="text-muted small">My Requests</div>
              <div class="fs-4 fw-bold">{{ myRequestsCount }}</div>
            </div>
            <span class="kpi-icon">🧾</span>
          </div>
        </div>
      </div>

      <div class="col-12 col-md-4">
        <div class="card h-100">
          <div class="card-body d-flex align-items-center justify-content-between">
            <div>
              <div class="text-muted small">Pending Approvals</div>
              <div class="fs-4 fw-bold">{{ pendingApprovalsCount }}</div>
            </div>
            <span class="kpi-icon">⏳</span>
          </div>
        </div>
      </div>
    </div>

    <div class="row g-3 mb-4">
      <div class="col-12 col-md-6 col-lg-3" v-for="ward in wards" :key="ward.name">
        <div class="card h-100 shadow-sm border-0">
          <div class="card-body">
            <div class="d-flex align-items-center justify-content-between mb-2">
              <h2 class="h6 mb-0">{{ ward.name }}</h2>
              <span class="badge text-bg-light">{{ ward.floor }}</span>
            </div>
            <p class="text-muted small mb-3">{{ ward.description }}</p>
            <div class="d-flex align-items-center justify-content-between small">
              <span class="text-muted">Lead</span>
              <span class="fw-semibold">{{ ward.lead }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="row g-3 mb-4">
      <div class="col-12 col-md-6">
        <router-link to="/shifts" class="btn btn-primary btn-lg w-100">
          <span class="h6 mb-0">Browse Available Shifts</span>
        </router-link>
      </div>
      <div class="col-12 col-md-6">
        <router-link to="/my-shifts" class="btn btn-outline-primary btn-lg w-100">
          <span class="h6 mb-0">My Shift Requests</span>
        </router-link>
      </div>
      <div class="col-12">
        <router-link to="/shifts/new" class="btn btn-success btn-lg w-100">
          <span class="h6 mb-0">Create New Shift</span>
        </router-link>
      </div>
    </div>

    <div class="card shadow-sm border-0">
      <div class="card-body">
        <div class="d-flex align-items-center justify-content-between mb-3">
          <h2 class="h6 mb-0">Duty schedules</h2>
          <span class="text-muted small">live from system</span>
        </div>

        <div v-if="schedulesLoading" class="alert alert-info py-2 mb-3">Loading shifts...</div>
        <div v-else-if="schedulesError" class="alert alert-danger py-2 mb-3">{{ schedulesError }}</div>
        <div v-else-if="schedules.length === 0" class="alert alert-warning py-2 mb-3">No shifts available yet.</div>

        <div class="table-responsive">
          <table class="table table-sm align-middle">
            <thead>
              <tr>
                <th>Ward</th>
                <th>Shift</th>
                <th>Time</th>
                <th>Required</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="plan in schedules" :key="plan.id">
                <td class="fw-semibold">{{ plan.department }}</td>
                <td>{{ plan.title }}</td>
                <td>{{ formatTimeRange(plan.startAt, plan.endAt) }}</td>
                <td>{{ plan.assignedCount }}/{{ plan.requiredStaff }}</td>
                <td>
                  <span class="badge" :class="statusBadgeClass(plan)">{{ statusText(plan) }}</span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue"
import { useRouter } from "vue-router"
import { logout, me, type MeResponse } from "../services/auth"
import { getAvailableShifts, getMyAssignments, getPendingAssignments, type Shift } from "../services/shifts"
import StaffNavbar from "../components/StaffNavbar.vue"

const router = useRouter()

const wards = [
  { name: "Emergency", floor: "Ground", description: "Acute care & triage", lead: "Dr. Malik" },
  { name: "ICU", floor: "1st", description: "24/7 monitoring", lead: "Dr. Fischer" },
  { name: "Surgery", floor: "2nd", description: "OR & recovery", lead: "Dr. Klein" },
  { name: "Pediatrics", floor: "3rd", description: "Child & youth care", lead: "Dr. Vogt" }
]

const schedules = ref<Shift[]>([])
const schedulesLoading = ref(false)
const schedulesError = ref("")

const openShiftsCount = ref(0)
const myRequestsCount = ref(0)
const pendingApprovalsCount = ref(0)

const authUser = ref<MeResponse | null>(null)

const displayName = computed(() => authUser.value?.name ?? "")

const loadLocalUser = () => {
  const raw = localStorage.getItem("authUser")
  if (!raw) return
  try {
    authUser.value = JSON.parse(raw) as MeResponse
  } catch {
    authUser.value = null
  }
}

onMounted(async () => {
  loadLocalUser()
  try {
    const fresh = await me()
    authUser.value = fresh
    localStorage.setItem("authUser", JSON.stringify(fresh))
  } catch {
    // ignore if not logged in
  }

  await loadSchedules()
  await loadKpis()
})

const loadSchedules = async () => {
  schedulesLoading.value = true
  schedulesError.value = ""
  try {
    schedules.value = await getAvailableShifts()
  } catch (err) {
    schedulesError.value = err instanceof Error ? err.message : "Could not load shifts"
  } finally {
    schedulesLoading.value = false
  }
}

const loadKpis = async () => {
  try {
    const shifts = await getAvailableShifts()
    openShiftsCount.value = shifts.filter(shift => shift.assignedCount < shift.requiredStaff).length
  } catch {
    openShiftsCount.value = 0
  }

  try {
    const myAssignments = await getMyAssignments()
    myRequestsCount.value = myAssignments.length
  } catch {
    myRequestsCount.value = 0
  }

  if (authUser.value?.role === "ADMIN") {
    try {
      const pending = await getPendingAssignments()
      pendingApprovalsCount.value = pending.length
    } catch {
      pendingApprovalsCount.value = 0
    }
  } else {
    pendingApprovalsCount.value = 0
  }
}

const formatTimeRange = (startAt: string, endAt: string) => {
  const start = new Date(startAt)
  const end = new Date(endAt)
  const startDate = start.toLocaleDateString("de-DE")
  const startTime = start.toLocaleTimeString("de-DE", { hour: "2-digit", minute: "2-digit" })
  const endTime = end.toLocaleTimeString("de-DE", { hour: "2-digit", minute: "2-digit" })
  return `${startDate} ${startTime} – ${endTime}`
}

const statusText = (shift: Shift) => {
  return shift.assignedCount >= shift.requiredStaff ? "filled" : "open"
}

const statusBadgeClass = (shift: Shift) => {
  return shift.assignedCount >= shift.requiredStaff ? "text-bg-success" : "text-bg-warning"
}

const handleLogout = async () => {
  try {
    await logout()
  } finally {
    localStorage.removeItem("authUser")
    authUser.value = null
    router.push("/")
  }
}
</script>
