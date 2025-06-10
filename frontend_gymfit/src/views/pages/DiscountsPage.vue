<template>
  <div class="discounts-page">
    <h1>Доступні знижки</h1>
    <div class="discounts-list">
      <div
        v-for="discount in discounts"
        :key="discount.id"
        class="discount-card"
        :style="{ backgroundImage: `url(${discount.imageUrl})` }"
      >
        <div class="overlay">
          <h2>{{ discount.title }}</h2>
          <p>{{ discount.description }}</p>
          <div class="dates">
            <span
              >{{ formatDate(discount.startDate) }} →
              {{ formatDate(discount.endDate) }}</span
            >
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";

const discounts = ref([]);

const fetchDiscounts = async () => {
  // isLoading.value = true;
  try {
    const response = await axios.get("/gymfit/discounts/all");
    discounts.value = response.data;
  } catch (error) {
    console.log("Error occurred:" + error);
  }
};

onMounted(fetchDiscounts);

const formatDate = (dateStr) => {
  const date = new Date(dateStr);
  return date.toLocaleDateString(undefined, {
    month: "short",
    day: "numeric",
    year: "numeric",
  });
};
</script>

<style scoped lang="scss">
@use "@/scss/styles" as *;

.discounts-page {
  padding: 4rem 2rem;
  color: #fff;
  min-height: 100vh;
  width: 90%;
  margin: 4% auto;
  text-align: center;
}

h1 {
  font-family: Impact, sans-serif;
  font-size: 3rem;
  margin-bottom: 2rem;
  color: white;
}

.discounts-list {
  display: grid;
  gap: 2rem;
  margin-top: 5%;
  grid-template-columns: repeat(3, minmax(300px, 1fr));
}

.discount-card {
  position: relative;
  background-size: cover;
  background-position: center;
  height: 300px;
  border-radius: 1.2rem;
  overflow: hidden;
  box-shadow: 0 0 16px rgb(1, 126, 125);
  display: flex;
  align-items: stretch;
  transition: transform 0.3s;

  &:hover {
    transform: scale(1.015);
  }

  .overlay {
    background: rgba(0, 0, 0, 0.7);
    color: #fff;
    padding: 2rem;
    width: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    text-align: left;

    h2 {
      font-size: 2rem;
      margin-bottom: 1rem;
      color: $fit-highlight;
      font-family: Impact, sans-serif;
    }

    p {
      font-size: 1.4rem;
      margin-bottom: 0.8rem;
      line-height: 1.5;
    }

    .condition {
      font-style: italic;
      font-size: 1.2rem;
      color: #ccc;
    }

    .dates {
      margin-top: 1rem;
      font-size: 1rem;
      opacity: 0.8;
      color: #f39c12;
    }
  }
}
</style>
