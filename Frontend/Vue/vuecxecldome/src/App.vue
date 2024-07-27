<template>
  <div id="app">
    <!-- <img alt="Vue logo" src="./assets/logo.png"> -->
    <Edit/>
    <!-- <Execl/> -->
  </div>
</template>

<script>

//import Execl from './components/execl.vue'
import Edit from './components/edit.vue'

export default {
  name: 'App',
  components: {
    //Execl,
    Edit
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
