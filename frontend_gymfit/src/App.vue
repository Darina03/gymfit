<script setup>
import NavBar from "@/components/NavBar.vue";
import Footer from "./components/Footer.vue";
</script>

<template>
  <div class="app-layout">
    <main class="main-content">
      <NavBar />
      <router-view />
    </main>
    <Footer />
  </div>
</template>

<style lang="scss">
body,
html,
#app {
  height: 100%;
  margin: 0;
  padding: 0;
}
body {
  background-image: url("@/assets/wallpapersden.com_black-triangle-vector-folds_1920x1200.jpg");
}
.app-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.main-content {
  flex: 1;
}
</style>
