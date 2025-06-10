<template>
  <div class="modal-content">
    <div
      v-if="visible"
      class="custom-modal-overlay"
      @click="!persistent && $emit('close')"
    >
      <div class="custom-modal">
        <h2 v-if="title">{{ title }}</h2>
        <slot />
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  visible: Boolean,
  title: String,
  persistent: {
    type: Boolean,
    default: false,
  },
});
const emit = defineEmits(["close"]);
const close = () => emit("close");
</script>

<style scoped lang="scss">
@use "@/scss/styles" as *;

.custom-modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  z-index: 9999;
  display: flex;
  justify-content: center;
  align-items: center;
}
.modal-content {
  font-size: 1.4rem;
  background: #1c1c1c;
  border-radius: 1.5rem;

  p {
    margin: 1rem 0;
  }
}
.custom-modal {
  background: #1e1e1e;
  padding: 2rem;
  border-radius: 1.2rem;
  max-width: 500px;
  width: 90%;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.85);
  color: #fff;
  text-align: center;

  h2 {
    margin-bottom: 1rem;
    color: $fit-highlight;
    font-size: 2rem;
    font-weight: bold;
  }
}
</style>
