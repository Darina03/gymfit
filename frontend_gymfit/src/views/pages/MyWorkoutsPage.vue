<template>
  <div class="container">
    <h1 class="main-header">Мої заплановані тренування</h1>
    <div v-if="isLoading" class="spinner-wrapper">
      <LoadingSpinner />
    </div>
    <div v-else class="calendar">
      <div v-if="groupedWorkouts.length" class="calendar-view">
        <div
          v-for="group in groupedWorkouts"
          :key="group.date"
          class="day-section"
        >
          <h2 class="day-title">{{ group.date }}</h2>
          <div class="workout-grid">
            <div
              v-for="workout in group.workouts"
              :key="workout.id"
              class="workout-card"
            >
              <div class="workout-header">
                <span class="type">{{ workout.type }}</span>
                <span class="time">{{ formatTime(workout.workoutDate) }}</span>
              </div>
              <div class="detail">
                <strong>Coach:</strong> {{ workout.coach.coachName }}
                {{ workout.coach.coachSurname }}
              </div>
              <div class="detail">
                <strong>Field:</strong> {{ workout.field.name }}
              </div>
              <button
                v-if="workout.type === 'PERSONAL'"
                class="cancel-btn"
                @click="openCancelModal(workout.id)"
              >
                Відмінити
              </button>
            </div>
          </div>
        </div>
      </div>
      <div v-else class="no-workouts">Поки що нема запланованих тренувань</div>
    </div>

    <CustomModal
      :visible="showConfirmModal"
      title="Впевнені?"
      @close="showConfirmModal = false"
    >
      <template #default>
        <p>Все ще бажаєте відмінити тренування?</p>

        <button class="modal-btn cancel" @click="showConfirmModal = false">
          Ні
        </button>
        <button class="modal-btn confirm" @click="confirmCancel">
          Так, відміняю
        </button>
      </template>
    </CustomModal>

    <CustomModal
      :visible="showResultModal"
      title="Сповіщення"
      @close="showResultModal = false"
    >
      <template #default>
        <p>{{ resultMessage }}</p>
        <button class="modal-btn" @click="showResultModal">OK</button>
      </template>
    </CustomModal>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import axios from "axios";
import LoadingSpinner from "@/components/LoadingSpinner.vue";
import CustomModal from "@/components/CustomModal.vue";

const workouts = ref([]);
const showConfirmModal = ref(false);
const showResultModal = ref(false);
const resultMessage = ref("");
const selectedWorkoutId = ref(null);
const isLoading = ref(false);
const openCancelModal = (id) => {
  selectedWorkoutId.value = id;
  showConfirmModal.value = true;
};

const fetchWorkouts = async () => {
  isLoading.value = true;
  try {
    const response = await axios.get("/gymfit/workouts/all-for-client");
    workouts.value = response.data;
  } catch (err) {
    console.error(err);
    alert("Could not load workouts.");
  } finally {
    isLoading.value = false;
  }
};
const confirmCancel = () => {
  cancelWorkout(selectedWorkoutId.value);
};
const cancelWorkout = async (id) => {
  try {
    await axios.post(`/gymfit/booking/cancel/${selectedWorkoutId.value}`);
    workouts.value = workouts.value.filter(
      (w) => w.id !== selectedWorkoutId.value,
    );
    resultMessage.value = "Трнування успішно відмінено!";
  } catch (err) {
    console.error(err);
    resultMessage.value =
      "На жаль, не вдалося вцідмінити тренування. Спробуйте ще раз!.";
  } finally {
    showConfirmModal.value = false;
    showResultModal.value = true;
  }
};

const formatTime = (dateString) => {
  const date = new Date(dateString);
  return date.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
};

const formatDateOnly = (dateString) => {
  const date = new Date(dateString);
  return date.toLocaleDateString(undefined, {
    weekday: "long",
    month: "short",
    day: "numeric",
  });
};

const groupedWorkouts = computed(() => {
  const grouped = {};
  workouts.value.forEach((w) => {
    const dateKey = formatDateOnly(w.workoutDate);
    if (!grouped[dateKey]) grouped[dateKey] = [];
    grouped[dateKey].push(w);
  });
  return Object.entries(grouped).map(([date, workouts]) => ({
    date,
    workouts,
  }));
});

onMounted(fetchWorkouts);
</script>

<style lang="scss">
@use "@/scss/styles" as *;

.spinner-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 50vh;
}

.container {
  margin: 7% auto;
  max-width: 80%;
}

.calendar {
  margin-top: 3%;
  padding: 2% 2%;
  color: #e0e0e0;
  background: #1e1e1e;
  border-radius: 2rem;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.6);
}

.main-header {
  font-family: Impact, Charcoal, sans-serif;
  font-size: 2.8rem;
}

h1 {
  text-align: center;
  margin-bottom: 30px;
  color: #ffffff;
  font-size: 2rem;
}

.calendar-view {
  display: flex;
  flex-direction: column;
  gap: 40px;
}

.day-section {
  border-left: 4px solid $fit-highlight;
  padding-left: 20px;
}

.day-title {
  font-size: 1.5rem;
  margin-bottom: 16px;
  color: $fit-highlight;
}

.workout-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
}

.workout-card {
  background-color: #333;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  gap: 10px;
  position: relative;
  transition: transform 0.2s ease;
}

.workout-card:hover {
  transform: translateY(-3px);
}

.workout-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
  font-size: 1.3rem;
  color: $fit-highlight;
}

.type {
  text-transform: capitalize;
}

.time {
  font-size: 1.2rem;
  color: #ccc;
}

.detail {
  color: #eee;
  font-size: 1.2rem;
}

.cancel-btn {
  @include button-style($fit-highlight, #333);
}

.no-workouts {
  text-align: center;
  font-size: 1.5rem;
  color: #999;
  margin-top: 40px;
}

.modal-btn {
  background: $fit-highlight;
  color: #1e1e1e;
  padding: 0.8rem 1.5rem;
  border-radius: 1rem;
  margin-top: 1rem;
  cursor: pointer;
  border: none;
  margin-right: 1rem;
  font-size: 1.3rem;
  font-weight: 600;

  &.cancel {
    background: #666;
    color: white;
  }
  &.confirm {
    color: #333;
  }

  &:hover {
    filter: brightness(1.1);
  }
}
</style>
