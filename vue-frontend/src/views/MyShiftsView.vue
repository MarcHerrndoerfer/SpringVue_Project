<template>
  <div class="container py-5">
    <StaffNavbar class="mb-4" />

    <div class="d-flex flex-column flex-lg-row align-items-lg-center justify-content-between gap-3 mb-4">
      <div>
        <h1 class="h3 mb-1">My Shift Requests</h1>
        <p class="text-muted mb-0">Your requested, approved and rejected shifts</p>
      </div>
      <button class="btn btn-outline-secondary btn-sm" type="button" @click="loadAssignments" :disabled="loading">
        Refresh
      </button>
    </div>

    <div v-if="error" class="alert alert-danger">{{ error }}</div>
    <div v-if="successMessage" class="alert alert-success">{{ successMessage }}</div>

    <div v-if="loading" class="alert alert-info">Loading your shifts...</div>

    <div v-else-if="assignments.length === 0" class="alert alert-warning">
      You have no shift requests yet.
    </div>

    <div v-else class="card shadow-sm border-0">
      <div class="card-body">
        <div class="table-responsive">
          <table class="table table-sm align-middle">
            <thead>
              <tr>
                <th>Department</th>
                <th>Shift</th>
                <th>Time</th>
                <th>Status</th>
                <th>Requested</th>
                <th class="text-end">Action</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="assignment in assignments" :key="assignment.id">
                <td>{{ assignment.department }}</td>
                <td class="fw-semibold">{{ assignment.shiftTitle }}</td>
                <td>{{ formatDateTime(assignment.startAt) }} - {{ formatTime(assignment.endAt) }}</td>
                <td>
                  <span class="badge" :class="statusBadgeClass(assignment.status)">{{ assignment.status }}</span>
                </td>
                <td>{{ formatDateTime(assignment.requestedAt) }}</td>
                <td class="text-end">
                  <button
                    v-if="assignment.status === 'PENDING'"
                    class="btn btn-outline-danger btn-sm"
                    type="button"
                    :disabled="cancellingShiftId === assignment.shiftId"
                    @click="cancel(assignment.shiftId, assignment.shiftTitle)"
                  >
                    Cancel
                  </button>
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
import { onMounted, ref } from "vue";
import { cancelShiftRequest, getMyAssignments, type ShiftAssignment } from "../services/shifts";
import StaffNavbar from "../components/StaffNavbar.vue";

const assignments = ref<ShiftAssignment[]>([]);
const loading = ref(false);
const error = ref<string | null>(null);
const successMessage = ref<string | null>(null);
const cancellingShiftId = ref<number | null>(null);

const loadAssignments = async () => {
  try {
    loading.value = true;
    error.value = null;
    assignments.value = await getMyAssignments();
  } catch (err) {
    error.value = err instanceof Error ? err.message : "Failed to load assignments";
  } finally {
    loading.value = false;
  }
};

const cancel = async (shiftId: number, title: string) => {
  try {
    cancellingShiftId.value = shiftId;
    error.value = null;
    await cancelShiftRequest(shiftId);
    successMessage.value = `Request canceled for ${title}`;
    await loadAssignments();
    setTimeout(() => {
      successMessage.value = null;
    }, 2500);
  } catch (err) {
    error.value = err instanceof Error ? err.message : "Failed to cancel request";
  } finally {
    cancellingShiftId.value = null;
  }
};

const formatDateTime = (input: string) => {
  const date = new Date(input);
  return date.toLocaleString("de-DE", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit"
  });
};

const formatTime = (input: string) => {
  const date = new Date(input);
  return date.toLocaleTimeString("de-DE", {
    hour: "2-digit",
    minute: "2-digit"
  });
};

const statusBadgeClass = (status: string) => {
  if (status === "APPROVED") return "text-bg-success";
  if (status === "REJECTED") return "text-bg-danger";
  return "text-bg-warning";
};

onMounted(loadAssignments);
</script>
