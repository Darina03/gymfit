<template>
  <div class="loader"></div>
</template>

<style lang="scss" scoped>
@use "@/scss/styles" as *;

.loader {
  border: 6px solid #333;
  border-top: 6px solid $fit-highlight;
  border-radius: 50%;
  width: 60px;
  height: 60px;
  animation: spin 1s linear infinite;
  margin: 20px auto;
}
@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
.loader.small {
  width: 20px;
  height: 20px;
  border-width: 3px;
  margin: 0 auto;
}
</style>
