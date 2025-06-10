<template>
  <div v-if="isLoading" class="spinner-wrapper">
    <LoadingSpinner />
  </div>
  <div v-if="!isLoading" class="user-profile">
    <div class="top-bar">
      <h1>Профіль</h1>
      <button class="edit-icon-btn" @click="goToEditProfile" title="Редагувати">
        ✏️
      </button>
    </div>
    <div class="user-info">
      <div class="info-row">
        <span class="label">Ім'я:</span> <span>{{ client.client.name }}</span>
      </div>
      <div class="info-row">
        <span class="label">Прізвище:</span>
        <span>{{ client.client.surname }}</span>
      </div>
      <div class="info-row">
        <span class="label">Дата народження:</span>
        <span>{{ client.client.birthday }}</span>
      </div>
      <div class="info-row">
        <span class="label">Номер телефону:</span>
        <span>{{ client.client.phoneNumber }}</span>
      </div>
      <div class="info-row">
        <span class="label">Електронна адреса:</span>
        <span>{{ client.client.email }}</span>
      </div>

      <div class="info-row gym-membership">
        <div class="gym-info">
          <span class="label">Абонементи доступу до спортзалу: </span>
          <span
              :class="{
              active: client.gymMembership,
              inactive: !client.gymMembership,
            }"
          >
            {{ client.gymMembership != null ? "Активний" : "Неактивний" }}
          </span>
          <div v-if="client.gymMembership" class="valid-till">
            {{ passTypeMap[client.gymMembership.type] }} (Дійсний до:
            {{ client.gymMembership.endDate }})
          </div>
        </div>
        <button class="buy-btn" @click="buyGymMembership">
          {{ client.gymMembership ? "Продовжити" : "Купити" }}
        </button>
      </div>

      <div v-if="showGymExtensionForm" class="extend-form">
        <h3>Оберіть абонемент</h3>

        <label>
          Тип:
          <select v-model="selectedType">
            <option value="MORNING_PASS">Ранковий (6:00 - 13:00)</option>
            <option value="EVENING_PASS">Вечірній (13:00 - 22:00)</option>
            <option value="ALL_DAY_PASS">Цілодобвий (6:00 - 22:00)</option>
          </select>
        </label>

        <label>
          Кількість (місяців):
          <input type="number" min="1" v-model="quantity" />
        </label>

        <p class="price">Ціна: {{ prices[selectedType] * quantity }}₴</p>

        <p class="note">* Кожен абонемент дійсний рівно 4 тижні!.</p>

        <div class="button-group">
          <button class="buy-btn" @click="addToCart">В кошик</button>
          <button class="cancel-btn" @click="cancelGymExtension">Назад</button>
        </div>
      </div>
    </div>

    <div class="accordion" @click="toggleAccordion('group')">
      <div class="accordion-header">
        <span
        >Абонементи групових тренувань({{
            client.groupMemberships.length
          }})</span
        >
        <span class="arrow" :class="{ open: isGroupOpen }">&#9662;</span>
      </div>
      <div v-if="isGroupOpen" class="accordion-body">
        <div
            class="membership-card"
            v-for="g in client.groupMemberships"
            :key="g.id"
        >
          <h3>{{ g.fieldName }}</h3>
          <div class="info-row">
            <span class="label">Тип:</span>
            <span>{{ formatMembershipType(g.type) }}</span>
          </div>
          <div class="info-row">
            <span class="label">Тренер:</span>
            <span>{{ g.coachName + " " + g.coachSurname }}</span>
          </div>
          <div class="info-row">
            <span class="label">к-сть занять:</span>
            <span>{{
                g.leftWorkoutAmount + "/" + g.initialWorkoutAmount
              }}</span>
          </div>
          <div class="info-row">
            <span class="label">Дійсний до:</span>
            <span>{{ g.endDate }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="accordion" @click="toggleAccordion('personal')">
      <div class="accordion-header">
        <span
        >Абонементи персональних тренувань ({{
            client.personalMemberships.length
          }})</span
        >
        <span class="arrow" :class="{ open: isPersonalOpen }">&#9662;</span>
      </div>
      <div v-if="isPersonalOpen" class="accordion-body">
        <div
            class="membership-card"
            v-for="p in client.personalMemberships"
            :key="p.id"
        >
          <h3>{{ p.fieldName }}</h3>
          <div class="info-row">
            <span class="label">Тип:</span>
            <span>{{ formatMembershipType(p.type) }}</span>
          </div>
          <div class="info-row">
            <span class="label">Тренер:</span>
            <span>{{ p.coachName + " " + p.coachSurname }}</span>
          </div>
          <div class="info-row">
            <span class="label">К-сть занять:</span>
            <span>{{
                p.leftWorkoutAmount + "/" + p.initialWorkoutAmount
              }}</span>
          </div>
          <div class="info-row">
            <span class="label">Дійсний до:</span>
            <span>{{ p.endDate }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="accordion" @click="toggleAccordion('trainers')">
      <div class="accordion-header">
        <span>Персональні тренери ({{ client.coaches.length }})</span>
        <span class="arrow" :class="{ open: isTrainersOpen }">&#9662;</span>
      </div>
      <div v-if="isTrainersOpen" class="accordion-body">
        <div class="trainer-card" v-for="t in client.coaches" :key="t.coach.id">
          <img :src="t.coach.picUrl" alt="Фото тренера" class="trainer-photo" />
          <div class="trainer-info">
            <h3>{{ t.coach.name }} {{ t.coach.surname }}</h3>
            <div class="session-count">
              <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="icon"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
              >
                <circle cx="12" cy="12" r="10"></circle>
                <polyline points="12 6 12 12 16 14"></polyline>
              </svg>
              <span>Автоматична відписка {{ t.endDate }}</span>
            </div>
            <div class="button-group">
              <button
                  class="unenroll-btn"
                  @click="unenrollCoach(t.coach.id)"
                  :disabled="!canUnenroll(t.endDate)"
              >
                Відписатися
              </button>
              <button
                  @click="
                  goToMemberships(
                    `${t.coach.name} ${t.coach.surname} `,
                    'PERSONAL',
                    '',
                  )
                "
                  class="extend-btn"
              >
                Продовжити
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="accordion" @click="toggleAccordion('classes')">
      <div class="accordion-header">
        <span>Групові класи ({{ client.enrolledGroupClasses.length }})</span>
        <span class="arrow" :class="{ open: isClassesOpen }">&#9662;</span>
      </div>
      <div v-if="isClassesOpen" class="accordion-body group-classes-body">
        <div
            class="class-card"
            v-for="g in client.enrolledGroupClasses"
            :key="g.groupClass.id"
        >
          <div class="class-header">
            <h3>{{ g.groupClass.fieldName }}</h3>
            <div class="session-count">
              <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="icon"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
              >
                <circle cx="12" cy="12" r="10"></circle>
                <polyline points="12 6 12 12 16 14"></polyline>
              </svg>
              <span>Автоматична відписка {{ g.endDate }} </span>
            </div>
          </div>
          <p class="coach-info">
            <strong>Тренер:</strong> {{ g.groupClass.coachName }}
            {{ g.groupClass.coachSurname }}
          </p>
          <div class="button-group">
            <button
                @click="unenrollGroup(g.groupClass.id)"
                class="unenroll-btn"
                :disabled="!canUnenroll(g.endDate)"
                :title="
                g.canUnenroll
                  ? 'Unenroll from this class'
                  : 'You cannot unenroll yet'
              "
            >
              Відписатися
            </button>
            <button
                @click="
                goToMemberships(
                  `${g.groupClass.coachName} ${g.groupClass.coachSurname} `,
                  'GROUP',
                  g.fieldName,
                )
              "
                class="extend-btn"
            >
              Продовжити
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { useToast } from "vue-toastification";
import { useCartStore } from "@/stores/cartStore";
import axios from "axios";
import LoadingSpinner from "@/components/LoadingSpinner.vue";

const cartStore = useCartStore();
const toast = useToast();
const router = useRouter();

const goToEditProfile = () => {
  router.push("/edit-profile");
};
const isLoading = ref(true);

const showGymExtensionForm = ref(false);
const selectedType = ref("MORNING_PASS");
const quantity = ref(1);

const prices = {
  MORNING_PASS: 450,
  EVENING_PASS: 550,
  ALL_DAY_PASS: 1000,
};

const passTypeMap = {
  MORNING_PASS: "Ранковий (6:00 - 13:00)",
  EVENING_PASS: "Вечірній (13:00 - 22:00)",
  ALL_DAY_PASS: "Цілодобовий (6:00 - 22:00)",
};

const formatMembershipType = (membershipType) => {
  const membershipMap = {
    PERSONAL: "Персональні",
    GROUP: "Групові",
    GYM_ACCESS: "Доступ до спортзалу",
  };
  return membershipMap[membershipType.toUpperCase()] || membershipType;
};

const passTypeId = {
  MORNING_PASS: -1,
  EVENING_PASS: -2,
  ALL_DAY_PASS: -3,
};
const isGroupOpen = ref(false);
const isPersonalOpen = ref(false);

const client = ref(null);
const showPopup = ref(false);
const isTrainersOpen = ref(false);
const isClassesOpen = ref(false);

const fetchClientInfo = async () => {
  isLoading.value = true;
  let endpoint = "/gymfit/clients/profile-info";

  try {
    const res = await axios.get(endpoint);
    client.value = res.data;
  } catch (err) {
    console.error("Failed to fetch client info:", err);
  } finally {
    isLoading.value = false;
  }
};

onMounted(fetchClientInfo);

const toggleAccordion = (type) => {
  if (type === "group") isGroupOpen.value = !isGroupOpen.value;
  else if (type === "personal") isPersonalOpen.value = !isPersonalOpen.value;
  else if (type === "trainers") isTrainersOpen.value = !isTrainersOpen.value;
  else if (type === "classes") isClassesOpen.value = !isClassesOpen.value;
};

const buyGymMembership = () => {
  showGymExtensionForm.value = true;
};

const cancelGymExtension = () => {
  showGymExtensionForm.value = false;
};

const goToMemberships = (coach, type, field) => {
  router.push({
    path: "/memberships",
    query: { coach: coach, type: type, field: field },
  });
};

const unenrollGroup = async (groupId) => {
  let endpoint = `/gymfit/group-classes/unenroll/${groupId}`;
  try {
    const res = await axios.post(endpoint);
    await fetchClientInfo();
  } catch (err) {
    console.error("Failed to unenroll:", err);
  }
};

const unenrollCoach = async (coachId) => {
  let endpoint = `/gymfit/coaches/unenroll/${coachId}`;
  try {
    const res = await axios.post(endpoint);
    await fetchClientInfo();
  } catch (err) {
    console.error("Failed to unenroll:", err);
  }
};

const addToCart = () => {
  const membership = {
    id: passTypeId[selectedType.value],
    membershipType: "GYM_ACCESS",
    passType: selectedType.value,
    passLabel: passTypeMap[selectedType.value],
    quantity: quantity.value,
    price: prices[selectedType.value],
  };

  cartStore.addToCart(membership);
  toast.success("Абонемент додано в кошик!");

  setTimeout(() => {
    showPopup.value = false;
  }, 2000);
};

const stripTime = (date) => {
  return new Date(date.getFullYear(), date.getMonth(), date.getDate());
};

const canUnenroll = (endDate) => {
  const today = stripTime(new Date());
  const end = stripTime(new Date(endDate));

  const sevenDaysAgo = new Date(end);
  sevenDaysAgo.setDate(end.getDate() - 7);

  return today >= sevenDaysAgo;
};
</script>

<style scoped lang="scss">
@use "@/scss/styles" as *;

$bg-dark: #1e1e1e;
$bg-light: #2a2a2a;
$bg-highlight: #333;
$text-light: #f1f1f1;
$text-muted: #aaa;
$accent: $fit-highlight;
$radius: 1.2rem;

.spinner-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 70vh;
}

.user-profile {
  margin: 6% auto;
  width: 95%;
  max-width: 1000px;
  color: $text-light;
  padding: 3rem;
  background: $bg-dark;
  border-radius: 2rem;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.6);

  h1 {
    text-align: center;
    color: $accent;
    margin-bottom: 2rem;
    font-size: 2.8rem;
    letter-spacing: 1px;
    font-family: Impact, Charcoal, sans-serif;
  }

  .top-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1.5rem;

    .edit-icon-btn {
      background: transparent;
      border: none;
      color: $accent;
      font-size: 1.8rem;
      cursor: pointer;
      transition: transform 0.2s;

      &:hover {
        transform: scale(1.2);
      }
    }
  }

  .user-info {
    display: flex;
    flex-direction: column;
    gap: 1.2rem;

    .info-row {
      display: flex;
      justify-content: space-between;
      padding: 0.7rem 0;
      border-bottom: 1px solid $bg-highlight;
      font-size: 1.5rem;

      .label {
        color: $accent;
        font-weight: 600;
      }
    }

    .gym-membership {
      display: flex;
      justify-content: space-between;
      align-items: center;
      background: $bg-light;
      padding: 1rem;
      border-radius: $radius;
      margin-top: 1rem;

      .gym-info {
        .active {
          color: limegreen;
        }

        .inactive {
          color: crimson;
        }

        .valid-till {
          font-size: 1.1rem;
          color: $text-muted;
          margin-top: 0.3rem;
        }
      }

      .buy-btn {
        @include button-style($accent, $bg-highlight);
      }
    }

    .extend-form {
      margin-top: 1rem;
      font-size: 1.3rem;
      background: $bg-light;
      padding: 1.5rem;
      border-radius: $radius;

      label {
        display: block;
        margin: 1rem 0 0.5rem;
        font-weight: 500;
      }

      select,
      input[type="number"] {
        width: 100%;
        padding: 0.5rem;
        background: #1a1a1a;
        color: $text-light;
        border: 1px solid $bg-highlight;
        border-radius: 0.6rem;
      }

      .price {
        margin: 1rem 0;
        font-weight: bold;
        color: $accent;
      }

      .note {
        font-size: 1.2rem;
        color: $text-muted;
      }

      .button-group {
        display: flex;
        gap: 1rem;
        margin-top: 1rem;

        .buy-btn {
          @include button-style($accent, $bg-highlight);
        }

        .cancel-btn {
          @include button-style(#555, $text-light);
        }
      }
    }
  }

  .accordion {
    margin-top: 2rem;
    border-radius: $radius;
    background: $bg-light;
    overflow: hidden;
    transition: all 0.3s ease;

    .accordion-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 1.2rem;
      background: $bg-highlight;
      cursor: pointer;
      font-size: 1.5rem;
      font-weight: bold;
      color: $accent;

      .arrow {
        transition: transform 0.3s ease;

        &.open {
          transform: rotate(180deg);
        }
      }
    }

    .accordion-body {
      padding: 1rem 1.5rem;
      background: $bg-dark;

      .trainer-card {
        display: flex;
        align-items: flex-start;
        gap: 1rem;

        .trainer-photo {
          width: 100px;
          height: 100px;
          object-fit: cover;
          border-radius: 0.75rem;
          flex-shrink: 0;
          border: 3px solid #444;
        }

        .trainer-info {
          flex: 1;
          display: flex;
          flex-direction: column;

          h3 {
            margin-bottom: 0.5rem;
            font-size: 1.6rem;
            color: $accent;
          }
        }
      }

      .membership-card,
      .trainer-card,
      .class-card {
        background: #262626;
        border-radius: 1rem;
        padding: 1rem;
        margin-bottom: 1.2rem;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);

        h3 {
          margin-bottom: 0.5rem;
          font-size: 1.6rem;
          color: $accent;
        }

        .info-row {
          display: flex;
          justify-content: space-between;
          font-size: 1.35rem;
          padding: 0.3rem 0;
        }

        .coach-info {
          margin-top: 0.8rem;
          font-size: 1.35rem;
        }

        .session-count {
          display: flex;
          align-items: center;
          gap: 0.5rem;
          font-size: 1.2rem;

          .icon {
            width: 1rem;
            height: 1rem;
            stroke: $accent;
          }
        }

        .button-group {
          margin-top: 1rem;
          display: flex;
          gap: 0.8rem;

          button {
            flex: 1;
            @include button-style($accent, $text-light);

            &.unenroll-btn {
              background: crimson;
              border: none;

              &:disabled {
                background: #444;
                cursor: not-allowed;
              }
            }

            &.extend-btn {
              background: #23afa3;
              color: $bg-highlight;
            }
          }
        }
      }
    }
  }
}
</style>
