<template>
  <nav class="navbar navbar-expand-lg bg-body-tertiary border rounded px-3 py-2 staff-navbar">
    <div class="container-fluid px-0">
      <span class="navbar-brand fw-semibold">Staff Navigation</span>

      <ul class="navbar-nav me-auto mb-2 mb-lg-0 gap-1">
        <li class="nav-item">
          <router-link :class="['nav-link', { active: isActive(['/staff']) }]" to="/staff">Dashboard</router-link>
        </li>
        <li class="nav-item">
          <router-link :class="['nav-link', { active: isActive(['/shifts']) }]" to="/shifts">Available Shifts</router-link>
        </li>
        <li class="nav-item">
          <router-link :class="['nav-link', { active: isActive(['/my-shifts']) }]" to="/my-shifts">My Shifts</router-link>
        </li>
        <li class="nav-item">
          <router-link :class="['nav-link', { active: isActive(['/shifts/new']) }]" to="/shifts/new">Create Shift</router-link>
        </li>
        <li class="nav-item" v-if="user?.role === 'ADMIN'">
          <router-link :class="['nav-link', { active: isActive(['/admin/assignments']) }]" to="/admin/assignments">Admin Assignments</router-link>
        </li>
      </ul>

      <div class="d-flex align-items-center gap-2">
        <span v-if="user" class="small text-muted d-none d-md-inline">
          {{ user.name }} ({{ user.role }})
        </span>
        <button class="btn btn-outline-secondary btn-sm" type="button" @click="handleLogout">Log out</button>
      </div>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { logout, me, type MeResponse } from "../services/auth";

const router = useRouter();
const route = useRoute();
const user = ref<MeResponse | null>(null);

const isActive = (paths: string[]) => paths.includes(route.path);

const loadLocalUser = () => {
  const raw = localStorage.getItem("authUser");
  if (!raw) return;
  try {
    user.value = JSON.parse(raw) as MeResponse;
  } catch {
    user.value = null;
  }
};

onMounted(async () => {
  loadLocalUser();
  try {
    const fresh = await me();
    user.value = fresh;
    localStorage.setItem("authUser", JSON.stringify(fresh));
  } catch {
    // keep local value
  }
});

const handleLogout = async () => {
  try {
    await logout();
  } finally {
    localStorage.removeItem("authUser");
    user.value = null;
    router.push("/login");
  }
};
</script>

<style scoped>
.navbar .nav-link {
  border-radius: 999px;
  padding: 0.35rem 0.75rem;
}

.navbar .nav-link.active {
  background-color: var(--bs-primary);
  color: var(--bs-white) !important;
  font-weight: 600;
}
</style>
