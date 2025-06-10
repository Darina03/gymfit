import { defineStore } from "pinia";
import { ref, watch } from "vue";
import axios from "axios";
import { jwtDecode } from "jwt-decode";

export const useAuthStore = defineStore("auth", () => {
  const token = ref(localStorage.getItem("token") || "");
  const role = ref(localStorage.getItem("role") || "");
  let logoutTimer = null;

  watch(token, (newToken) => {
    localStorage.setItem("token", newToken);
    if (newToken)
      axios.defaults.headers.common["Authorization"] = `Bearer ${newToken}`;
    else delete axios.defaults.headers.common["Authorization"];
  });

  watch(role, (newRole) => {
    localStorage.setItem("role", newRole);
  });

  const setLogoutTimer = (tokenValue) => {
    try {
      const decoded = jwtDecode(tokenValue);
      const exp = decoded.exp * 1000; // convert to ms
      const timeLeft = exp - Date.now();

      if (logoutTimer) clearTimeout(logoutTimer);
      if (timeLeft > 0) {
        logoutTimer = setTimeout(() => {
          logout();
        }, timeLeft);
      } else {
        logout();
      }
    } catch (err) {
      console.error("Failed to decode token", err);
      logout();
    }
  };

  const login = async (email, password) => {
    try {
      const response = await axios.post("/gymfit/auth/login", {
        email,
        password,
      });
      token.value = response.data.token;
      role.value = response.data.role;
      setLogoutTimer(response.data.token);
    } catch (error) {
      throw error;
    }
  };

  const logout = () => {
    token.value = "";
    role.value = "";
    localStorage.clear();
    if (logoutTimer) clearTimeout(logoutTimer);
    logoutTimer = null;
  };

  const register = async (clientData) => {
    try {
      const response = await axios.post("/gymfit/auth/register", clientData);
      token.value = response.data.token;
      role.value = response.data.role;
      setLogoutTimer(response.data.token);
      return response.data;
    } catch (error) {
      console.error("Registration error:", error);
      throw error;
    }
  };

  const initializeAuth = () => {
    if (token.value) {
      setLogoutTimer(token.value);
    }
  };

  return {
    token,
    role,
    login,
    logout,
    register,
    initializeAuth,
  };
});
