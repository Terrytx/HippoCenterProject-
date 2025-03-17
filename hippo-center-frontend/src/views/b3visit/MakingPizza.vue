<template>
  <div id="theForm">
    <fieldset>
      <legend>making pizza</legend>
      <input type="checkbox" v-model="toggleAll" /> All/none
      <hr />
      <p>
        <label>
          <input type="checkbox" v-model="toppings" value="cheese" /> cheese
        </label>
        <label>
          <input type="checkbox" v-model="toppings" value="pepperoni" /> pepperoni
        </label>
        <label>
          <input type="checkbox" v-model="toppings" value="meatballs" /> meatballs
        </label>
        <label>
          <input type="checkbox" v-model="toppings" value="dicedTomatoes" /> diced tomatoes
        </label>
        <label>
          <input type="checkbox" v-model="toppings" value="corn" /> corn
        </label>
        <label>
          <input type="checkbox" v-model="toppings" value="pineapple" /> pineapple
        </label>
        <label>
          <input type="checkbox" v-model="toppings" value="ham" /> ham
        </label>
        <label>
          <input type="checkbox" v-model="toppings" value="mashrooms" /> mashrooms
        </label>
        <label>
          <input type="checkbox" v-model="toppings" value="sausage" /> sausage
        </label>
        <label>
          <input type="checkbox" v-model="toppings" value="greenPeppers" /> green peppers
        </label>
        <label>
          <input type="checkbox" v-model="toppings" value="onions" /> onions
        </label>
      </p>
      <div id="result">
        Selected toppings: {{ toppings.join(", ") || "none" }}
      </div>
    </fieldset>
  </div>
</template>

<script>
import { ref, watch } from 'vue';

export default {
  name: 'CreatePizza',
  setup() {
    const allToppings = [
      'cheese',
      'pepperoni',
      'meatballs',
      'dicedTomatoes',
      'corn',
      'pineapple',
      'ham',
      'mashrooms',
      'sausage',
      'greenPeppers',
      'onions',
    ];
    const toppings = ref([]);
    const toggleAll = ref(false);

    // Watch for toggleAll changes
    watch(toggleAll, (value) => {
      if (value) {
        toppings.value = [...allToppings]; // Select all items
      }
      // No action needed when toggleAll becomes false
    });

    // Watch for toppings changes to update toggleAll
    watch(toppings, (value) => {
      toggleAll.value = value.length === allToppings.length; // Update toggleAll state
    });

    return {
      toppings,
      toggleAll,
    };
  },
};
</script>

<style scoped>
* {
  font: 18px Consolas;
}
body {
  background-color: #ccc;
}
#theForm {
  background-color: #fff;
  border: 1px solid #ddd;
  width: 650px;
  padding: 10px;
  margin: 30px auto;
}
#theForm div {
  margin: 10px 0;
}
#theForm,
button,
legend {
  text-transform: capitalize;
}
</style>
