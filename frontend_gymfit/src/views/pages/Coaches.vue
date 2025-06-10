<script setup>
import { ref, computed, onMounted } from "vue";
import axios from "axios";
import CoachCard from "@/components/CoachCard.vue";
import LoadingSpinner from "@/components/LoadingSpinner.vue";

const coaches = ref([]);
const gyms = ref([]);
const specialties = ref([]);
const isLoading = ref(true);

const nameQuery = ref("");
const gymQuery = ref("");
const specialtyQuery = ref("");

onMounted(async () => {
  try {
    const { data: data } = await axios.get(
      "/gymfit/coaches/all-for-coaches-page",
    );
    coaches.value = data.coaches;
    gyms.value = data.gyms;
    specialties.value = data.fields;
  } catch (error) {
    console.log(error);
  } finally {
    isLoading.value = false;
  }
});

const filteredCoaches = computed(() => {
  return coaches.value.filter((coach) => {
    const fullName = `${coach.name} ${coach.surname}`.toLowerCase();
    const matchesName = fullName.includes(nameQuery.value.toLowerCase());

    const matchesGym = !gymQuery.value || coach.idGym === gymQuery.value;
    console.log(typeof gymQuery.value + " " + coach.idGym);
    const matchesSpecialty =
      !specialtyQuery.value ||
      coach.fieldNames.some((field) =>
        field.toLowerCase().includes(specialtyQuery.value.toLowerCase()),
      );

    return matchesName && matchesGym && matchesSpecialty;
  });
});
</script>

<template>
  <div class="coaches">
    <h1>Тренери</h1>

    <div class="filters">
      <input
        type="text"
        v-model="nameQuery"
        placeholder="Пошук за ім'ям та прізвищем"
      />

      <select v-model="gymQuery">
        <option value="">Всі центри</option>
        <option v-for="gym in gyms" :key="gym" :value="gym.id">
          {{ gym.number }}
        </option>
      </select>

      <select v-model="specialtyQuery">
        <option value="">Всі напрями</option>
        <option v-for="spec in specialties" :key="spec" :value="spec.name">
          {{ spec.name }}
        </option>
      </select>
    </div>
    <div v-if="isLoading" class="spinner-wrapper">
      <LoadingSpinner />
    </div>

    <div v-else class="coaches-list">
      <router-link
        v-for="coach in filteredCoaches"
        :key="coach.id"
        :coach-id="coach.id"
        :to="`/coaches/${coach.id}`"
        class="coach-link"
      >
        <CoachCard :coach="coach" />
      </router-link>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use "@/scss/styles" as *;

.spinner-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 40vh;
}

.coaches {
  margin: 7% auto;
  width: 90%;
}

h1 {
  text-align: center;
  font-size: 3rem;
  color: #ffffff;
  font-family: Impact, Charcoal, sans-serif;
}

.filters {
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin: 2rem auto;
  flex-wrap: wrap;

  input,
  select {
    background: #2a2a2a;
    color: white;
    padding: 0.7rem 1.2rem;
    font-size: 1.2rem;
    border-radius: 0.8rem;
    border: 1px solid #444;
    width: 270px;
    transition: 0.3s;
  }

  input::placeholder {
    color: #aaa;
  }

  select:focus,
  input:focus {
    outline: 2px solid $fit-highlight;
  }
}

.coaches-list {
  margin-top: 3%;
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 5rem;
}
</style>
