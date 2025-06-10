import "./style.css";
import { createApp } from "vue";
import App from "./App.vue";
import router from "./router/index.js";
import axios from "axios";
import { createPinia } from "pinia";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap.bundle.min.js";
import "@fortawesome/fontawesome-free/css/all.css";
import { mask } from "vue-the-mask";
import Toast from "vue-toastification";
import "vue-toastification/dist/index.css";
import { useAuthStore } from "@/stores/auth";

const app = createApp(App);
const pinia = createPinia();

axios.defaults.withCredentials = true;
axios.defaults.baseURL = "https://api.gym-fit.org";
const token = localStorage.getItem("token");
if (token) {
  axios.defaults.headers.common["Authorization"] = `Bearer ${token}`;
}
app.use(pinia);
const authStore = useAuthStore();
authStore.initializeAuth();
app.use(router);
app.directive("mask", mask);
app.use(Toast, {
  position: "bottom-right",
  timeout: 3000,
  closeOnClick: true,
  pauseOnHover: true,
  draggable: true,
  showCloseButtonOnHover: false,
  hideProgressBar: false,
  icon: true,
  rtl: false,
});
app.mount("#app");
