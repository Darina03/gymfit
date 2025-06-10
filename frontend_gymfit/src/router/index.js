import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "@/stores/auth";

const routes = [
  { path: "/", component: () => import("@/views/pages/Home.vue") },
  {
    path: "/login",
    component: () => import("@/views/pages/LoginPage.vue"),
  },
  {
    path: "/register",
    component: () => import("@/views/pages/SignUpPage.vue"),
  },
  {
    path: "/success",
    component: () => import("@/views/pages/PaymentSuccess.vue"),
    beforeEnter: (to, from, next) => {
      if (!to.query.sessionId) {
        next("/profile");
      } else {
        next();
      }
    },
  },
  {
    path: "/my-workouts",
    component: () => import("@/views/pages/MyWorkoutsPage.vue"),
    meta: { role: "CLIENT" },
  },
  {
    path: "/coaches",
    component: () => import("@/views/pages/Coaches.vue"),
  },
  {
    path: "/coaches/:id",
    component: () => import("@/views/pages/CoachProfile.vue"),
    props: true,
  },
  {
    path: "/memberships",
    component: () => import("@/views/pages/MembershipsPage.vue"),
  },
  {
    path: "/cart",
    component: () => import("@/views/pages/CartPage.vue"),
    props: true,
    meta: { role: "CLIENT" },
  },
  {
    path: "/profile",
    component: () => import("@/views/pages/UserProfile.vue"),
    props: true,
    meta: { role: "CLIENT" },
  },
  {
    path: "/edit-profile",
    component: () => import("@/views/pages/EditProfile.vue"),
    props: true,
  },
  {
    path: "/special-offers",
    component: () => import("@/views/pages/DiscountsPage.vue"),
  },
  {
    path: "/workouts",
    component: () => import("@/views/pages/WorkoutBookingPage.vue"),
    props: true,
    meta: { role: "CLIENT" },
  },
  {
    path: "/coach/dashboard",
    component: () => import("@/views/coach/Dashboard.vue"),
    meta: { role: "COACH" },
  },
  {
    path: "/admin/dashboard",
    component: () => import("@/views/admin/Dashboard.vue"),
    meta: { role: "ADMIN" },
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  const auth = useAuthStore();
  if (to.meta.role && to.meta.role !== auth.role) {
    return next("/");
  }
  next();
});

export default router;
