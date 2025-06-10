<template>
  <div v-if="isLoading" class="spinner-wrapper">
    <LoadingSpinner />
  </div>
  <div v-if="!isLoading" class="edit-profile-page">
    <h1>Редагування</h1>

    <form @submit.prevent="updateClient">
      <div class="form-group">
        <label>Ім'я</label>
        <input type="text" v-model="form.name" required />
      </div>

      <div class="form-group">
        <label>Прізвище</label>
        <input type="text" v-model="form.surname" required />
      </div>

      <div class="form-group">
        <label>Дата народження</label>
        <input type="date" v-model="form.birthday" required />
      </div>

      <div class="form-group">
        <label>Номер телефону</label>
        <input type="tel" v-model="form.phoneNumber" required />
      </div>

      <div class="form-actions">
        <button type="button" class="cancel-btn" @click="cancelEdit">
          Назад
        </button>

        <button type="submit" class="save-btn">
          <span v-if="!isLoadingBtn">Зберегти</span>
          <span v-if="isLoadingBtn" class="small">
            <LoadingSpinner class="small" />
          </span>
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import axios from "axios";
import LoadingSpinner from "@/components/LoadingSpinner.vue";

const router = useRouter();
const isLoading = ref(true);
const isLoadingBtn = ref(false);

const form = ref({});

const cancelEdit = () => {
  router.push("/profile");
};

onMounted(async () => {
  try {
    const res = await axios.get("/gymfit/clients/profile-update");
    form.value = res.data;
  } catch (err) {
    console.error("Failed to fetch client info:", err);
  } finally {
    isLoading.value = false;
  }
});

const updateClient = async () => {
  let endpoint = "/gymfit/clients/profile-update";
  isLoadingBtn.value = true;
  try {
    const res = await axios.post(endpoint, form.value);
  } catch (err) {
    console.error("Failed to update client info:", err);
  }
  isLoadingBtn.value = false;
  await router.push("/profile");
};
</script>

<style scoped lang="scss">
@use "@/scss/styles" as *;

.spinner-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 70vh;
}

.edit-profile-page {
  margin: 6% auto;
  width: 90%;
  max-width: 700px;
  background: #1e1e1e;
  padding: 3rem;
  border-radius: 2rem;
  color: #fff;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.7);

  h1 {
    text-align: center;
    color: $fit-highlight;
    margin-bottom: 2.5rem;
    font-family: Impact, Charcoal, sans-serif;
    font-size: 3rem;
  }

  form {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;

    .form-group {
      display: flex;
      flex-direction: column;

      label {
        margin-bottom: 0.5rem;
        font-weight: bold;
        font-size: 1.35rem;
        color: $fit-highlight;
      }

      input {
        padding: 0.8rem 1rem;
        border-radius: 1rem;
        border: 1px solid #444;
        background: #2c2c2c;
        color: #fff;
        font-size: 1.35rem;

        &:focus {
          outline: none;
          border-color: $fit-highlight;
        }
      }
    }

    .form-actions {
      display: flex;
      justify-content: space-between;
      margin-top: 2rem;

      .save-btn,
      .cancel-btn {
        padding: 0.8rem 1.5rem;
        border: none;
        width: 160px;
        border-radius: 1.2rem;
        font-weight: bold;
        font-size: 1.3rem;
        cursor: pointer;
        transition: background 0.3s;
      }

      .save-btn {
        background-color: $fit-highlight;
        color: #1e1e1e;

        &:hover {
          background-color: darken($fit-highlight, 10%);
        }
      }

      .cancel-btn {
        background-color: #aaa;
        color: #1e1e1e;

        &:hover {
          background-color: darken(#aaa, 10%);
        }
      }
    }
  }
}
</style>
