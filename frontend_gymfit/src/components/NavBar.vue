<script setup>
import { useAuthStore } from "@/stores/auth";
import { computed } from "vue";
import { useRouter } from "vue-router";

const auth = useAuthStore();
const router = useRouter();

const isLoggedIn = computed(() => auth.token && auth.token !== "");
const handleLogout = () => {
  auth.logout();
  router.push("/");
};
</script>

<template>
  <nav class="navbar navbar-expand-lg fixed-top w-100 transparent-navbar">
    <div
      class="container-fluid d-flex justify-content-between align-items-center"
    >
      <span class="navbar-brand nav_font fs-1">
        Gym<span class="fit-highlight">Fit</span>
      </span>

      <ul class="navbar-nav d-flex flex-row">
        <li class="nav-item me-3">
          <router-link class="nav-link" to="/">Головна</router-link>
        </li>
        <li class="nav-item me-3">
          <router-link class="nav-link" to="/special-offers"
            >Знижки
          </router-link>
        </li>
        <li class="nav-item me-3">
          <router-link class="nav-link" to="/coaches">Тренери</router-link>
        </li>
        <li class="nav-item me-3">
          <router-link class="nav-link" to="/memberships"
            >Абонементи
          </router-link>
        </li>
        <li class="nav-item me-3">
          <router-link v-if="isLoggedIn" class="nav-link" to="/workouts"
            >Тренування
          </router-link>
        </li>
      </ul>

      <div class="d-flex">
        <router-link v-if="isLoggedIn" to="/profile">
          <i class="fas fa-user-circle me-3 profile-icon"></i>
        </router-link>

        <div class="d-flex">
          <router-link v-if="isLoggedIn" to="/my-workouts">
            <i class="fas fa-calendar-days me-3 calendar-icon"></i>
          </router-link>
        </div>

        <router-link v-if="isLoggedIn" to="/cart">
          <i class="fas fa-shopping-cart me-3 cart-icon"></i>
        </router-link>

        <div v-if="!isLoggedIn" class="dropdown">
          <button
            class="btn fit-btn dropdown-toggle"
            type="button"
            id="joinDropdown"
            data-bs-toggle="dropdown"
            aria-expanded="false"
          >
            Приєднатися
          </button>
          <ul
            class="dropdown-menu dropdown-menu-dark"
            aria-labelledby="joinDropdown"
          >
            <li>
              <router-link class="dropdown-item" to="/login">Вхід</router-link>
            </li>
            <li>
              <router-link class="dropdown-item" to="/register"
                >Реєстрація</router-link
              >
            </li>
          </ul>
        </div>

        <button v-else @click="handleLogout" class="btn fit-btn">Вихід</button>
      </div>
    </div>
  </nav>
</template>

<style lang="scss" scoped>
@use "@/scss/styles" as *;

$profile-color: #0a3ba6;
$calendar-color: #0b9b28;
$cart-color: #de8406;

.navbar-brand {
  font-weight: 800;
  color: #ffffff;
}

.navbar-brand:hover {
  color: #ffffff;
}

.router-link-active {
  color: $fit-highlight;
}

.router-link-exact-active {
  color: $fit-highlight !important;
}

li,
a {
  font-family: Impact, sans-serif;
  font-size: 1.4rem;
  color: #cccccc;
}

li a:hover {
  color: $fit-highlight;
}

.nav_font {
  font-style: italic;
  font-family: Impact, Charcoal, sans-serif;
  font-weight: 50;
}

.fit-btn {
  font-family: Impact, Charcoal, sans-serif;
  font-size: 1rem;
}

.d-flex {
  margin-right: 0.5%;
}

.profile-icon {
  @include icon-color($profile-color);
}

.calendar-icon {
  @include icon-color($calendar-color);
}

.cart-icon {
  @include icon-color($cart-color);
}

.transparent-navbar {
  background-color: rgba(0, 0, 0, 0.5);
  transition: background-color 0.3s ease;
  z-index: 1000;
}

.dropdown-menu-dark {
  background-color: #2c2c2c;
  border: 1px solid #444;
}

.dropdown-item {
  color: #fff;
  font-size: 1.2rem;

  &:hover {
    background-color: $fit-highlight;
    color: #000;
  }
}
</style>
