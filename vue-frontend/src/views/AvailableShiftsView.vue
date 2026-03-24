<template>
  <div class="container py-5">
    <StaffNavbar class="mb-4" />

    <div class="d-flex flex-column flex-lg-row align-items-lg-center justify-content-between gap-3 mb-4">
      <div>
        <h1 class="h3 mb-1">Available Shifts</h1>
        <p class="text-muted mb-0">Browse and apply for open positions</p>
      </div>
      <router-link to="/shifts/new" class="btn btn-success btn-sm">Create Shift</router-link>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="alert alert-info">
      <div class="spinner-border spinner-border-sm me-2" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
      Loading shifts...
    </div>

    <!-- Error State -->
    <div v-if="error" class="alert alert-danger alert-dismissible fade show" role="alert">
      {{ error }}
      <button type="button" class="btn-close" @click="error = null"></button>
    </div>

    <!-- Success Message -->
    <div v-if="successMessage" class="alert alert-success alert-dismissible fade show" role="alert">
      {{ successMessage }}
      <button type="button" class="btn-close" @click="successMessage = null"></button>
    </div>

    <!-- Filters -->
    <div class="card mb-4 shadow-sm border-0" v-if="!loading">
      <div class="card-body">
        <div class="row g-3">
          <div class="col-12 col-md-6">
            <label class="form-label small fw-semibold">Department</label>
            <select v-model="selectedDepartmentId" class="form-select form-select-sm" @change="filterShifts">
              <option value="">All Departments</option>
              <option value="1">Emergency</option>
              <option value="2">ICU</option>
              <option value="3">Surgery</option>
              <option value="4">Pediatrics</option>
            </select>
          </div>
          <div class="col-12 col-md-6">
            <label class="form-label small fw-semibold">Sort</label>
            <select v-model="sortBy" class="form-select form-select-sm" @change="sortShifts">
              <option value="earliest">Earliest First</option>
              <option value="latest">Latest First</option>
              <option value="available">Most Available</option>
            </select>
          </div>
        </div>
      </div>
    </div>

    <!-- Shifts List -->
    <div v-if="!loading && filteredShifts.length === 0" class="alert alert-warning">
      No shifts available at the moment.
    </div>

    <div v-if="!loading" class="row g-3">
      <div class="col-12 col-lg-6" v-for="shift in filteredShifts" :key="shift.id">
        <div class="card h-100 shadow-sm border-0">
          <div class="card-body">
            <!-- Header -->
            <div class="d-flex align-items-start justify-content-between mb-3">
              <div>
                <h5 class="card-title mb-1">{{ shift.title }}</h5>
                <p class="text-muted small mb-0">{{ shift.department }}</p>
              </div>
              <span class="badge text-bg-primary">{{ shift.assignedCount }}/{{ shift.requiredStaff }}</span>
            </div>

            <!-- Time -->
            <div class="mb-3">
              <div class="small">
                <span class="fw-semibold">📅 Date:</span> {{ formatDate(shift.startAt) }}
              </div>
              <div class="small">
                <span class="fw-semibold">⏰ Time:</span> {{ formatTime(shift.startAt) }} – {{ formatTime(shift.endAt) }}
              </div>
            </div>

            <!-- Status -->
            <div class="mb-3">
              <div class="progress" style="height: 20px">
                <div
                  class="progress-bar"
                  :class="shift.assignedCount >= shift.requiredStaff ? 'bg-danger' : 'bg-success'"
                  :style="{ width: Math.min(100, (shift.assignedCount / shift.requiredStaff) * 100) + '%' }"
                >
                  {{ shift.assignedCount >= shift.requiredStaff ? "Full" : "Open" }}
                </div>
              </div>
            </div>

            <!-- Action Button -->
            <button
              v-if="!shift.requested"
              @click="requestShift(shift)"
              :disabled="shift.assignedCount >= shift.requiredStaff || requestingShiftId === shift.id"
              class="btn btn-sm btn-primary w-100"
            >
              <span v-if="requestingShiftId === shift.id" class="spinner-border spinner-border-sm me-2"></span>
              {{ requestingShiftId === shift.id ? "Requesting..." : "Request Shift" }}
            </button>
            <button
              v-else
              @click="cancelRequest(shift)"
              :disabled="cancelingShiftId === shift.id"
              class="btn btn-sm btn-outline-danger w-100"
            >
              <span v-if="cancelingShiftId === shift.id" class="spinner-border spinner-border-sm me-2"></span>
              {{ cancelingShiftId === shift.id ? "Canceling..." : "Cancel Request" }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, computed } from "vue";
import { getAvailableShifts, requestShift as requestShiftAPI, cancelShiftRequest, type Shift } from "../services/shifts";
import StaffNavbar from "../components/StaffNavbar.vue";

const shifts = ref<(Shift & { requested?: boolean })[]>([]);
const loading = ref(true);
const error = ref<string | null>(null);
const successMessage = ref<string | null>(null);
const selectedDepartmentId = ref("");
const sortBy = ref("earliest");
const requestingShiftId = ref<number | null>(null);
const cancelingShiftId = ref<number | null>(null);

// Get user's requests from localStorage
const getUserRequests = (): number[] => {
  const data = localStorage.getItem("userShiftRequests");
  return data ? JSON.parse(data) : [];
};

const setUserRequests = (requests: number[]) => {
  localStorage.setItem("userShiftRequests", JSON.stringify(requests));
};

const filteredShifts = computed(() => {
  let filtered = [...shifts.value];

  if (selectedDepartmentId.value) {
    filtered = filtered.filter(s => {
      const deptMap: { [key: string]: string } = {
        "1": "Emergency",
        "2": "ICU",
        "3": "Surgery",
        "4": "Pediatrics"
      };
      return s.department === deptMap[selectedDepartmentId.value];
    });
  }

  if (sortBy.value === "latest") {
    filtered.sort((a, b) => new Date(b.startAt).getTime() - new Date(a.startAt).getTime());
  } else if (sortBy.value === "available") {
    filtered.sort((a, b) => (b.requiredStaff - b.assignedCount) - (a.requiredStaff - a.assignedCount));
  } else {
    filtered.sort((a, b) => new Date(a.startAt).getTime() - new Date(b.startAt).getTime());
  }

  return filtered;
});

const loadShifts = async () => {
  try {
    loading.value = true;
    error.value = null;
    const data = await getAvailableShifts();
    const userRequests = getUserRequests();
    shifts.value = data.map(shift => ({
      ...shift,
      requested: userRequests.includes(shift.id)
    }));
  } catch (err) {
    error.value = err instanceof Error ? err.message : "Failed to load shifts";
  } finally {
    loading.value = false;
  }
};

const filterShifts = () => {
  // Already handled by computed property
};

const sortShifts = () => {
  // Already handled by computed property
};

const requestShift = async (shift: Shift) => {
  try {
    requestingShiftId.value = shift.id;
    await requestShiftAPI(shift.id);
    
    // Update local state
    const shiftIdx = shifts.value.findIndex(s => s.id === shift.id);
    if (shiftIdx >= 0) {
      const targetShift = shifts.value[shiftIdx];
      if (targetShift) {
        targetShift.requested = true;
      }
    }

    // Update localStorage
    const requests = getUserRequests();
    if (!requests.includes(shift.id)) {
      requests.push(shift.id);
      setUserRequests(requests);
    }

    successMessage.value = `Request submitted for ${shift.title}`;
    setTimeout(() => (successMessage.value = null), 3000);
  } catch (err) {
    error.value = err instanceof Error ? err.message : "Failed to request shift";
  } finally {
    requestingShiftId.value = null;
  }
};

const cancelRequest = async (shift: Shift) => {
  try {
    cancelingShiftId.value = shift.id;
    await cancelShiftRequest(shift.id);

    // Update local state
    const shiftIdx = shifts.value.findIndex(s => s.id === shift.id);
    if (shiftIdx >= 0) {
      const targetShift = shifts.value[shiftIdx];
      if (targetShift) {
        targetShift.requested = false;
      }
    }

    // Update localStorage
    const requests = getUserRequests().filter(id => id !== shift.id);
    setUserRequests(requests);

    successMessage.value = `Request canceled for ${shift.title}`;
    setTimeout(() => (successMessage.value = null), 3000);
  } catch (err) {
    error.value = err instanceof Error ? err.message : "Failed to cancel request";
  } finally {
    cancelingShiftId.value = null;
  }
};

const formatDate = (dateStr: string) => {
  const date = new Date(dateStr);
  return date.toLocaleDateString("de-DE", { weekday: "short", year: "numeric", month: "short", day: "numeric" });
};

const formatTime = (dateStr: string) => {
  const date = new Date(dateStr);
  return date.toLocaleTimeString("de-DE", { hour: "2-digit", minute: "2-digit" });
};

onMounted(loadShifts);
</script>
