<template>
  <div class="coach-query">
    <div class="selection">
      <h2>Бронювання тренувань</h2>

      <label for="coach">Оберіть споміж ваших абонементів:</label>
      <select
          id="coach"
          v-model="selectedMembership"
          @change="generateNextDates"
      >
        <option value="" disabled>Оберіть абонемент</option>
        <option
            v-for="membership in memberships"
            :key="membership.coach.id"
            :value="membership"
        >
          {{ membership.label }}
        </option>
      </select>
    </div>

    <div
        v-if="availableDates.length > 0 || isLoadingDates"
        class="date-selection"
    >
      <h3>Обіріть дату</h3>

      <div v-if="isLoadingDates" class="spinner-wrapper">
        <div class="spinner"></div>
      </div>

      <div v-else class="dates">
        <div
            v-for="date in availableDates"
            :key="date"
            class="date-block"
            @click="selectDate(date)"
            :class="{ active: selectedDate === date }"
        >
          {{ formatDate(date) }}
        </div>
      </div>
    </div>

    <div v-if="selectedDate" class="time-selection">
      <h3>Оберіть вільну годину на {{ formatDate(selectedDate) }}</h3>

      <div v-if="isLoading" class="spinner-wrapper">
        <LoadingSpinner />
      </div>

      <div v-else class="times">
        <div
            v-for="slot in allSlots"
            :key="slot.id"
            class="time-slot"
            :class="{ booked: slot.isBooked }"
            @click="!slot.isBooked && openModal(slot)"
        >
          {{ slot.time }}
        </div>
      </div>
    </div>

    <CustomModal
        :visible="showModal"
        title="Підтвердіть бронювання"
        @close="closeModal"
        :persistent="true"
    >
      <template #default>
        <p>
          <strong>Тренер:</strong>
          {{ selectedMembership?.coach?.coachName }}
          {{ selectedMembership?.coach?.coachSurname }}
        </p>
        <p><strong>Дата:</strong> {{ formatDate(selectedSlot?.fullDate) }}</p>
        <p><strong>Час:</strong> {{ selectedSlot?.time }}</p>

        <div
            v-if="!isMembershipActiveOnDate(selectedSlot?.fullDate)"
            class="expired"
        >
          <p>Ваш абонемент буде вже недійсний цього дня.</p>
          <button class="modal-btn" @click="closeModal">Close</button>
        </div>

        <div
            v-if="!isGymMembershipActiveOnDate(selectedSlot?.fullDate)"
            class="expired"
        >
          <p>
            Ваш абонемент доступу до спортзалу буде вже недійсний цього дня.
          </p>
          <button class="modal-btn" @click="closeModal">Close</button>
        </div>

        <div
            v-else-if="
            isGymMembershipActiveOnDate(selectedSlot?.fullDate) &&
            isMembershipActiveOnDate(selectedSlot?.fullDate)
          "
        >
          <button class="modal-btn" @click="confirmBooking">Підтверити</button>
          <button class="modal-btn cancel" @click="closeModal">Назад</button>
        </div>
      </template>
    </CustomModal>

    <CustomModal
        :visible="showResultModal"
        :title="bookingResult.success ? 'Успішно' : 'Помилка'"
        @close="closeResultModal"
        :persistent="true"
    >
      <template #default>
        <p>{{ bookingResult.message }}</p>
        <button class="modal-btn" @click="closeResultModal">OK</button>
      </template>
    </CustomModal>
    <CustomModal
        :visible="showWarningModal"
        title="Увага"
        @close="showWarningModal = false"
        :persistent="true"
    >
      <template #default>
        <p>
          Вам потрібно <strong>Цілодобовий</strong> абонемент доступу до
          спортзалу, щоб бронювати цей вид тренувань. Будь ласка придбайте
          абонемент або зверніться до адміністратора.
        </p>
        <button class="modal-btn" @click="showWarningModal = false">OK</button>
      </template>
    </CustomModal>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import axios from "axios";
import LoadingSpinner from "@/components/LoadingSpinner.vue";
import CustomModal from "@/components/CustomModal.vue";

const memberships = ref([]);
const gymMembership = ref(null);
const selectedMembership = ref("");
const selectedCoachId = ref("");
const selectedDate = ref("");
const selectedSlot = ref(null);
const showModal = ref(false);
const showResultModal = ref(false);
const bookingResult = ref({ success: false, message: "" });
const isLoadingDates = ref(false);
const isLoading = ref(false);
const showWarningModal = ref(false);

const loadMemberships = async () => {
  try {
    const res = await axios.get("/gymfit/memberships/all-active-for-booking");
    memberships.value = res.data.coachMemberships.map((m) => ({
      ...m,
      label: `${m.coach.coachName} ${m.coach.coachSurname} — ${m.fieldName}`,
    }));
    gymMembership.value = res.data.gymMembership;

    console.log(res.data);
  } catch (e) {
    console.error("Failed to load memberships", e);
  }
};

onMounted(loadMemberships);

const availableDates = ref([]);

const generateNextDates = async () => {
  selectedDate.value = "";
  availableDates.value = [];
  isLoadingDates.value = true;

  try {
    const today = new Date();
    const result = [];
    let date = new Date(today);

    while (result.length < 28) {
      const day = date.getDay();
      if (day !== 0 && day !== 6) {
        result.push(date.toISOString().split("T")[0]);
      }
      date.setDate(date.getDate() + 1);
    }

    availableDates.value = result;
  } finally {
    isLoadingDates.value = false;
  }
};

const allSlots = ref([]);

const fetchSchedule = async () => {
  if (!selectedMembership.value || !selectedDate.value) return;
  isLoading.value = true;

  try {
    const res = await axios.get(
        `/gymfit/workouts/all-for-coach/${selectedMembership.value.coach.id}`,
        { params: { date: selectedDate.value } },
    );
    console.log(res.data);
    allSlots.value = res.data.map((slot) => ({
      ...slot,
      fullDate: slot.workoutDate,
      time: new Date(slot.workoutDate).toLocaleTimeString([], {
        hour: "2-digit",
        minute: "2-digit",
        id: slot.id,
      }),
    }));
  } catch (e) {
    console.error("Failed to fetch workouts", e);
  } finally {
    isLoading.value = false;
  }
};

const selectDate = (date) => {
  selectedDate.value = date;
};

watch(selectedDate, fetchSchedule);

const formatDate = (dateStr) => {
  const options = { weekday: "short", month: "short", day: "numeric" };
  return new Date(dateStr).toLocaleDateString(undefined, options);
};

const openModal = (slot) => {
  selectedSlot.value = { ...slot };

  const workoutDate = slot.fullDate;

  const isInvalidGymMembership =
      !gymMembership.value || gymMembership.value.type !== "ALL_DAY_PASS";

  if (isInvalidGymMembership) {
    showWarningModal.value = true;
    return;
  }

  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
  selectedSlot.value = null;
};

const isMembershipActiveOnDate = (date) => {
  if (!selectedMembership.value) return false;

  const membershipEnd = new Date(selectedMembership.value.validTill);
  const trainingDate = new Date(date);

  return membershipEnd >= trainingDate;
};

const isGymMembershipActiveOnDate = (date) => {
  if (!gymMembership.value) return false;

  const membershipEnd = new Date(gymMembership.value.validTill);
  const trainingDate = new Date(date);

  return membershipEnd >= trainingDate;
};
const confirmBooking = async () => {
  if (!selectedSlot.value?.id) {
    console.error("Workout ID is missing");
    return;
  }

  try {
    await axios.post(
        `/gymfit/booking/book/${selectedSlot.value.id}?membershipId=${selectedMembership.value.id}`,
    );

    bookingResult.value = {
      success: true,
      message: "Тренування заброньовано!",
    };
    showResultModal.value = true;
    closeModal();
    await fetchSchedule();
  } catch (e) {
    console.error("Booking failed", e);

    let errorMessage = "На жаль, сталася помилка... Спробуйте ще раз!.";
    if (e.response && e.response.data) {
      errorMessage += " " + e.response.data;
    }

    bookingResult.value = {
      success: false,
      message: errorMessage,
    };
    showResultModal.value = true;
  }
};

const closeResultModal = () => {
  showResultModal.value = false;
  showModal.value = false;
  fetchSchedule();
};
</script>

<style scoped lang="scss">
@use "@/scss/styles" as *;

.spinner-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 10vh;
}

.coach-query {
  width: 95%;
  max-width: 1200px;
  margin: 7% auto;
  background: #1e1e1e;
  padding: 3rem;
  border-radius: 2rem;
  color: #fff;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.7);

  .selection {
    margin-bottom: 2rem;

    h2 {
      font-family: Impact, sans-serif;
      font-size: 2.8rem;
      color: $fit-highlight;
      text-align: center;
    }

    label,
    select {
      display: block;
      width: 100%;
      margin-top: 1rem;
      font-size: 1.2rem;
    }

    select {
      background: #2c2c2c;
      color: #fff;
      border: 1px solid #444;
      border-radius: 1rem;
      padding: 0.8rem;
      margin-top: 0.5rem;
    }
  }

  .date-selection,
  .time-selection {
    margin-top: 2rem;

    h3 {
      font-size: 2rem;
      margin-bottom: 1.5rem;
      color: $fit-highlight;
    }

    .dates,
    .times {
      display: grid;
      grid-template-columns: repeat(7, 1fr);
      gap: 1rem;
    }

    .date-block,
    .time-slot {
      background: #2c2c2c;
      padding: 1rem;
      border-radius: 1rem;
      cursor: pointer;
      font-size: 1.2rem;
      text-align: center;
      transition: background 0.3s;
      user-select: none;
      box-shadow: 0 4px 10px rgba(0, 0, 0, 0.5);

      &.active {
        background: $fit-highlight;
        color: #1e1e1e;
        font-weight: bold;
      }

      &.booked {
        background: #555;
        cursor: not-allowed;
      }

      &:hover:not(.booked) {
        background: scale-color($fit-highlight, $lightness: 10%);
        color: #1e1e1e;
      }
    }
  }
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

  &:hover {
    filter: brightness(1.1);
  }
}

.expired {
  color: red;
  font-weight: bold;
  margin-top: 1rem;
}
</style>
