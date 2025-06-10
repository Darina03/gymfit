<template>
  <div class="login-page">
    <div class="login-card">
      <h2>Вхід</h2>
      <form @submit.prevent="submitLogin">
        <div class="input-group">
          <label>Електронна адреса</label>
          <input v-model="email" type="text" />
          <span v-if="validationErrors.email" class="validation">{{
            validationErrors.email
          }}</span>
        </div>

        <div class="input-group">
          <label>Пароль</label>
          <input v-model="password" type="password" />
          <span v-if="validationErrors.password" class="validation">{{
            validationErrors.password
          }}</span>
        </div>
        <span v-if="error" class="error-message">{{ error }}</span>

        <button type="submit" class="login-btn">
          <span v-if="!isLoading">Вхід</span>
          <span v-if="isLoading && isFormValid" class="spinner-wrapper">
            <LoadingSpinner class="small" />
          </span>
        </button>

        <div class="forgot-password">
          <router-link to="/forgot-password">Забули пароль?</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import LoadingSpinner from "@/components/LoadingSpinner.vue";

const authStore = useAuthStore();
const router = useRouter();

const isLoading = ref(false);
const email = ref("");
const password = ref("");
const error = ref("");
const validationErrors = ref({
  email: "",
  password: "",
});

watch(email, (newVal) => {
  if (!newVal) {
    validationErrors.value.email = "Електронна адреса обов'язкова.";
  } else if (!emailRegex.test(newVal)) {
    validationErrors.value.email = "Неправилний формат!";
  } else {
    validationErrors.value.email = "";
  }
});

watch(password, (newVal) => {
  if (validationErrors.value.password && newVal.trim()) {
    validationErrors.value.password = "";
  }
});

const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

const validateForm = () => {
  validationErrors.value.email = "";
  validationErrors.value.password = "";

  if (!email.value) {
    validationErrors.value.email = "Поле має бути заповнене!";
  } else if (!emailRegex.test(email.value)) {
    validationErrors.value.email = "Неправилний формат!";
  }

  if (!password.value) {
    validationErrors.value.password = "Поле має бути заповнене!";
  }

  return isFormValid.value;
};

const isFormValid = computed(() => {
  return !validationErrors.value.email && !validationErrors.value.password;
});

const submitLogin = async () => {
  error.value = "";

  if (!validateForm()) return;

  isLoading.value = true;

  try {
    await authStore.login(email.value, password.value);

    switch (authStore.role) {
      case "CLIENT":
        router.push("/profile");
        break;
      case "COACH":
        router.push("/coach/profile");
        break;
      case "ADMIN":
        router.push("/admin/dashboard");
        break;
    }
  } catch (e) {
    if (e.response?.data?.message) {
      error.value = e.response.data.message;
    } else {
      error.value = "Перевірте введені дані!";
    }
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped lang="scss">
@use "@/scss/styles" as *;
.spinner-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
}
.validation {
  color: #f44336;
  margin-top: 0.2rem;
  font-size: 1.1rem;
}

.error-message {
  display: block;
  margin: 1rem 1rem 1rem 0;
  color: #f44336;
  text-align: left;
  font-size: 1.1rem;
}

.forgot-password {
  text-align: right;
  margin-top: 0.5rem;

  a {
    color: $fit-highlight;
    font-size: 1.1rem;
    text-decoration: underline;
    cursor: pointer;

    &:hover {
      opacity: 0.8;
    }
  }
}

.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-image: url("https://images.unsplash.com/photo-1434754205268-ad3b5f549b11?q=80&w=2074&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D");
  background-size: cover;
  background-position: center;

  &::before {
    content: "";
    position: absolute;
    inset: 0;
    background: rgba(0, 0, 0, 0.6);
    z-index: 0;
  }

  .login-card {
    background: #1e1e1e;
    padding: 2.5rem;
    border-radius: 2rem;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.8);
    width: 400px;
    z-index: 1;

    h2 {
      color: $fit-highlight;
      font-size: 2rem;
      text-align: center;
      margin-bottom: 2rem;
      font-family: Impact, sans-serif;
    }

    .input-group {
      margin-bottom: 1.2rem;
      display: flex;
      flex-direction: column;

      label {
        color: #ccc;
        margin-bottom: 0.4rem;
        font-weight: bold;
        font-size: 1.2rem;
      }

      input {
        padding: 0.7rem 1rem;
        border-radius: 0.8rem;
        border: 1px solid #444;
        background: #2b2b2b;
        color: #fff;
        font-size: 1.2rem;

        &:focus {
          outline: none;
          border-color: $fit-highlight;
        }
      }
    }

    .login-btn {
      width: 100%;
      padding: 0.8rem;
      border: none;
      border-radius: 1rem;
      background: linear-gradient(135deg, $fit-highlight 0%, #017e7d 100%);
      color: #000;
      font-size: 1.2rem;
      font-weight: bold;
      cursor: pointer;
      transition: background 0.3s;

      &:hover {
        opacity: 0.95;
      }
    }

    .error {
      margin-top: 1rem;
      color: #f44336;
      text-align: center;
      font-weight: bold;
    }
  }
}
</style>
