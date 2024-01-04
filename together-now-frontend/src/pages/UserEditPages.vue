<script setup lang="ts">
import {useRoute} from "vue-router";
import {ref} from "vue";

const route = useRoute();
const editUser = ref({
  editKey: route.query.editKey,
  editName: route.query.editName,
  currentValue: route.query.currentValue,
})

const onSubmit = (values) => {
  // todo 将editName、editKey、currentValue提交到后端

  console.log('submit', values);
};
</script>

<template>
  <van-form @submit="onSubmit">
      <van-field
          v-model="editUser.currentValue"
          :name="editUser.editKey"
          :label="`${editUser.editName}`"
          :placeholder="`请输入${editUser.editName}`"
          :rules="[{ required: true, message: `请输入${editUser.editName}` }]"
      />
    <div style="margin: 16px;">
      <van-button round block type="primary" native-type="submit">
        提交
      </van-button>
    </div>
  </van-form>

</template>

<style scoped>

</style>