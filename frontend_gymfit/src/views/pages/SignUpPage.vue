<template>
  <div class="signup-wrapper">
    <div class="signup-card">
      <h1>Реєстрація</h1>

      <form @submit.prevent="submitForm">
        <div class="steps">
          <span :class="{ active: step === 1 }">1</span>
          <span :class="{ active: step === 2 }">2</span>
          <span :class="{ active: step === 3 }">3</span>
        </div>

        <div v-if="step === 1" class="step-section">
          <label>Ім'я</label>
          <input v-model="form.firstName" type="text" />
          <span class="error-message" v-if="errors.step1.firstName">{{
            errors.step1.firstName
          }}</span>

          <label>Прізвище</label>
          <input v-model="form.lastName" type="text" />
          <span class="error-message" v-if="errors.step1.lastName">{{
            errors.step1.lastName
          }}</span>

          <label>День народження</label>
          <input v-model="form.birthDate" type="date" />
          <span class="error-message" v-if="errors.step1.birthDate">{{
            errors.step1.birthDate
          }}</span>
        </div>

        <div v-if="step === 2" class="step-section">
          <label>Номер телефону</label>
          <input
            v-mask="'+38 (0##) ### ## ##'"
            v-model="form.phone"
            type="tel"
          />
          <span class="error-message" v-if="errors.step2.phone">{{
            errors.step2.phone
          }}</span>

          <label>Електронна адреса</label>
          <input v-model="form.email" type="email" />
          <span class="error-message" v-if="errors.step2.email">{{
            errors.step2.email
          }}</span>
        </div>

        <div v-if="step === 3" class="step-section">
          <label>Пароль</label>
          <input v-model="form.password" type="password" />
          <span class="error-message" v-if="errors.step3.password">{{
            errors.step3.password
          }}</span>

          <label>Підтвердіть пароль</label>
          <input v-model="form.confirmPassword" type="password" />
          <span class="error-message" v-if="errors.step3.confirmPassword">{{
            errors.step3.confirmPassword
          }}</span>
        </div>

        <div class="buttons">
          <button type="button" @click="prevStep" :disabled="step === 1">
            Назад
          </button>
          <button type="button" @click="nextStep" v-if="step < 3">Далі</button>
          <button type="submit" v-if="step === 3" :disabled="loading">
            <span v-if="loading"
              ><LoadingSpinner class="small"></LoadingSpinner
            ></span>
            <span v-else>Зареєструватися</span>
          </button>
        </div>
      </form>
    </div>
  </div>
  <CustomModal
    :visible="modalVisible"
    :title="'Успішно!'"
    @close="goToProfile"
    :persistent="true"
  >
    <template #default>
      <p>Ваш акаунт створений! Раді Вас вітати!</p>
      <button class="modal-btn" @click="goToProfile">Перейти в профіль</button>
    </template>
  </CustomModal>

  <CustomModal
    v-if="errorMessage"
    title="Упс.."
    :message="errorMessage"
    width="500px"
    @close="errorMessage = ''"
  >
  </CustomModal>
</template>

<script setup>
import { ref, watch } from "vue";
import { useAuthStore } from "@/stores/auth";
import { useRouter } from "vue-router";
import CustomModal from "@/components/CustomModal.vue";
import LoadingSpinner from "@/components/LoadingSpinner.vue";

const authStore = useAuthStore();
const router = useRouter();

const loading = ref(false);
const errorMessage = ref("");
const modalVisible = ref(false);
const step = ref(1);

const form = ref({
  firstName: "",
  lastName: "",
  birthDate: "",
  phone: "",
  email: "",
  password: "",
  confirmPassword: "",
});

const errors = ref({
  step1: {},
  step2: {},
  step3: {},
});

const validateEmail = (email) => {
  const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return regex.test(email.trim());
};

const validatePasswordStrength = (password) => {
  const regex =
    /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=[\]{};':"\\|,.<>/?]).{8,}$/;
  return regex.test(password);
};

const validatePhone = (phone) => {
  const digitsOnly = phone.replace(/\D/g, "");
  return digitsOnly.length === 12;
};

const validateStep1 = () => {
  errors.value.step1 = {};
  const { firstName, lastName, birthDate } = form.value;

  if (!firstName.trim()) {
    errors.value.step1.firstName = "Поле має бути заповнене!";
  }
  if (!lastName.trim()) {
    errors.value.step1.lastName = "Поле має бути заповнене!";
  }

  if (!birthDate) {
    errors.value.step1.birthDate = "Поле має бути заповнене!";
  } else {
    const today = new Date();
    const birth = new Date(birthDate);
    const age = today.getFullYear() - birth.getFullYear();
    const isBeforeBirthday =
      today.getMonth() < birth.getMonth() ||
      (today.getMonth() === birth.getMonth() &&
        today.getDate() < birth.getDate());

    const fullAge = isBeforeBirthday ? age - 1 : age;

    if (birth > today) {
      errors.value.step1.birthDate = "Дата не може бути з майбутнього!";
    } else if (fullAge < 18) {
      errors.value.step1.birthDate = "Мінімальній вік - 18 років!";
    } else if (fullAge > 110) {
      errors.value.step1.birthDate = "Ваш вік нереліастичний!";
    }
  }

  return Object.keys(errors.value.step1).length === 0;
};
const validateStep2 = () => {
  errors.value.step2 = {};
  const { phone, email } = form.value;

  if (!phone.trim()) {
    errors.value.step2.phone = "Поле має бути заповнене!";
  } else if (!validatePhone(phone)) {
    errors.value.step2.phone = "Неправильний формат!";
  }

  if (!email.trim()) {
    errors.value.step2.email = "Поле має бути заповнене!";
  } else if (!validateEmail(email)) {
    errors.value.step2.email = "Неправильний формат!";
  }

  return Object.keys(errors.value.step2).length === 0;
};

const validateStep3 = () => {
  errors.value.step3 = {};
  const { password, confirmPassword } = form.value;

  if (!password) {
    errors.value.step3.password = "Поле має бути заповнене!";
  } else if (!validatePasswordStrength(password)) {
    errors.value.step3.password =
      "Пароль має містити щонайменше 8 символів, включаючи великі й малі літери, цифри та спеціальний символ.";
  }

  if (!confirmPassword) {
    errors.value.step3.confirmPassword = "Введіть пароль ще раз!";
  } else if (password !== confirmPassword) {
    errors.value.step3.confirmPassword = "Паролі не збігаються";
  }

  return Object.keys(errors.value.step3).length === 0;
};

const prevStep = () => {
  if (step.value > 1) step.value--;
};

const nextStep = () => {
  if (step.value === 1 && !validateStep1()) return;
  if (step.value === 2 && !validateStep2()) return;
  if (step.value < 3) step.value++;
};

const submitForm = async () => {
  if (!validateStep3()) return;

  loading.value = true;
  try {
    await authStore.register(form.value);
    modalVisible.value = true;
  } catch (error) {
    errorMessage.value =
      error.response?.data?.message ||
      "Реєстрація не вдалася... Спробуйте ще раз!";
  } finally {
    loading.value = false;
  }
};

const goToProfile = () => {
  modalVisible.value = false;
  router.push("/profile");
};

watch(
  () => form.value.firstName,
  (val) => {
    if (val?.trim()) delete errors.value.step1.firstName;
  },
);

watch(
  () => form.value.lastName,
  (val) => {
    if (val?.trim()) delete errors.value.step1.lastName;
  },
);

watch(
  () => form.value.birthDate,
  (val) => {
    if (val) {
      const today = new Date();
      const birth = new Date(val);
      const age = today.getFullYear() - birth.getFullYear();
      const isBeforeBirthday =
        today.getMonth() < birth.getMonth() ||
        (today.getMonth() === birth.getMonth() &&
          today.getDate() < birth.getDate());

      const fullAge = isBeforeBirthday ? age - 1 : age;

      if (birth <= today && fullAge >= 18) {
        delete errors.value.step1.birthDate;
      }
    }
  },
);

watch(
  () => form.value.phone,
  (val) => {
    if (val?.trim() && validatePhone(val)) delete errors.value.step2.phone;
  },
);

watch(
  () => form.value.email,
  (val) => {
    if (val?.trim() && validateEmail(val)) delete errors.value.step2.email;
  },
);

watch(
  () => form.value.password,
  (val) => {
    if (val?.trim() && validatePasswordStrength(val))
      delete errors.value.step3.password;
    if (form.value.confirmPassword && val === form.value.confirmPassword)
      delete errors.value.step3.confirmPassword;
  },
);

watch(
  () => form.value.confirmPassword,
  (val) => {
    if (val?.trim()) delete errors.value.step3.confirmPassword;
    if (val === form.value.password) delete errors.value.step3.confirmPassword;
  },
);
</script>

<style scoped lang="scss">
@use "@/scss/styles" as *;
.error-message {
  color: red;
  font-size: 1.1rem;
  margin-top: 2px;
  display: block;
}
.signup-wrapper {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 2rem;
  overflow: hidden;

  // Background image
  background-image: url("https://images.unsplash.com/photo-1532029837206-abbe2b7620e3?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D");
  background-size: cover;
  background-position: center;

  // Dark overlay
  &::before {
    content: "";
    position: absolute;
    inset: 0;
    background: rgba(0, 0, 0, 0.6);
    z-index: 0;
  }

  .signup-card {
    background: #1e1e1e;
    padding: 2.5rem;
    border-radius: 1.5rem;
    width: 100%;
    max-width: 550px;
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.7);
    color: #fff;
    position: relative;
    z-index: 1;

    h1 {
      text-align: center;
      margin-bottom: 1.8rem;
      color: $fit-highlight;
      font-family: Impact, sans-serif;
      font-size: 2.8rem;
    }

    form {
      display: flex;
      flex-direction: column;

      label {
        font-weight: bold;
        margin-top: 1.2rem;
        font-size: 1.2rem;

        color: $fit-highlight;
      }

      input {
        padding: 0.8rem 1rem;
        margin-top: 0.4rem;
        background: #2c2c2c;
        border: 1px solid #444;
        border-radius: 0.8rem;
        color: #fff;
        font-size: 1.2rem;
        width: 100%;
      }

      .steps {
        display: flex;
        justify-content: center;
        gap: 1rem;
        margin-bottom: 1.5rem;

        span {
          width: 2rem;
          height: 2rem;
          border-radius: 50%;
          background: #444;
          display: flex;
          align-items: center;
          justify-content: center;
          color: #aaa;
          font-weight: bold;
          transition: 0.3s;

          &.active {
            background: $fit-highlight;
            color: #000;
          }
        }
      }

      .buttons {
        display: flex;
        justify-content: space-between;
        margin-top: 2rem;

        button {
          background: $fit-highlight;
          border: none;
          border-radius: 0.8rem;
          padding: 0.8rem 1.5rem;
          font-weight: bold;
          color: #000;
          cursor: pointer;
          transition: background 0.3s;

          &:hover {
            background: darken($fit-highlight, 10%);
          }

          &:disabled {
            background: #444;
            color: #888;
            cursor: not-allowed;
          }
        }
      }
    }
  }
}
.modal-btn {
  @include button-style($fit-highlight, #333);
}
</style>
