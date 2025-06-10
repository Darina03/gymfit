import { defineStore } from "pinia";
import { ref, watch } from "vue";

export const useCartStore = defineStore("cart", () => {
  const items = ref(JSON.parse(localStorage.getItem("cart_items") || "[]"));

  const save = () => {
    localStorage.setItem("cart_items", JSON.stringify(items.value));
  };

  watch(items, save, { deep: true });

  const addToCart = (membership) => {
    membership.discountedPrice = null;
    const existing = items.value.find((item) => item.id === membership.id);
    if (membership.membershipType === "GYM_ACCESS") {
      if (existing) {
        existing.quantity += membership.quantity;
      } else {
        items.value.push({ ...membership, quantity: membership.quantity });
      }
    } else {
      if (existing) {
        existing.quantity += 1;
      } else {
        items.value.push({ ...membership, quantity: 1 });
      }
    }
  };

  const increaseQuantity = (index) => {
    items.value[index].quantity += 1;
  };

  const decreaseQuantity = (index) => {
    if (items.value[index].quantity > 1) {
      items.value[index].quantity -= 1;
    } else {
      items.value.splice(index, 1);
    }
  };

  const clearCart = () => {
    items.value = [];
  };

  return {
    items,
    addToCart,
    increaseQuantity,
    decreaseQuantity,
    clearCart,
  };
});
