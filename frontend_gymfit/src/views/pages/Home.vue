<template>
  <section class="hero-section">
    <div class="hero-content">
      <div class="text_header">
        <h3 class="welcome mb-0">Сила та здоров'я</h3>
        <p>Почни свій шлях сьогодні</p>
      </div>
      <div class="introduction">
        <p>
          Готові перетворити своє тіло та підвищити рівень енергії? У GymFit ми
          пропонуємо професійне тренерство, сучасне обладнання та підтримуючу
          спільноту, щоб допомогти вам досягти ваших фітнес-цілей. Незалежно від
          того, чи ви лише починаєте, чи вже підвищуєте рівень — тут знайдеться
          місце для кожного. Приєднуйтесь до нас уже сьогодні й відчуйте різницю
          з першого дня. Ваша подорож до сильнішої та здоровішої версії себе
          починається зараз!
        </p>
      </div>
    </div>
  </section>

  <!--  <section class="main-content">-->
  <!--    <h2>About Us</h2>-->
  <!--    <p>Info about the gym...</p>-->
  <!--  </section>-->
</template>

<style lang="scss" scoped>
@use "@/scss/styles" as *;

.hero-section {
  height: 100vh;
  background-image: url("https://images.pexels.com/photos/1229356/pexels-photo-1229356.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2");
  background-size: cover;
  background-position: center;
  position: relative;
  display: flex;
  align-items: flex-start;
  padding-top: 100px;
  justify-content: center;
  color: white;

  &::before {
    content: "";
    position: absolute;
    inset: 0;
    background-color: rgba(0, 0, 0, 0.5);
    z-index: 0;
  }

  .hero-content {
    z-index: 1;
    text-align: left;
    width: 80%;
    padding-top: 5%;

    h3 {
      font-size: 2rem;
      font-family: Impact, Charcoal, sans-serif;
      font-weight: 50;
      color: $fit-highlight;
    }

    .text_header p {
      font-size: 5rem;
      color: #ffffff;
      font-family: Impact, Charcoal, sans-serif;
      line-height: 1;
      border-bottom: 3px solid $fit-highlight;
      padding-bottom: 1rem;
      width: 40%;
    }

    span {
      color: $fit-highlight;
    }

    .introduction p {
      font-size: 1.8rem;
      color: #cccccc;
      font-family: "Modern No. 20", sans-serif;
      width: 65%;
      margin-top: 5%;
      text-align: justify;
    }
  }
}

.main-content {
  padding: 100px 40px;
  background-color: white;
  color: #333;
}
</style>
