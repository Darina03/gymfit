<template>
  <div class="cart-page">
    <h1>Кошик</h1>

    <div v-if="cartItems.length === 0" class="empty-cart">Кошик порожній</div>

    <div v-else class="cart-list">
      <div v-for="(item, index) in cartItems" :key="index" class="cart-item">
        <div class="item-info">
          <div v-if="item.fieldName" class="info-line">
            <span class="label">Напрямок:</span>
            <span class="value">{{ item.fieldName }}</span>
          </div>

          <div class="info-line">
            <span class="label">Абонемент:</span>
            <span class="value">
              {{ formatMembershipType(item.membershipType) }}
              <template v-if="item.workoutAmount">
                – {{ item.workoutAmount }} тренувань
              </template>
            </span>
          </div>

          <div v-if="item.passType" class="info-line">
            <span class="label">Тип:</span>
            <span class="value">
              {{ formatPassType(item.passType) }}
            </span>
          </div>

          <div v-if="item.schedules" class="info-line">
            <span class="label">Дні занять:</span>
            <span class="value">{{ formatWorkoutDates(item.schedules) }}</span>
          </div>

          <div v-if="item.coachName" class="info-line">
            <span class="label">Тренер:</span>
            <span class="value">{{
                item.coachName + " " + item.coachSurname
              }}</span>
          </div>
          <div class="info-line">
            <span class="label">Ціна (1 шт):</span>
            <span class="value">
              <span v-if="activeDiscounts.length !== 0">
                <span class="old-price">₴{{ item.price.toFixed(2) }}</span>
                <span class="discounted-price">{{
                    item.discountedPrice.toFixed(2)
                  }}</span>
              </span>
              <span v-else>₴{{ item.price.toFixed(2) }}</span>
            </span>
          </div>
        </div>

        <div class="controls-block">
          <div class="quantity-controls">
            <button @click="decreaseQuantity(index)">-</button>
            <span>{{ item.quantity }}</span>
            <button @click="increaseQuantity(index)">+</button>
          </div>
          <p class="total-item-price">
            =
            <span v-if="activeDiscounts.length !== 0">
              <span class="old-price"
              >₴{{ (item.price * item.quantity).toFixed(2) }}</span
              >
              <span class="discounted-price"
              >₴{{ (item.discountedPrice * item.quantity).toFixed(2) }}</span
              >
            </span>
            <span v-else> ₴{{ (item.price * item.quantity).toFixed(2) }} </span>
          </p>
        </div>
      </div>
      <div
          class="discount-combined"
          v-if="activeDiscounts.length || !appliedDiscount"
      >
        <div class="discount-section" v-if="activeDiscounts.length">
          <h3>Застосовані знижки</h3>
          <ul>
            <li v-for="(discount, idx) in activeDiscounts" :key="idx">
              {{ discount.title }}: -{{ discount.discountPercent }}%
            </li>
          </ul>
        </div>

        <div class="discount-block">
          <div class="discount-controls">
            <div class="input-group">
              <input
                  v-model="promoCode"
                  :disabled="appliedDiscount"
                  placeholder="Введіть промокод"
              />
              <button @click="applyDiscount" :disabled="appliedDiscount">
                Застосувати
              </button>
            </div>
            <div v-if="discountMessage" class="discount-message">
              {{ discountMessage }}
            </div>
          </div>
        </div>
      </div>

      <div class="cart-summary">
        <h3>
          Сума:
          <span v-if="activeDiscounts.length !== 0" class="old-price"
          >₴{{ total.toFixed(2) }}</span
          >
          <span class="final-price">₴{{ discountedTotal.toFixed(2) }}</span>
        </h3>
        <button class="checkout-btn add-to-cart-btn" @click="proceedToPayment">
          <span v-if="!isLoadingBtn">Оплатити</span>
          <span v-if="isLoadingBtn" class="small">
            <LoadingSpinner class="small" />
          </span>
        </button>
      </div>
    </div>
    <CustomModal
        :visible="showModal"
        title="Помилка"
        @close="showModal = false"
    >
      <p>{{ modalMessage }}</p>
    </CustomModal>
  </div>
</template>

<script setup>
import { useCartStore } from "@/stores/cartStore";
import { ref, computed, onMounted } from "vue";
import axios from "axios";
import CustomModal from "@/components/CustomModal.vue";
import LoadingSpinner from "@/components/LoadingSpinner.vue";

const cart = useCartStore();
const cartItems = computed(() => cart.items);
const promoCode = ref("");
const discountMessage = ref("");
const showModal = ref(false);
const modalMessage = ref("");
const isLoadingBtn = ref(false);
const activeDiscounts = ref([]);
const appliedDiscount = ref(null);

const increaseQuantity = (index) => {
  cart.increaseQuantity(index);
  findApplicableDiscounts();
  applyDiscountsForMemberships();
};

const decreaseQuantity = (index) => {
  cart.decreaseQuantity(index);
  findApplicableDiscounts();
  applyDiscountsForMemberships();
};

const total = computed(() =>
    cartItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0),
);

const discountedTotal = computed(() => {
  return cartItems.value.reduce((sum, item) => {
    if (item.discountedPrice != null) {
      return sum + item.discountedPrice * item.quantity;
    } else {
      return sum + item.price * item.quantity;
    }
  }, 0);
});
const findApplicableDiscounts = async () => {
  const payload = mapCartItemsForDiscountCheck(cartItems.value);
  try {
    const response = await axios.post("/gymfit/discounts", payload);
    activeDiscounts.value = response.data;
    applyDiscountsForMemberships();
  } catch (error) {
    console.error("Failed to load active discounts", error);
  }
};

onMounted(findApplicableDiscounts);

const applyDiscountsForMemberships = () => {
  cartItems.value.forEach((item) => {
    let currentPrice = item.price;

    activeDiscounts.value.forEach((discount) => {
      if (discount.applicableItemIds.includes(item.id)) {
        const discountMultiplier = 1 - discount.discountPercent / 100;
        currentPrice = parseFloat(
            (currentPrice * discountMultiplier).toFixed(2),
        );
      }
    });

    item.discountedPrice = currentPrice;
  });
};

const applyDiscount = async () => {
  if (appliedDiscount.value) {
    discountMessage.value = "Промокод застосовано.";
    return;
  }

  try {
    const response = await axios.post("/gymfit/discounts/validate", {
      promocode: promoCode.value,
      cartItems: mapCartItemsForDiscountCheck(cartItems.value),
    });

    const discountPercent = response.data.percent;
    const applicableItemIds = response.data.applicableItemIds;

    activeDiscounts.value.push({
      title: `Промокод: ${promoCode.value}`,
      discountPercent,
      applicableItemIds,
    });

    appliedDiscount.value = {
      code: promoCode.value,
      percent: discountPercent,
    };
    applyDiscountsForMemberships();
    discountMessage.value = "Промокод застосовано!";
  } catch (error) {
    appliedDiscount.value = null;
    discountMessage.value = "Промокод не існує/недійсний!";
  }
};

const formatMembershipType = (membershipType) => {
  const membershipMap = {
    PERSONAL: "Персональні",
    GROUP: "Групові",
    GYM_ACCESS: "Доступ до спортзалу",
  };
  return membershipMap[membershipType.toUpperCase()] || membershipType;
};

const formatPassType = (passType) => {
  const passTypeMap = {
    MORNING_PASS: "Ранковий (6:00 - 13:00)",
    EVENING_PASS: "Вечірній (13:00 - 22:00)",
    ALL_DAY_PASS: "Цілодобовий (6:00 - 22:00)",
  };
  return passTypeMap[passType.toUpperCase()] || passType;
};

const formatWorkoutDates = (schedules) => {
  const dayMapFull = {
    MONDAY: "Пн",
    TUESDAY: "Вт",
    WEDNESDAY: "Ср",
    THURSDAY: "Чт",
    FRIDAY: "Пт",
    SATURDAY: "Сб",
    SUNDAY: "Нд",
  };

  if (!Array.isArray(schedules)) return "";

  return schedules
      .map(
          (s) =>
              `${dayMapFull[s.scheduledDay.toUpperCase()] || s.scheduledDay} ${s.classTime?.slice(0, 5) || ""}`,
      )
      .join(", ");
};

const mapCartItemsForDiscountCheck = (items) => {
  return items.map((item) => ({
    id: item.id,
    membershipType: item.membershipType,
    passType: item.passType || null,
    quantity: item.quantity || 1,
    price: item.discountedPrice != null ? item.discountedPrice : item.price,
    discountedPrice: null,
  }));
};

const mapCartItemsForCheckout = (items) => {
  return items.map((item) => ({
    id: item.membershipType === "GYM_ACCESS" ? null : item.id,
    membershipType: item.membershipType,
    passType: item.passType || null,
    quantity: item.quantity || 1,
    price: item.discountedPrice != null ? item.discountedPrice : item.price,
    discountedPrice: null,
  }));
};

const proceedToPayment = async () => {
  isLoadingBtn.value = true;
  try {
    const payload = mapCartItemsForCheckout(cartItems.value);
    console.log(payload);

    const response = await axios.post("/gymfit/checkout", payload);
    const checkoutUrl = response.data.checkoutUrl;
    window.location.href = checkoutUrl;
  } catch (error) {
    if (error.response?.data) {
      modalMessage.value = error.response.data;
    } else {
      modalMessage.value = "Упс... Щось сталося! Спробуйте ще раз!";
    }
    showModal.value = true;
  }
  isLoadingBtn.value = false;
};
</script>

<style scoped lang="scss">
@use "@/scss/styles" as *;

h1 {
  font-size: 2.8rem;
  letter-spacing: 1px;
  font-family: Impact, Charcoal, sans-serif;
}

.cart-page {
  width: 80%;
  margin: 7% auto 3rem auto;
  color: #fff;
  text-align: center;
}

.empty-cart {
  margin-top: 3rem;
  font-size: 1.5rem;
  color: #ccc;
}

.cart-list {
  display: flex;
  flex-direction: column;
  gap: 2rem;
  margin-top: 4rem;
}

.cart-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #1e1e1e;
  padding: 1.5rem;
  border-radius: 1rem;
  box-shadow: 0 0 10px $fit-highlight;
  flex-wrap: wrap;
}

.item-info {
  flex: 1 1 60%;
  text-align: left;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  font-size: 1.1rem;
}

.info-line {
  display: flex;
  gap: 0.5rem;
}

.label {
  min-width: 130px;
  font-weight: bold;
  color: $fit-highlight;
  font-size: 1.4rem;
}

.value {
  flex: 1;
  font-size: 1.4rem;
}

.controls-block {
  flex: 1 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}

.quantity-controls {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.quantity-controls button {
  background-color: #f39c12;
  border: none;
  color: white;
  padding: 0.5rem 0.8rem;
  border-radius: 0.5rem;
  font-size: 1.2rem;
  cursor: pointer;
}

.total-item-price {
  font-weight: bold;
  font-size: 1.2rem;
}

.cart-summary {
  text-align: right;
  margin-top: 2rem;
}

.checkout-btn {
  background: linear-gradient(135deg, $fit-highlight 0%, #017e7d 100%);
  color: #000;
  padding: 0.7rem 1.2rem;
  border: none;
  border-radius: 0.8rem;
  width: 180px;
  font-weight: bold;
  cursor: pointer;
  font-size: 1.1rem;
  transition: background 0.3s;

  &:hover {
    background: linear-gradient(
            135deg,
            lighten($fit-highlight, 10%) 0%,
            lighten(#017e7d, 10%) 100%
    );
  }
}

.discount-block {
  display: flex;
  justify-content: flex-end;
  align-items: flex-start;
  gap: 1rem;
  margin-top: 2rem;
  flex: 1 1 40%;

  .discount-controls {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }

  .input-group {
    display: flex;
    gap: 0.5rem;
  }

  input {
    padding: 0.5rem;
    border-radius: 0.5rem;
    border: 1px solid #888;
    background: #2c2c2c;
    color: white;
    font-size: 1.2rem;

    &:focus {
      outline: none;
      border-color: $fit-highlight;
    }
  }

  button {
    background-color: #f39c12;
    border: none;
    color: white;
    padding: 0.5rem 1rem;
    border-radius: 0.5rem;
    cursor: pointer;
  }

  .discount-message {
    color: $fit-highlight;
    font-weight: bold;
    margin-top: 0.2rem;
  }
}

.discount-section {
  padding: 1rem 2rem;
  margin-top: 2rem;
  background: #1e1e1e;
  border-radius: 1rem;
  box-shadow: 0 0 10px #ee902c;
  text-align: left;
  flex: 1 1 55%;
  //margin-top: 0;

  h3 {
    color: $fit-highlight;
    margin-bottom: 0.5rem;
    font-size: 2rem;
    font-weight: 600;
  }

  ul {
    list-style: disc;
    padding-left: 1.5rem;
    color: #ffffff;

    li {
      margin-bottom: 0.3rem;
      font-size: 1.45rem;
    }
  }
}

.old-price {
  text-decoration: line-through;
  color: #888;
  margin-right: 1rem;
  font-weight: normal;
}

.final-price {
  color: $fit-highlight;
  font-weight: bold;
}

.discount-combined {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-top: 2rem;
  gap: 2rem;
  flex-wrap: wrap;
}
</style>
