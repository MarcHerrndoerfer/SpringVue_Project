<template>
  <div class="container py-5">
    <StaffNavbar class="mb-4" />

    <div class="d-flex flex-column flex-lg-row align-items-lg-center justify-content-between gap-3 mb-4">
      <div>
        <h1 class="h3 mb-1">Manage Shift Assignments</h1>
        <p class="text-muted mb-0">Review and approve/reject shift requests from staff</p>
      </div>
      <div>
        <button
          @click="refreshData"
          :disabled="loading"
          class="btn btn-outline-secondary btn-sm"
        >
          <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
          Refresh
        </button>
      </div>
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

    <!-- Tabs -->
    <ul class="nav nav-tabs mb-4">
      <li class="nav-item">
        <button
          @click="activeTab = 'pending'"
          :class="['nav-link', { active: activeTab === 'pending' }]"
        >
          Pending ({{ pendingCount }})
        </button>
      </li>
      <li class="nav-item">
        <button
          @click="activeTab = 'all'"
          :class="['nav-link', { active: activeTab === 'all' }]"
        >
          All
        </button>
      </li>
    </ul>

    <!-- Loading State -->
    <div v-if="loading && assignments.length === 0" class="alert alert-info">
      <div class="spinner-border spinner-border-sm me-2" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
      Loading assignments...
    </div>

    <!-- Empty State -->
    <div v-if="!loading && filteredAssignments.length === 0" class="alert alert-info">
      No {{ activeTab === 'pending' ? 'pending' : '' }} assignments found.
    </div>

    <!-- Assignments List -->
    <div v-if="!loading" class="row g-3">
      <div class="col-12" v-for="assignment in filteredAssignments" :key="assignment.id">
        <div class="card shadow-sm border-0">
          <div class="card-body">
            <div class="row align-items-start">
              <!-- Left: Shift & User Info -->
              <div class="col-12 col-lg-8 mb-3 mb-lg-0">
                <div class="mb-3">
                  <h5 class="card-title mb-1">{{ assignment.shiftTitle }}</h5>
                  <p class="text-muted small mb-2">{{ assignment.department }}</p>
                  <div class="small">
                    <strong>📅 Date:</strong> {{ formatDate(assignment.startAt) }}<br />
                    <strong>⏰ Time:</strong> {{ formatTime(assignment.startAt) }} – {{ formatTime(assignment.endAt) }}
                  </div>
                </div>

                <hr class="my-3" />

                <div>
                  <p class="mb-2">
                    <strong>Staff:</strong>
                    <span class="badge text-bg-primary ms-2">{{ assignment.userRole }}</span>
                  </p>
                  <p class="mb-2 small">
                    <strong>{{ assignment.userName }}</strong><br />
                    {{ assignment.userEmail }}
                  </p>
                  <p class="small text-muted mb-0">
                    Requested: {{ formatDateTime(assignment.requestedAt) }}
                  </p>
                </div>
              </div>

              <!-- Right: Status & Actions -->
              <div class="col-12 col-lg-4">
                <div class="mb-3">
                  <span
                    :class="[
                      'badge',
                      {
                        'text-bg-warning': assignment.status === 'PENDING',
                        'text-bg-success': assignment.status === 'APPROVED',
                        'text-bg-danger': assignment.status === 'REJECTED'
                      }
                    ]"
                  >
                    {{ assignment.status }}
                  </span>
                  <div v-if="assignment.decidedAt" class="small text-muted mt-2">
                    Decided: {{ formatDateTime(assignment.decidedAt) }}<br />
                    By: {{ assignment.decidedByName }}
                  </div>
                </div>

                <!-- Action Buttons -->
                <div v-if="assignment.status === 'PENDING'" class="d-grid gap-2">
                  <button
                    @click="approve(assignment)"
                    :disabled="processingId === assignment.id"
                    class="btn btn-sm btn-success"
                  >
                    <span v-if="processingId === assignment.id" class="spinner-border spinner-border-sm me-2"></span>
                    {{ processingId === assignment.id ? "Approving..." : "✓ Approve" }}
                  </button>
                  <button
                    @click="reject(assignment)"
                    :disabled="processingId === assignment.id"
                    class="btn btn-sm btn-danger"
                  >
                    <span v-if="processingId === assignment.id" class="spinner-border spinner-border-sm me-2"></span>
                    {{ processingId === assignment.id ? "Rejecting..." : "✗ Reject" }}
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, computed } from "vue";
import {
  getPendingAssignments,
  getAllAssignments,
  approveAssignment,
  rejectAssignment,
  type ShiftAssignment
} from "../services/shifts";
import StaffNavbar from "../components/StaffNavbar.vue";

const assignments = ref<ShiftAssignment[]>([]);
const loading = ref(true);
const error = ref<string | null>(null);
const successMessage = ref<string | null>(null);
const activeTab = ref<"pending" | "all">("pending");
const processingId = ref<number | null>(null);

const filteredAssignments = computed(() => {
  if (activeTab.value === "pending") {
    return assignments.value.filter(a => a.status === "PENDING");
  }
  return assignments.value;
});

const pendingCount = computed(() => {
  return assignments.value.filter(a => a.status === "PENDING").length;
});

const loadAssignments = async () => {
  try {
    loading.value = true;
    error.value = null;

    if (activeTab.value === "pending") {
      assignments.value = await getPendingAssignments();
    } else {
      assignments.value = await getAllAssignments();
    }
  } catch (err) {
    error.value = err instanceof Error ? err.message : "Failed to load assignments";
  } finally {
    loading.value = false;
  }
};

const refreshData = async () => {
  await loadAssignments();
};

const approve = async (assignment: ShiftAssignment) => {
  try {
    processingId.value = assignment.id;
    const updated = await approveAssignment(assignment.id);

    // Update local state
    const idx = assignments.value.findIndex(a => a.id === assignment.id);
    if (idx >= 0) {
      assignments.value[idx] = updated;
    }

    successMessage.value = `Approved ${assignment.userName} for ${assignment.shiftTitle}`;
    setTimeout(() => (successMessage.value = null), 3000);
  } catch (err) {
    error.value = err instanceof Error ? err.message : "Failed to approve assignment";
  } finally {
    processingId.value = null;
  }
};

const reject = async (assignment: ShiftAssignment) => {
  try {
    processingId.value = assignment.id;
    const updated = await rejectAssignment(assignment.id);

    // Update local state
    const idx = assignments.value.findIndex(a => a.id === assignment.id);
    if (idx >= 0) {
      assignments.value[idx] = updated;
    }

    successMessage.value = `Rejected ${assignment.userName} for ${assignment.shiftTitle}`;
    setTimeout(() => (successMessage.value = null), 3000);
  } catch (err) {
    error.value = err instanceof Error ? err.message : "Failed to reject assignment";
  } finally {
    processingId.value = null;
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

const formatDateTime = (dateStr: string) => {
  const date = new Date(dateStr);
  return date.toLocaleDateString("de-DE", { year: "numeric", month: "2-digit", day: "2-digit", hour: "2-digit", minute: "2-digit" });
};

onMounted(loadAssignments);
</script>
