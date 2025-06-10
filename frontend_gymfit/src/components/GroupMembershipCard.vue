<template>
  <div class="membership-card">
    <div class="info">
      <h3>{{ membership.fieldName }}</h3>

      <div class="info-row">
        <span class="label">Тип:</span>
        <span class="value">Групові</span>
      </div>

      <div class="info-row">
        <span class="label">Дні тренувань:</span>
        <div class="value badges">
          <span
            v-for="(day, index) in formattedWorkoutDays"
            :key="index"
            class="badge"
          >
            {{ day }}
          </span>
        </div>
      </div>

      <div class="info-row">
        <span class="label">Учасників:</span>
        <span class="value"
          >{{ membership.currentEnrollments }}/{{
            membership.maxParticipants
          }}</span
        >
      </div>

      <div class="info-row">
        <span class="label">К-сть занять:</span>
        <span class="value">{{ membership.workoutAmount }}</span>
      </div>

      <div class="info-row">
        <span class="label">Тренер:</span>
        <span class="value">{{
          membership.coachName + " " + membership.coachSurname
        }}</span>
      </div>

      <div class="info-row">
        <span class="label price">Ціна:</span>
        <span class="value price">${{ membership.price }}</span>
      </div>
    </div>

    <button @click="addToCart" class="add-to-cart-btn" :disabled="!isLoggedIn">
      В кошик
    </button>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { useCartStore } from "@/stores/cartStore.js";
import { useAuthStore } from "@/stores/auth.js";

const props = defineProps({
  membership: Object,
});
const emit = defineEmits(["added"]);

const cart = useCartStore();
const auth = useAuthStore();

const isLoggedIn = computed(() => !!auth.token);
const isAdded = ref(false);

const dayMap = {
  MONDAY: "Пн",
  TUESDAY: "Вт",
  WEDNESDAY: "Ср",
  THURSDAY: "Чт",
  FRIDAY: "Пт",
  SATURDAY: "Сб",
  SUNDAY: "Нд",
};

const formattedWorkoutDays = computed(() => {
  return props.membership.schedules.map((schedule) => {
    const day =
      dayMap[schedule.scheduledDay?.toUpperCase()] || schedule.scheduledDay;
    const time = schedule.classTime?.slice(0, 5);
    return `${day} ${time}`;
  });
});

const addToCart = () => {
  if (!isLoggedIn.value) return;
  cart.addToCart(props.membership);
  isAdded.value = true;
  emit("added");
};
</script>

<style scoped lang="scss">
@use "@/scss/styles" as *;

.membership-card {
  background-image:
    linear-gradient(rgba(0, 0, 0, 0.8), rgba(0, 0, 0, 0.8)),
    url("https://i.pinimg.com/736x/61/84/cc/6184cc9bdd16e6dcfb1d7221b49e89a6.jpg");
  background-size: cover;
  background-position: center;
  color: #ffffff;
  padding: 1.5rem;
  border-radius: 1.5rem;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.6);
  width: 350px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  border: 1px solid #2c2c2c;
  transition: transform 0.3s ease;

  &:hover {
    transform: translateY(-5px);
  }

  h3 {
    font-size: 1.7rem;
    color: $fit-highlight;
    margin-bottom: 1rem;
    font-family: Impact, sans-serif;
    text-align: center;
  }

  .info {
    display: flex;
    flex-direction: column;
    gap: 0.6rem;

    .info-row {
      display: flex;
      align-items: flex-start;
      gap: 1rem;

      .label {
        width: 150px;
        text-align: right;
        font-weight: bold;
        color: $fit-highlight;
        flex-shrink: 0;
        font-size: 1.3rem;
      }

      .value {
        flex: 1;
        text-align: left;
        font-size: 1.3rem;
      }

      .price {
        color: #f39c12;
        font-size: 1.5rem;
      }

      .badges {
        display: flex;
        flex-wrap: wrap;
        gap: 0.4rem;
      }

      .badge {
        background-color: $fit-highlight;
        color: #000;
        padding: 0.2rem 0.5rem;
        border-radius: 0.5rem;
        font-size: 1rem;
        font-weight: bold;
        white-space: nowrap;
      }
    }
  }

  .add-to-cart-btn {
    margin-top: 1rem;
    background: linear-gradient(135deg, $fit-highlight 0%, #017e7d 100%);
    color: #000;
    padding: 0.6rem 1rem;
    border: none;
    border-radius: 0.8rem;
    font-weight: bold;
    cursor: pointer;
    transition: background 0.3s;
    font-size: 1.5rem;

    &:hover {
      background: linear-gradient(135deg, $fit-highlight 0%, #017e7d 100%);
    }

    &:disabled {
      background: #555;
      cursor: not-allowed;
      opacity: 0.6;
    }
  }
}
</style>
