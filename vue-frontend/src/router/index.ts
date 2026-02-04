import { createRouter, createWebHistory } from "vue-router";
import HomeView from "../views/HomeView.vue";
import LoginView from "../views/LoginView.vue";
import RegisterView from "../views/RegisterView.vue";
import StaffDashboard from "../views/StaffDashboard.vue";
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
  if (!to.meta?.requiresStaff) return true;

  let user = readStoredUser();

  if (!user) {
    try {
      user = await me();
      localStorage.setItem("authUser", JSON.stringify(user));
    } catch {
      return { path: "/login", query: { redirect: to.fullPath } };
    }
  }

  if (!STAFF_ROLES.has(user.role)) {
    return { path: "/" };
  }

  return true;
});

export default router;
