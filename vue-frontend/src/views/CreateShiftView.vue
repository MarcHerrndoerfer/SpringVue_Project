<template>
  <div class="container py-5">
    <StaffNavbar class="mb-4" />

    <div class="d-flex flex-column flex-lg-row align-items-lg-center justify-content-between gap-3 mb-4">
      <div>
        <h1 class="h3 mb-1">Create Shift</h1>
        <p class="text-muted mb-0">Add a new shift to the planning system</p>
      </div>
      <router-link to="/shifts" class="btn btn-outline-secondary btn-sm">Back to shifts</router-link>
    </div>

    <div v-if="error" class="alert alert-danger">{{ error }}</div>
    <div v-if="successMessage" class="alert alert-success">{{ successMessage }}</div>

    <div class="card shadow-sm border-0">
      <div class="card-body">
        <form class="row g-3" @submit.prevent="submitForm">
          <div class="col-12 col-md-6">
            <label class="form-label">Department</label>
            <select v-model.number="form.departmentId" class="form-select" required>
              <option :value="0" disabled>Select department</option>
              <option v-for="department in departments" :key="department.id" :value="department.id">
                {{ department.name }}
              </option>
            </select>
          </div>

          <div class="col-12 col-md-6">
            <label class="form-label">Required Staff</label>
            <input v-model.number="form.requiredStaff" type="number" min="1" class="form-control" required />
          </div>

          <div class="col-12">
            <label class="form-label">Shift Title</label>
            <input v-model.trim="form.title" type="text" class="form-control" placeholder="e.g. Night Shift ICU" required />
          </div>

          <div class="col-12 col-md-6">
            <label class="form-label">Start</label>
            <input v-model="form.startAt" type="datetime-local" class="form-control" required />
          </div>

          <div class="col-12 col-md-6">
            <label class="form-label">End</label>
            <input v-model="form.endAt" type="datetime-local" class="form-control" required />
          </div>

          <div class="col-12 d-flex justify-content-end gap-2">
            <router-link to="/staff" class="btn btn-outline-secondary">Cancel</router-link>
            <button class="btn btn-primary" type="submit" :disabled="submitting">
              {{ submitting ? "Saving..." : "Create Shift" }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { createShift, getDepartments, type Department } from "../services/shifts";
import StaffNavbar from "../components/StaffNavbar.vue";

const router = useRouter();

const departments = ref<Department[]>([]);
const submitting = ref(false);
const error = ref<string | null>(null);
const successMessage = ref<string | null>(null);

const form = ref({
  departmentId: 0,
  title: "",
  startAt: "",
  endAt: "",
  requiredStaff: 1
});

const loadDepartments = async () => {
  try {
    error.value = null;
    departments.value = await getDepartments();
  } catch (err) {
    error.value = err instanceof Error ? err.message : "Failed to load departments";
  }
};

const toIso = (value: string) => {
  const date = new Date(value);
  return date.toISOString().slice(0, 19);
};

const submitForm = async () => {
  if (form.value.departmentId <= 0) {
    error.value = "Please select a department";
    return;
  }

  if (!form.value.title || !form.value.startAt || !form.value.endAt) {
    error.value = "Please fill all fields";
    return;
  }

  if (new Date(form.value.endAt) <= new Date(form.value.startAt)) {
    error.value = "End time must be after start time";
    return;
  }

  try {
    submitting.value = true;
    error.value = null;

    await createShift({
      departmentId: form.value.departmentId,
      title: form.value.title,
      startAt: toIso(form.value.startAt),
      endAt: toIso(form.value.endAt),
      requiredStaff: form.value.requiredStaff
    });

    successMessage.value = "Shift created successfully";
    setTimeout(() => {
      router.push("/shifts");
    }, 900);
  } catch (err) {
    error.value = err instanceof Error ? err.message : "Failed to create shift";
  } finally {
    submitting.value = false;
  }
};

onMounted(loadDepartments);
</script>
