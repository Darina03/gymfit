<template>
  <div class="payment-status">
    <div class="status-box success">
      <h2>Оплата успішна! 🎉</h2>
      <p>Дякуємо, що обиражте нас! Замовлення виконано.</p>
      <button @click="goToProfile">До профілю</button>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import axios from "axios";
import { useCartStore } from "@/stores/cartStore";

const router = useRouter();
const route = useRoute();
const cart = useCartStore();

onMounted(async () => {
  const sessionId = route.query.sessionId;

  if (!sessionId) {
    await router.replace("/");
    return;
  }

  try {
    const res = await axios.get("/gymfit/payment-success/verify-payment", {
      params: { sessionId },
    });

    if (res.data === true) {
      cart.clearCart();
    } else {
      await router.replace("/");
    }
  } catch (e) {
    console.error("Verification error:", e);
    await router.replace("/profile");
  }
});

const goToProfile = () => router.push("/profile");
</script>

<style scoped lang="scss">
.payment-status {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  color: #fff;
}

.status-box {
  background: #1e1e1e;
  padding: 2rem;
  border-radius: 1rem;
  text-align: center;
  box-shadow: 0 0 15px rgba(0, 0, 0, 0.5);
  width: 100%;
  max-width: 500px;

  h2 {
    margin-bottom: 1rem;
    font-size: 2.5rem;
  }

  p {
    margin-bottom: 2rem;
    font-size: 1.5rem;
    color: #ccc;
  }

  button {
    background-color: #23afa3;
    color: white;
    border: none;
    padding: 0.6rem 1.2rem;
    border-radius: 0.5rem;
    font-size: 1.3rem;
    cursor: pointer;

    &:hover {
      background-color: #23afa3;
    }
  }

  &.success {
    border-left: 4px solid #23afa3;
  }
}
</style>
