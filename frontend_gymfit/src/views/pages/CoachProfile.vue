<script setup>
import { computed, onMounted, ref } from "vue";
import { useAuthStore } from "@/stores/auth";
import axios from "axios";
import { useToast } from "vue-toastification";
import { useRoute } from "vue-router";
import LoadingSpinner from "@/components/LoadingSpinner.vue";
import { a } from "vue-the-mask/src/tokens.js";

const route = useRoute();

const coachId = Number(route.params.id);
const auth = useAuthStore();
const toast = useToast();

const coach = ref(null);
const comments = ref([]);
const currentUserRating = ref(0);
const userComment = ref("");
const isLoading = ref(true);
const isLoggedIn = computed(() => auth.token && auth.token !== "");

function calculateAge(birthDateStr) {
  const birthDate = new Date(birthDateStr);
  const today = new Date();
  let age = today.getFullYear() - birthDate.getFullYear();
  const m = today.getMonth() - birthDate.getMonth();
  if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
    age--;
  }
  return age;
}

const formattedDate = (dateString) => {
  const date = new Date(dateString);
  return `${date.getDate().toString().padStart(2, "0")}-${(date.getMonth() + 1)
    .toString()
    .padStart(2, "0")}-${date.getFullYear()}`;
};
const fetchCoachInfo = async () => {
  try {
    const { data: coachData } = await axios.get(
      `/gymfit/coaches/profile/${coachId}`,
    );
    coach.value = {
      ...coachData.coachInfo,
      age: calculateAge(coachData.coachInfo.birthday),
    };
    comments.value = coachData.comments;
    currentUserRating.value = coachData.coachRatingByClient;
  } catch (error) {
    console.error("Initialization error:", error);
  } finally {
    isLoading.value = false;
  }
};

onMounted(fetchCoachInfo);

const submitRating = async () => {
  try {
    await axios.post(`/gymfit/ratings/${coachId}`, {
      rating: currentUserRating.value,
    });
    const { data: coachData } = await axios.get(
      `/gymfit/coaches/profile/${coachId}`,
    );
    coach.value = {
      ...coachData,
      age: calculateAge(coachData.birthday),
    };
    await fetchCoachInfo();
    toast.success("Оцінку збережено!");
  } catch (error) {
    console.error("Rating error:", error);
    toast.error("Не вдалося зберегти оцінку.");
  }
};

const submitComment = async () => {
  if (!userComment.value.trim()) return;

  try {
    await axios.post(`/gymfit/comments`, {
      coachId: coachId,
      content: userComment.value,
    });
    await fetchCoachInfo();
    userComment.value = "";
    toast.success("Коментар додано!");
  } catch (error) {
    console.error("Comment error:", error);
    toast.error("Не вдалося додати коментар.");
  }
};
</script>

<template>
  <div v-if="isLoading" class="spinner-wrapper">
    <LoadingSpinner />
  </div>
  <div v-else-if="coach" class="coach-profile-wrapper">
    <div class="coach-profile">
      <div class="profile-card">
        <div
          class="profile-image"
          :style="{ backgroundImage: `url(${coach.picUrl})` }"
        ></div>
        <div class="profile-info">
          <h2>{{ coach.name }} {{ coach.surname }}</h2>
          <p class="rank">{{ coach.rank }}</p>

          <div class="rating">
            <span
              v-for="n in 5"
              :key="n"
              class="star"
              :class="{ filled: n <= Math.round(coach.averageRating) }"
              >★</span
            >
            <span class="avg">(avg: {{ coach.averageRating.toFixed(1) }})</span>
          </div>

          <p class="motto">"{{ coach.motto }}"</p>
          <p class="description">{{ coach.description }}</p>

          <ul class="details">
            <li><strong>Вік:</strong> {{ coach.age }}</li>
            <li>
              <strong>Інстаграм: </strong>
              <a
                :href="`https://instagram.com/${coach.instagram.slice(1)}`"
                target="_blank"
                >{{ coach.instagram }}</a
              >
            </li>
            <li><strong>Досвід:</strong> {{ coach.experience }} р.</li>
            <li>
              <strong>Напрямки:</strong>
              <span class="tags">
                <span
                  v-for="(spec, idx) in coach.fieldNames"
                  :key="idx"
                  class="tag"
                  >{{ spec }}</span
                >
              </span>
            </li>
          </ul>
        </div>
      </div>

      <div class="interaction">
        <div class="rate-section" v-if="isLoggedIn">
          <h3>Оцініть тренера</h3>
          <div class="stars">
            <span
              v-for="n in 5"
              :key="n"
              class="rate-star"
              :class="{ filled: n <= currentUserRating }"
              @click="currentUserRating = n"
              >★</span
            >
          </div>
          <button @click="submitRating" class="submit-btn">Оцінити</button>
        </div>

        <div class="comment-section" v-if="isLoggedIn">
          <h3>Залишити коментар</h3>
          <textarea
            v-model="userComment"
            rows="3"
            placeholder="Your comment..."
          />
          <button @click="submitComment" class="submit-btn">Відправити</button>
        </div>

        <div class="comments-list">
          <h3>Коментарі</h3>
          <div v-if="comments.length === 0">
            Поки що у цього тренера нема відгуків.
          </div>
          <div
            class="comment"
            v-for="(comment, index) in comments"
            :key="index"
          >
            <p class="meta">
              <strong>{{ comment.clientFullName }}</strong> —
              <em>{{ formattedDate(comment.creationDate) }}</em>
            </p>
            <p class="text">{{ comment.content }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use "@/scss/styles" as *;

.spinner-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 70vh;
}

.coach-profile-wrapper {
  display: flex;
  margin-top: 5%;
  justify-content: center;
  padding: 3rem 1rem;
  color: #fff;
}

.coach-profile {
  max-width: 1200px;
  width: 100%;

  .profile-card {
    display: flex;
    flex-direction: row;
    background: #1a1a1a;
    border-radius: 20px;
    overflow: hidden;
    margin-bottom: 3rem;
    box-shadow: 0 0 20px rgba(0, 0, 0, 0.6);

    .profile-image {
      width: 45%;
      min-height: 650px;
      background-size: cover;
      background-position: center;
    }

    .profile-info {
      width: 55%;
      padding: 2rem;

      h2 {
        font-size: 2.7rem;
        color: $fit-highlight;
        margin-bottom: 0.5rem;
      }

      .rank {
        font-size: 1.4rem;
        color: #ccc;
        margin-bottom: 1rem;
      }

      .rating {
        display: flex;
        align-items: center;
        margin-bottom: 1rem;

        .star {
          font-size: 1.8rem;
          color: #555;

          &.filled {
            color: gold;
          }
        }

        .avg {
          margin-left: 0.5rem;
          font-size: 1.2rem;
          color: #aaa;
        }
      }

      .motto {
        font-style: italic;
        font-size: 1.4rem;
        color: #f2f2f2;
        margin-bottom: 1rem;
      }

      .description {
        text-align: justify;
        margin-bottom: 1.2rem;
        font-size: 1.3rem;
      }

      .details {
        list-style: none;
        padding: 0;
        margin-bottom: 2rem;
        font-size: 1.3rem;

        li {
          margin-bottom: 0.7rem;

          strong {
            color: $fit-highlight;
          }
        }

        .tags {
          display: flex;
          flex-wrap: wrap;
          gap: 0.5rem;
          margin-top: 5%;

          .tag {
            background: $fit-highlight;
            padding: 0.3rem 0.7rem;
            border-radius: 15px;
            font-size: 1.2rem;
            color: black;
            font-weight: bold;
          }
        }
      }

      .book-btn {
        background-color: $fit-highlight;
        border: none;
        padding: 0.8rem 1.5rem;
        border-radius: 10px;
        font-size: 1.1rem;
        font-weight: bold;
        color: white;
        cursor: pointer;
        transition: background-color 0.3s;

        &:hover {
          background-color: darken($fit-highlight, 10%);
        }
      }
    }
  }

  .interaction {
    background: #1a1a1a;
    padding: 2rem;
    border-radius: 15px;

    h3 {
      margin-bottom: 0.5rem;
      color: $fit-highlight;
      font-size: 2rem;
    }

    .rate-section {
      margin-bottom: 2rem;

      .stars {
        display: flex;
        gap: 0.4rem;
        margin-bottom: 1rem;

        .rate-star {
          font-size: 2rem;
          cursor: pointer;
          color: #555;

          &.filled {
            color: gold;
          }
        }
      }
    }

    .comment-section {
      textarea {
        width: 100%;
        padding: 0.8rem;
        border-radius: 10px;
        resize: none;
        font-size: 1.2rem;
        margin-bottom: 0.8rem;
        background: #333;
        color: white;
        border: none;
      }
    }

    .submit-btn {
      background-color: $fit-highlight;
      border: none;
      padding: 0.6rem 1.2rem;
      border-radius: 10px;
      color: black;
      font-weight: bold;
      cursor: pointer;
      font-size: 1.2rem;

      &:hover {
        background-color: darken($fit-highlight, 10%);
      }
    }

    .comments-list {
      margin-top: 2rem;

      .comment {
        margin-bottom: 1.2rem;
        background: #222;
        padding: 1rem;
        border-radius: 10px;

        .meta {
          font-size: 1rem;
          color: #aaa;
          margin-bottom: 0.3rem;
        }

        .text {
          font-size: 1.2rem;
        }
      }
    }
  }
}
</style>
