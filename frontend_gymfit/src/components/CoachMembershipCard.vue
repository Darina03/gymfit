<template>
  <div class="membership-card">
    <div class="info">
      <h3>{{ membership.fieldName }}</h3>

      <div class="info-row">
        <span class="label">Тип:</span>
        <span class="value">Персональні</span>
      </div>

      <div class="info-row">
        <span class="label">К-сть занять:</span>
        <span class="value">{{ membership.workoutAmount }}</span>
      </div>

      <div class="info-row">
        <span class="label">Тренер:</span>
        <span class="value"
          >{{ membership.coachName }} {{ membership.coachSurname }}</span
        >
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
import { computed, ref } from "vue";
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
