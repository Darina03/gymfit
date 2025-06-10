<template>
  <footer class="app-footer">
    <div class="footer-container">
      <div class="footer-section">
        <h2>Про нас</h2>
        <p>GymFit — це спільнота однодумців.</p>
      </div>
      <div class="footer-section">
        <h2>Контакти</h2>
        <p>Email: info@gym.com</p>
        <p>Tel: +380 99 123 4567</p>
      </div>
      <div class="footer-section">
        <h2>Соціальні мережі</h2>
        <div class="social-links">
          <a href="#">Facebook</a>
          <a href="#">Instagram</a>
        </div>
      </div>
    </div>
    <div class="footer-bottom">&copy; 2025 GymFit Copyright</div>
  </footer>
</template>

<style scoped lang="scss">
@use "@/scss/styles" as *;

.app-footer {
  background-color: #121212;
  color: #ffffff;
  width: 100%;
  padding: 2rem 0;

  .footer-container {
    max-width: 1200px;
    width: 100%;
    margin: auto 23%;
    padding: 0 2rem;

    display: flex;
    justify-content: space-between;
    flex-wrap: wrap;
    gap: 2rem;

    .footer-section {
      flex: 1 1 250px;

      h2 {
        font-size: 1.3rem;
        margin-bottom: 0.5rem;
        color: $fit-highlight;
      }

      p {
        font-size: 1.2rem;
        margin: 0.2rem 0;
      }

      .social-links {
        display: flex;
        gap: 1rem;
        margin-top: 0.5rem;

        a {
          color: #aaa;
          font-size: 1.2rem;
          text-decoration: none;
          transition: color 0.3s;

          &:hover {
            color: $fit-highlight;
          }
        }
      }
    }
  }

  .footer-bottom {
    text-align: center;
    margin-top: 2rem;
    font-size: 1rem;
    color: #666;
    border-top: 1px solid #333;
    padding-top: 1rem;
  }
}
</style>
