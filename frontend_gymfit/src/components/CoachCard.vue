<template>
  <div class="coach-card">
    <img :src="coach.picUrl" alt="Coach" />
    <div class="info">
      <h3>{{ coach.name }} {{ coach.surname }}</h3>
      <p>{{ coach.fieldNames.join(", ") }}</p>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  coach: Object,
});
</script>

<style scoped lang="scss">
@use "@/scss/styles" as *;

.coach-card {
  position: relative;
  width: 280px;
  height: 400px;
  border-radius: 1rem;
  overflow: hidden;
  cursor: pointer;
  transition:
    transform 0.3s ease,
    box-shadow 0.3s ease;
  box-shadow: 0 0 15px rgba(0, 0, 0, 0.2);

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.4);
    border: 2px solid $fit-highlight;
  }

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .info {
    position: absolute;
    bottom: 0;
    width: 100%;
    padding: 1rem;
    background: rgba(0, 0, 0, 0.6);
    color: white;
    text-align: center;

    h3 {
      margin: 0;
      font-size: 1.5rem;
      font-weight: bold;
    }

    p {
      margin-top: 0.3rem;
      font-size: 1.1rem;
      color: #f39c12;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      max-width: 100%;
    }
  }
}
</style>
