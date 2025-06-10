<template>
  <div class="memberships-page">
    <h1>Обери свій абонемент</h1>

    <div class="search-bar">
      <input
        v-model="searchQuery"
        type="text"
        placeholder="Пошук за ім'ям та прізвищем"
      />
      <select v-model="specialtyQuery">
        <option value="">Всі напрями</option>
        <option v-for="spec in specialties" :key="spec" :value="spec">
          {{ spec }}
        </option>
      </select>
      <select v-model="typeQuery">
        <option value="PERSONAL">Персональні</option>
        <option value="GROUP">Групові</option>
      </select>
    </div>

    <div v-if="isLoading" class="spinner-wrapper">
      <LoadingSpinner />
    </div>

    <div v-else class="memberships-grid">
      <template v-for="m in filteredMemberships" :key="m.id">
        <CoachMembershipCard
          v-if="typeQuery === 'PERSONAL'"
          :membership="m"
          @added="triggerPopup"
        />
        <GroupMembershipCard v-else :membership="m" @added="triggerPopup" />
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from "vue";
import CoachMembershipCard from "@/components/CoachMembershipCard.vue";
import GroupMembershipCard from "@/components/GroupMembershipCard.vue";
import LoadingSpinner from "@/components/LoadingSpinner.vue";
import axios from "axios";
import { useToast } from "vue-toastification";
import { useRoute } from "vue-router";

const route = useRoute();
const toast = useToast();
const memberships = ref([]);
const isLoading = ref(true);
const searchQuery = ref((route.query.coach || "").trim());
const specialtyQuery = ref(route.query.field || "");
const typeQuery = ref(route.query.type || "PERSONAL");

const specialties = ref([]);

const fetchMemberships = async () => {
  isLoading.value = true;

  let endpoint = "/gymfit/group-classes/all";
  if (typeQuery.value === "PERSONAL") {
    endpoint = "/gymfit/coach-membership-templates/all";
  }
  try {
    const res = await axios.get(endpoint);
    memberships.value = res.data.map((m) => ({
      ...m,
      membershipType: typeQuery.value,
    }));
    specialties.value = [
      ...new Set(res.data.map((m) => m.fieldName || m.specialty)),
    ];
  } catch (err) {
    console.error("Failed to fetch memberships:", err);
  } finally {
    isLoading.value = false;
  }
};

watch(typeQuery, fetchMemberships, { immediate: true });

const triggerPopup = () => {
  toast.success("Абонемент додано в кошик!");
};

const filteredMemberships = computed(() => {
  return memberships.value.filter((m) => {
    const matchesCoach = `${m.coachName} ${m.coachSurname}`
      .trim()
      .toLowerCase()
      .includes(searchQuery.value.toLowerCase());
    const matchesSpecialty =
      !specialtyQuery.value ||
      (m.specialty || m.fieldName)
        .toLowerCase()
        .includes(specialtyQuery.value.toLowerCase());
    const matchesType = typeQuery.value
      ? m.membershipType === typeQuery.value
      : true;
    return matchesCoach && matchesSpecialty && matchesType;
  });
});
</script>

<style scoped lang="scss">
@use "@/scss/styles" as *;

.memberships-page {
  margin: 6% auto;
  width: 95%;
  max-width: 1400px;
  padding: 2rem;
  border-radius: 1.5rem;
}

h1 {
  font-size: 2.8rem;
  text-align: center;
  color: white;
  font-family: Impact, sans-serif;
  margin-bottom: 2rem;
}

.search-bar {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 1rem;
  margin-bottom: 2.5rem;

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

    &:focus {
      outline: none;
      border-color: $fit-highlight;
      background-color: #333;
    }
  }

  input::placeholder {
    color: #bbb;
  }
}

.memberships-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(280px, 1fr));
  gap: 2rem;
  padding: 1rem 0;
}

.spinner-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 70vh;
  margin-top: -10%;
}
</style>
