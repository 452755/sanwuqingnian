<template>
  <div>
    <vueEditor v-model="editContent"></vueEditor>
  </div>  
</template>

<script>
 import { vueEditor } from 'vue-wangeditor-awesome'

export default {
  data(){
    return {
      editContent:""
    }
  },
  components: {
    vueEditor
  }
}
</script>