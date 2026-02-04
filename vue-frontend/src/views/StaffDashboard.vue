<template>
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

    <div class="card shadow-sm border-0">
      <div class="card-body">
        <div class="d-flex align-items-center justify-content-between mb-3">
          <h2 class="h6 mb-0">Duty schedules (sample)</h2>
          <span class="text-muted small">updated today</span>
        </div>

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
                <td class="fw-semibold">{{ plan.ward }}</td>
                <td>{{ plan.title }}</td>
                <td>{{ plan.time }}</td>
                <td>{{ plan.required }}</td>
                <td>
                  <span class="badge" :class="plan.badgeClass">{{ plan.status }}</span>
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

const router = useRouter()

const wards = [
  { name: "Emergency", floor: "Ground", description: "Acute care & triage", lead: "Dr. Malik" },
  { name: "ICU", floor: "1st", description: "24/7 monitoring", lead: "Dr. Fischer" },
  { name: "Surgery", floor: "2nd", description: "OR & recovery", lead: "Dr. Klein" },
  { name: "Pediatrics", floor: "3rd", description: "Child & youth care", lead: "Dr. Vogt" }
]

const schedules = [
  {
    id: 1,
    ward: "Emergency",
    title: "Early Shift",
    time: "06:00 – 14:00",
    required: "2 doctors, 4 nurses",
    status: "open",
    badgeClass: "text-bg-warning"
  },
  {
    id: 2,
    ward: "ICU",
    title: "Late Shift",
    time: "14:00 – 22:00",
    required: "1 doctor, 3 nurses",
    status: "filled",
    badgeClass: "text-bg-success"
  },
  {
    id: 3,
    ward: "Surgery",
    title: "Night Shift",
    time: "22:00 – 06:00",
    required: "1 doctor, 2 nurses",
    status: "open",
    badgeClass: "text-bg-warning"
  }
]

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
</script>
