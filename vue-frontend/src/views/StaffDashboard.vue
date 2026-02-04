<template>
  <div class="staff-page">
    <div class="container py-5">
    <div class="d-flex flex-column flex-lg-row align-items-lg-center justify-content-between gap-3 mb-4">
      <div>
        <h1 class="h3 mb-1">Staff Area</h1>
        <p class="text-muted mb-0">Overview of hospital wards and duty schedules</p>
        <div v-if="displayName" class="text-muted small mt-1">Signed in as {{ displayName }}</div>
      </div>
      <div class="d-flex align-items-center gap-2">
        <div class="badge text-bg-primary px-3 py-2">Staff only</div>
        <button class="btn btn-outline-secondary btn-sm" type="button" @click="handleLogout">
          Log out
        </button>
      </div>
    </div>

    <div v-if="error" class="alert alert-danger" role="alert">{{ error }}</div>
    <div v-else-if="loading" class="text-muted">Loading staff data...</div>

    <div v-else>
      <div class="hero-card mb-4">
        <div>
          <h2 class="h4 mb-1">Welcome back</h2>
          <p class="mb-0 text-muted">Here is a quick overview of today’s staffing and schedules.</p>
        </div>
        <div class="hero-pill">Updated just now</div>
      </div>

      <div class="row g-3 mb-4">
        <div class="col-12 col-md-6 col-lg-3" v-for="ward in wards" :key="ward.id">
          <div class="card h-100 shadow-sm border-0">
            <div class="card-body">
              <h2 class="h6 mb-1">{{ ward.name }}</h2>
              <div class="text-muted small">Ward ID {{ ward.id }}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="card shadow-sm border-0 mb-4">
        <div class="card-body">
          <div class="d-flex align-items-center justify-content-between mb-3">
            <h2 class="h6 mb-0">Duty schedules</h2>
            <span class="text-muted small">Grouped by department</span>
          </div>

          <div class="d-grid gap-3">
            <div
              v-for="group in groupedShifts"
              :key="group.name"
              class="schedule-section"
              :class="deptClass(group.name)"
            >
              <div class="schedule-header">
                <div>
                  <div class="schedule-title">{{ group.name }}</div>
                  <div class="text-muted small">{{ group.items.length }} shifts</div>
                </div>
                <span class="badge text-bg-light">{{ group.name }}</span>
              </div>

              <div class="table-responsive">
                <table class="table table-sm align-middle mb-0">
                  <thead>
                    <tr>
                      <th>Shift</th>
                      <th>Time</th>
                      <th>Required</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="shift in group.items" :key="shift.id">
                      <td class="fw-semibold">{{ shift.title }}</td>
                      <td>{{ formatRange(shift.startAt, shift.endAt) }}</td>
                      <td>{{ shift.requiredStaff }}</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="card shadow-sm border-0 mb-4">
        <div class="card-body">
          <div class="d-flex align-items-center justify-content-between mb-3">
            <h2 class="h6 mb-0">My shifts</h2>
            <span class="text-muted small">Assigned to you</span>
          </div>

          <div class="table-responsive">
            <table class="table table-sm align-middle">
              <thead>
                <tr>
                  <th>Ward</th>
                  <th>Shift</th>
                  <th>Time</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="entry in myShifts" :key="entry.id">
                  <td class="fw-semibold">{{ entry.ward }}</td>
                  <td>{{ entry.title }}</td>
                  <td>{{ formatRange(entry.startAt, entry.endAt) }}</td>
                  <td>
                    <span class="badge text-bg-light">{{ entry.status }}</span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <div class="row g-3 mb-4">
        <div class="col-12 col-lg-8">
          <div class="card shadow-sm border-0 h-100">
            <div class="card-body">
              <div class="d-flex align-items-center justify-content-between mb-3">
                <h2 class="h6 mb-0">Team availability</h2>
                <span class="text-muted small">This week</span>
              </div>

              <div class="table-responsive">
                <table class="table table-sm align-middle mb-0">
                  <thead>
                    <tr>
                      <th>Staff member</th>
                      <th>Role</th>
                      <th>Available days</th>
                      <th>Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="entry in availability" :key="entry.name">
                      <td class="fw-semibold">{{ entry.name }}</td>
                      <td>{{ entry.role }}</td>
                      <td>{{ entry.days.join(", ") }}</td>
                      <td>
                        <span class="badge" :class="statusBadge(entry.status)">
                          {{ formatStatus(entry.status) }}
                        </span>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
        <div class="col-12 col-lg-4">
          <div class="card shadow-sm border-0 h-100">
            <div class="card-body">
              <div class="d-flex align-items-center justify-content-between mb-3">
                <h2 class="h6 mb-0">Sick leave</h2>
                <span class="text-muted small">Current</span>
              </div>

              <div v-if="sickStaff.length === 0" class="text-muted small">
                No sick leave reported.
              </div>
              <ul v-else class="list-unstyled mb-0">
                <li v-for="entry in sickStaff" :key="entry.name" class="d-flex gap-2 mb-2">
                  <span class="status-dot bg-danger"></span>
                  <div>
                    <div class="fw-semibold">{{ entry.name }}</div>
                    <div class="text-muted small">
                      Out {{ formatDate(entry.startDate) }} – {{ formatDate(entry.endDate) }}
                    </div>
                    <div v-if="entry.note" class="text-muted small">{{ entry.note }}</div>
                  </div>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>

      <div class="card shadow-sm border-0 mb-4">
        <div class="card-body">
          <div class="d-flex align-items-center justify-content-between mb-3">
            <h2 class="h6 mb-0">Hospital utilization</h2>
            <span class="text-muted small">Today (sample)</span>
          </div>

          <div class="chart-grid">
            <div class="chart-y">
              <div>100%</div>
              <div>75%</div>
              <div>50%</div>
              <div>25%</div>
              <div>0%</div>
            </div>
            <svg class="chart" viewBox="0 0 100 60" preserveAspectRatio="none" aria-label="Utilization chart">
              <g>
                <line x1="0" y1="0" x2="100" y2="0" />
                <line x1="0" y1="15" x2="100" y2="15" />
                <line x1="0" y1="30" x2="100" y2="30" />
                <line x1="0" y1="45" x2="100" y2="45" />
                <line x1="0" y1="60" x2="100" y2="60" />
              </g>
              <g>
                <rect
                  v-for="(item, index) in utilization"
                  :key="item.label"
                  :x="index * 16 + 3"
                  :y="60 - item.value"
                  width="10"
                  :height="item.value"
                  rx="2"
                />
              </g>
            </svg>
            <div class="chart-x">
              <div v-for="item in utilization" :key="item.label">{{ item.label }}</div>
            </div>
          </div>

          <div class="chart-meta">
            <div class="chart-legend">
              <span class="legend-dot"></span>
              <span>Utilization (%)</span>
            </div>
            <div class="text-muted small">X-axis: Departments • Y-axis: Occupancy</div>
          </div>
        </div>
      </div>

      <div class="card shadow-sm border-0">
        <div class="card-body">
          <div class="d-flex align-items-center justify-content-between mb-3">
            <h2 class="h6 mb-0">My profile</h2>
            <span class="text-muted small">Update your staff details</span>
          </div>

          <div v-if="profileError" class="alert alert-danger" role="alert">{{ profileError }}</div>
          <div v-if="profileMessage" class="alert alert-success" role="alert">{{ profileMessage }}</div>

          <form class="row g-3" @submit.prevent="handleSaveProfile">
            <div class="col-12 col-md-6">
              <label class="form-label" for="profile-name">Name</label>
              <input
                id="profile-name"
                class="form-control"
                type="text"
                v-model="profileForm.name"
                required
              />
            </div>
            <div class="col-12 col-md-6">
              <label class="form-label" for="profile-phone">Phone</label>
              <input id="profile-phone" class="form-control" type="text" v-model="profileForm.phone" />
            </div>
            <div class="col-12 col-md-6">
              <label class="form-label" for="profile-department">Department</label>
              <select id="profile-department" class="form-select" v-model="profileForm.departmentId">
                <option value="">Select a department</option>
                <option v-for="ward in wards" :key="ward.id" :value="String(ward.id)">
                  {{ ward.name }}
                </option>
              </select>
            </div>
            <div class="col-12">
              <label class="form-label" for="profile-bio">Notes</label>
              <textarea
                id="profile-bio"
                class="form-control"
                rows="3"
                v-model="profileForm.bio"
              />
            </div>
            <div class="col-12">
              <button class="btn btn-primary" type="submit" :disabled="savingProfile">
                {{ savingProfile ? "Saving..." : "Save profile" }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue"
import { useRouter } from "vue-router"
import { logout, me, type MeResponse } from "../services/auth"
import {
  fetchAvailability,
  fetchMyShifts,
  fetchProfile,
  fetchShifts,
  fetchSickLeave,
  fetchWards,
  updateProfile,
  type AvailabilityEntry,
  type Department,
  type MyShift,
  type Shift,
  type SickLeaveEntry,
  type StaffProfile
} from "../services/staff"

const router = useRouter()

const wards = ref<Department[]>([])
const shifts = ref<Shift[]>([])
const myShifts = ref<MyShift[]>([])
const profile = ref<StaffProfile | null>(null)
const loading = ref(true)
const error = ref("")
const profileMessage = ref("")
const profileError = ref("")
const savingProfile = ref(false)
const availability = ref<AvailabilityEntry[]>([])
const sickStaff = ref<SickLeaveEntry[]>([])

const profileForm = ref({
  name: "",
  phone: "",
  departmentId: "",
  bio: ""
})

const authUser = ref<MeResponse | null>(null)

const displayName = computed(() => profile.value?.name ?? authUser.value?.name ?? "")

const groupedShifts = computed(() => {
  const map = new Map<string, Shift[]>()
  for (const shift of shifts.value) {
    const key = shift.departmentName
    if (!map.has(key)) map.set(key, [])
    map.get(key)?.push(shift)
  }

  return Array.from(map.entries()).map(([name, items]) => ({ name, items }))
})

const loadLocalUser = () => {
  const raw = localStorage.getItem("authUser")
  if (!raw) return
  try {
    authUser.value = JSON.parse(raw) as MeResponse
  } catch {
    authUser.value = null
  }
}

const formatRange = (startAt: string, endAt: string) => {
  const start = new Date(startAt)
  const end = new Date(endAt)
  const formatter = new Intl.DateTimeFormat("en-GB", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit"
  })
  return `${formatter.format(start)} – ${formatter.format(end)}`
}

const syncProfileForm = (data: StaffProfile) => {
  profileForm.value = {
    name: data.name ?? "",
    phone: data.phone ?? "",
    departmentId: data.departmentId ? String(data.departmentId) : "",
    bio: data.bio ?? ""
  }
}

onMounted(async () => {
  loadLocalUser()
  loading.value = true
  error.value = ""
  try {
    const fresh = await me()
    authUser.value = fresh
    localStorage.setItem("authUser", JSON.stringify(fresh))
  } catch {
    // ignore if not logged in
  }

  try {
    const [wardData, shiftData, myShiftData, profileData, availabilityData, sickData] = await Promise.all([
      fetchWards(),
      fetchShifts(),
      fetchMyShifts(),
      fetchProfile(),
      fetchAvailability(),
      fetchSickLeave()
    ])
    wards.value = wardData
    shifts.value = shiftData
    myShifts.value = myShiftData
    profile.value = profileData
    syncProfileForm(profileData)
    availability.value = availabilityData
    sickStaff.value = sickData
  } catch (err: any) {
    error.value = err?.message || "Failed to load staff data"
  } finally {
    loading.value = false
  }
})

const handleLogout = async () => {
  try {
    await logout()
  } finally {
    localStorage.removeItem("authUser")
    authUser.value = null
    router.push("/")
  }
}

const handleSaveProfile = async () => {
  if (!profileForm.value.name.trim()) return
  profileMessage.value = ""
  profileError.value = ""
  savingProfile.value = true
  try {
    const updated = await updateProfile({
      name: profileForm.value.name.trim(),
      phone: profileForm.value.phone?.trim() || null,
      departmentId: profileForm.value.departmentId
        ? Number(profileForm.value.departmentId)
        : null,
      bio: profileForm.value.bio?.trim() || null
    })
    profile.value = updated
    syncProfileForm(updated)
    profileMessage.value = "Profile updated successfully."
  } catch (err: any) {
    profileError.value = err?.message || "Failed to save profile"
  } finally {
    savingProfile.value = false
  }
}

const statusBadge = (status: string) => {
  switch (status) {
    case "AVAILABLE":
      return "text-bg-success"
    case "LIMITED":
      return "text-bg-warning"
    case "ON_LEAVE":
      return "text-bg-secondary"
    default:
      return "text-bg-light"
  }
}

const formatStatus = (status: string) => {
  switch (status) {
    case "AVAILABLE":
      return "Available"
    case "LIMITED":
      return "Limited"
    case "ON_LEAVE":
      return "On leave"
    default:
      return status
  }
}

const formatDate = (value: string) => {
  const date = new Date(value)
  return new Intl.DateTimeFormat("en-GB", {
    year: "numeric",
    month: "short",
    day: "2-digit"
  }).format(date)
}

const deptClass = (name: string) => {
  switch (name.toLowerCase()) {
    case "emergency":
      return "dept-emergency"
    case "icu":
      return "dept-icu"
    case "surgery":
      return "dept-surgery"
    case "pediatrics":
      return "dept-pediatrics"
    default:
      return "dept-default"
  }
}

const utilization = [
  { label: "ICU", value: 52 },
  { label: "ER", value: 44 },
  { label: "Surg", value: 36 },
  { label: "Peds", value: 28 },
  { label: "Ward", value: 40 }
]
</script>

<style scoped>
.staff-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #f6f8ff 0%, #f8fbff 40%, #ffffff 100%);
}

.hero-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  padding: 1.5rem;
  border-radius: 18px;
  background: linear-gradient(135deg, rgba(74, 108, 247, 0.12), rgba(74, 108, 247, 0.02));
  border: 1px solid rgba(74, 108, 247, 0.15);
}

.hero-pill {
  padding: 0.45rem 0.8rem;
  border-radius: 999px;
  font-size: 0.8rem;
  font-weight: 600;
  background: #4a6cf7;
  color: #fff;
}

.status-dot {
  display: inline-block;
  width: 10px;
  height: 10px;
  margin-top: 0.35rem;
  border-radius: 999px;
}

.card {
  border-radius: 16px;
}

.table thead th {
  color: #6c757d;
  font-weight: 600;
}

.chart-grid {
  display: grid;
  grid-template-columns: auto 1fr;
  grid-template-rows: 1fr auto;
  gap: 0.5rem 1rem;
  align-items: end;
}

.chart {
  width: 100%;
  height: 180px;
}

.chart line {
  stroke: rgba(74, 108, 247, 0.12);
  stroke-width: 0.6;
}

.chart rect {
  fill: #4a6cf7;
  opacity: 0.85;
}

.chart-y {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 180px;
  font-size: 0.75rem;
  color: #6c757d;
}

.chart-x {
  grid-column: 2 / 3;
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  font-size: 0.75rem;
  color: #6c757d;
  text-align: center;
}

.chart-meta {
  margin-top: 0.75rem;
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  align-items: center;
}

.chart-legend {
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  font-size: 0.8rem;
  color: #4a6cf7;
  font-weight: 600;
}

.legend-dot {
  width: 10px;
  height: 10px;
  border-radius: 999px;
  background: #4a6cf7;
}

.schedule-section {
  border-radius: 16px;
  padding: 1rem;
  border: 1px solid rgba(15, 23, 42, 0.08);
  background: #fff;
}

.schedule-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 0.75rem;
}

.schedule-title {
  font-weight: 600;
}

.dept-emergency {
  border-left: 6px solid #ef4444;
  background: rgba(239, 68, 68, 0.06);
}

.dept-icu {
  border-left: 6px solid #6366f1;
  background: rgba(99, 102, 241, 0.08);
}

.dept-surgery {
  border-left: 6px solid #0ea5e9;
  background: rgba(14, 165, 233, 0.08);
}

.dept-pediatrics {
  border-left: 6px solid #22c55e;
  background: rgba(34, 197, 94, 0.08);
}

.dept-default {
  border-left: 6px solid #94a3b8;
  background: rgba(148, 163, 184, 0.08);
}
</style>
