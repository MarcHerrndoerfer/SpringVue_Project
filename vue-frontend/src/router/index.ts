import { createRouter, createWebHistory } from "vue-router";
import HomeView from "../views/HomeView.vue";
import LoginView from "../views/LoginView.vue";
import RegisterView from "../views/RegisterView.vue";
import StaffDashboard from "../views/StaffDashboard.vue";
import AvailableShiftsView from "../views/AvailableShiftsView.vue";
import CreateShiftView from "../views/CreateShiftView.vue";
import MyShiftsView from "../views/MyShiftsView.vue";
import ManageAssignmentsView from "../views/ManageAssignmentsView.vue";
import { me, type MeResponse } from "../services/auth";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: "/",
      name: "home",
      component: HomeView
    },
    {
      path: "/staff",
      name: "staff",
      component: StaffDashboard,
      meta: { requiresStaff: true }
    },
    {
      path: "/shifts",
      name: "shifts",
      component: AvailableShiftsView,
      meta: { requiresStaff: true }
    },
    {
      path: "/shifts/new",
      name: "create-shift",
      component: CreateShiftView,
      meta: { requiresStaff: true }
    },
    {
      path: "/my-shifts",
      name: "my-shifts",
      component: MyShiftsView,
      meta: { requiresStaff: true }
    },
    {
      path: "/admin/assignments",
      name: "admin-assignments",
      component: ManageAssignmentsView,
      meta: { requiresAdmin: true }
    },
    {
      path: "/login",
      name: "login",
      component: LoginView
    },
    {
      path: "/register",
      name: "register",
      component: RegisterView
    }
  ]
});

const STAFF_ROLES = new Set(["DOCTOR", "NURSE", "ADMIN"]);

function readStoredUser(): MeResponse | null {
  const raw = localStorage.getItem("authUser");
  if (!raw) return null;
  try {
    return JSON.parse(raw) as MeResponse;
  } catch {
    return null;
  }
}

router.beforeEach(async (to) => {
  if (!to.meta?.requiresStaff && !to.meta?.requiresAdmin) return true;

  let user = readStoredUser();

  if (!user) {
    try {
      user = await me();
      localStorage.setItem("authUser", JSON.stringify(user));
    } catch {
      localStorage.removeItem("authUser");
      return { path: "/login", query: { redirect: to.fullPath } };
    }
  }

  if (to.meta?.requiresAdmin) {
    if (user.role !== "ADMIN") {
      return { path: "/" };
    }
    return true;
  }

  if (!STAFF_ROLES.has(user.role)) {
    return { path: "/" };
  }

  return true;
});

export default router;
